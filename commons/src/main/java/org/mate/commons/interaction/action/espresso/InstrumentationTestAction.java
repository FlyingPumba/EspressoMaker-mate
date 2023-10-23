package org.mate.commons.interaction.action.espresso;

import android.os.Parcel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.mate.commons.exceptions.AUTCrashException;
import org.mate.commons.interaction.action.Action;
import org.mate.commons.interaction.action.espresso.executables.InstrumentationTestExecutable;
import org.mate.commons.interaction.action.espresso.interactions.InstrumentationTestInteraction;
import org.mate.commons.interaction.action.espresso.root_matchers.EspressoRootMatcher;
import org.mate.commons.utils.CodeProducer;
import org.mate.commons.utils.MATELog;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * An InstrumentationTest action is an action that will be executed in an Android Instrumentation test.
 * It is composed of an Interaction (which defines the target view) and a Executable (the action to perform on the
 * target view).
 */
public class InstrumentationTestAction extends Action implements CodeProducer {

    /**
     * The actual action to perform on the target view (e.g., click, long click, etc.)
     */
    private final InstrumentationTestExecutable executable;

    /**
     * The root matcher to indicate Espresso on which Root to find the target view.
     */
    private @Nullable
    final EspressoRootMatcher espressoRootMatcher;

    /**
     * The interaction where to perform the action.
     */
    private final InstrumentationTestInteraction interaction;

    public InstrumentationTestAction(InstrumentationTestExecutable executable,
                                  InstrumentationTestInteraction interaction,
                                  @Nullable EspressoRootMatcher espressoRootMatcher) {
        this.executable = executable;
        this.interaction = interaction;

        // Use a root matcher only if the ViewAction allows it.
        if (executable.allowsRootMatcher()) {
            this.espressoRootMatcher = espressoRootMatcher;
        } else {
            this.espressoRootMatcher = null;
        }
    }

    /**
     * @return The executable that will be performed on the target view.
     */
    public InstrumentationTestExecutable getExecutable() {
        return executable;
    }

    /**
     * Executes this InstrumentationTest action. Exceptions are cached so that they do not bubble up and crash the
     * Representation Layer module.
     * @return whether the action was executed successfully or not.
     */
    public boolean execute(String targetPackageName) throws AUTCrashException {
        try {
            if (espressoRootMatcher != null) {
                interaction.inRoot(espressoRootMatcher.getRootMatcher());
            }
            interaction.perform(executable);
            return true;
        } catch (Exception e) {
            checkForSoftAUTCrash(e, targetPackageName);
            MATELog.log_warn("Unable to execute InstrumentationTestAction: " + e.getMessage());
        }

        return false;
    }

    /**
     * This method explicitly checks if the event execution failed due to a bug in the AUT.
     * If so, the exception is rethrown as an Error so that the AUT process crashes, and the event
     * that led to it is detected as an actual crash in the MATE Client.
     * @param e the exception to check
     * @param targetPackageName the package name of the AUT.
     */
    private void checkForSoftAUTCrash(Exception e, String targetPackageName) throws AUTCrashException {
        Throwable currentException = e;

        while (currentException != null) {
            StackTraceElement[] stackTrace = currentException.getStackTrace();
            for (StackTraceElement stackTraceElement : stackTrace) {
                if (stackTraceElement.getClassName().startsWith(targetPackageName)) {
                    throw new AUTCrashException("Found soft crash caused by AUT: " +
                            currentException.getMessage(), e);
                }
            }

            if (currentException.getCause() != null && currentException.getCause() != currentException) {
                currentException = currentException.getCause();
            } else {
                currentException = null;
            }
        }
    }

    @Override
    public String getCode() {
        String espressoInteractionCode = interaction.getCode();
        String viewActionCode = executable.getCode();
        String rootMatcherCode = "";
        if (espressoRootMatcher != null) {
            rootMatcherCode = String.format(".inRoot(%s)", espressoRootMatcher.getCode());
        }
        return String.format("%s%s.%s",
                espressoInteractionCode,
                rootMatcherCode, viewActionCode);
    }

    @Override
    public Set<String> getNeededClassImports() {
        Set<String> imports = new HashSet<>();

        imports.addAll(interaction.getNeededClassImports());
        imports.addAll(executable.getNeededClassImports());

        if (espressoRootMatcher != null) {
            imports.addAll(espressoRootMatcher.getNeededClassImports());
        }

        return imports;
    }

    @Override
    public Set<String> getNeededStaticImports() {
        Set<String> imports = new HashSet<>();

        imports.addAll(executable.getNeededStaticImports());
        imports.addAll(interaction.getNeededStaticImports());

        if (espressoRootMatcher != null) {
            imports.addAll(espressoRootMatcher.getNeededStaticImports());
        }

        return imports;
    }

    /**
     * Compares two InstrumentationTest actions for equality.
     *
     * @param o The object to which we compare.
     * @return Returns {@code true} if both actions are equal,
     * otherwise {@code false} is returned.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o == null || getClass() != o.getClass()) {
            return false;
        } else {
            InstrumentationTestAction other = (InstrumentationTestAction) o;
            return executable == other.executable && interaction.equals(other.interaction)
                    && Objects.equals(espressoRootMatcher, other.espressoRootMatcher);
        }
    }

    /**
     * Computes the hash code based on attributes used for {@link #equals(Object)}.
     *
     * @return Returns the associated hash code of the InstrumentationTest action.
     */
    @Override
    public int hashCode() {
        return Objects.hash(executable, interaction, espressoRootMatcher);
    }

    /**
     * The string representation of the InstrumentationTest action.
     */
    @NonNull
    @Override
    public String toString() {
        return getCode();
    }

    @NonNull
    @Override
    public String toShortString() {
        return toString();
    }

    @Override
    public int getIntForActionSubClass() {
        return ACTION_SUBCLASS_INSTRUMENTED_TEST;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(this.executable, flags);
        dest.writeParcelable(this.interaction, flags);
        dest.writeParcelable(this.espressoRootMatcher, flags);
    }

    public InstrumentationTestAction(Parcel in) {
        super(in);
        this.executable = in.readParcelable(InstrumentationTestExecutable.class.getClassLoader());
        this.interaction = in.readParcelable(InstrumentationTestInteraction.class.getClassLoader());
        this.espressoRootMatcher = in.readParcelable(EspressoRootMatcher.class.getClassLoader());
    }

    public static final Creator<InstrumentationTestAction> CREATOR = new Creator<InstrumentationTestAction>() {
        @Override
        public InstrumentationTestAction createFromParcel(Parcel source) {
            // We need to use the Action.CREATOR here, because we want to make sure to remove the
            // ActionSubClass integer from the beginning of Parcel and call the appropriate
            // constructor for this action.
            // Otherwise, the first integer will be read as data for an instance variable.
            return (InstrumentationTestAction) Action.CREATOR.createFromParcel(source);
        }

        @Override
        public InstrumentationTestAction[] newArray(int size) {
            return new InstrumentationTestAction[size];
        }
    };
}
