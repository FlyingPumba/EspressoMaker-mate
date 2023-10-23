package org.mate.commons.interaction.action.espresso;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import junit.framework.AssertionFailedError;

import org.mate.commons.interaction.action.espresso.assertions.EspressoViewAssertion;
import org.mate.commons.interaction.action.espresso.interactions.InstrumentationTestInteraction;
import org.mate.commons.interaction.action.espresso.root_matchers.EspressoRootMatcher;
import org.mate.commons.utils.CodeProducer;
import org.mate.commons.utils.MATELog;
import org.mate.commons.utils.TimeoutRun;

import java.util.HashSet;
import java.util.Set;

/**
 * An Espresso assertion is an assertion that will be executed using the Espresso testing framework.
 * It is composed of a ViewMatcher (that tells Espresso which is the target view) and a
 * ViewAssertion (that tels Espresso what assertion to check on the target view).
 * <p>
 * Note: do not confuse this class (EspressoAssertion) with the EspressoViewAssertion class.
 * The latter is used for representing the actual Espresso ViewAssertion instances (e.g.,
 * isDisplayed()) and can not be used without having an appropriate ViewMatcher (e.g., on which
 * view to perform the assertion).
 */
public class EspressoAssertion implements CodeProducer, Parcelable {

    /**
     * The timeout for executing the Espresso assertions (in milliseconds).
     */
    public static final int ASSERTION_EXECUTION_TIMEOUT = 500;

    /**
     * The selector to indicate Espresso the target view.
     */
    private final InstrumentationTestInteraction interaction;

    /**
     * The actual assertion to check on the target view (e.g., isDisplayed, isClickable, etc.)
     */
    private final EspressoViewAssertion espressoViewAssertion;

    /**
     * The root matcher to indicate Espresso on which Root to find the target view.
     */
    @Nullable
    private final EspressoRootMatcher espressoRootMatcher;

    public EspressoAssertion(InstrumentationTestInteraction interaction,
                             EspressoViewAssertion espressoViewAssertion,
                             @Nullable EspressoRootMatcher espressoRootMatcher) {
        this.interaction = interaction;
        this.espressoViewAssertion = espressoViewAssertion;
        this.espressoRootMatcher = espressoRootMatcher;
    }

    @Override
    public String getCode() {
        String interactionCode = interaction.getCode();
        String viewAssertionCode = espressoViewAssertion.getCode();

        String rootMatcherCode = "";
        if (espressoRootMatcher != null) {
            rootMatcherCode = String.format(".inRoot(%s)", espressoRootMatcher.getCode());
        }

        String code = String.format("%s%s.check(%s)",
                interactionCode,
                rootMatcherCode, viewAssertionCode);

        return code;
    }

    /**
     * Execute the Espresso assertion in a safe way.
     * @return true if the assertion is valid for the current UI. False, otherwise.
     */
    public boolean execute() {
        final boolean[] result = {false};

        TimeoutRun.timeoutRun(() -> {
            try {
                if (espressoRootMatcher != null) {
                    interaction.inRoot(espressoRootMatcher.getRootMatcher());
                }
                interaction.check(espressoViewAssertion);
                result[0] = true;
            } catch (AssertionFailedError | Exception e) {
                MATELog.log_warn("EspressoAssertion execution failed: " + e.getMessage());
            }
            return null;
        }, ASSERTION_EXECUTION_TIMEOUT);

        return result[0];
    }

    @Override
    public Set<String> getNeededClassImports() {
        Set<String> imports = new HashSet<>();

        imports.addAll(interaction.getNeededClassImports());
        imports.addAll(espressoViewAssertion.getNeededClassImports());

        if (espressoRootMatcher != null) {
            imports.addAll(espressoRootMatcher.getNeededClassImports());
        }

        return imports;
    }

    @Override
    public Set<String> getNeededStaticImports() {
        Set<String> imports = new HashSet<>();
        imports.add("androidx.test.espresso.Espresso.onView");

        imports.addAll(interaction.getNeededStaticImports());
        imports.addAll(espressoViewAssertion.getNeededStaticImports());

        if (espressoRootMatcher != null) {
            imports.addAll(espressoRootMatcher.getNeededStaticImports());
        }

        return imports;
    }

    @Override
    public String toString() {
        return this.getCode();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.interaction, flags);
        dest.writeParcelable(this.espressoViewAssertion, flags);
        dest.writeParcelable(this.espressoRootMatcher, flags);
    }

    protected EspressoAssertion(Parcel in) {
        this.interaction = in.readParcelable(InstrumentationTestInteraction.class.getClassLoader());
        this.espressoViewAssertion = in.readParcelable(EspressoViewAssertion.class.getClassLoader());
        this.espressoRootMatcher = in.readParcelable(EspressoRootMatcher.class.getClassLoader());
    }

    public static final Creator<EspressoAssertion> CREATOR = new Creator<EspressoAssertion>() {
        @Override
        public EspressoAssertion createFromParcel(Parcel source) {
            return new EspressoAssertion(source);
        }

        @Override
        public EspressoAssertion[] newArray(int size) {
            return new EspressoAssertion[size];
        }
    };
}
