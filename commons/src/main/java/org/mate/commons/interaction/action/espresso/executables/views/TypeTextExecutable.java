package org.mate.commons.interaction.action.espresso.executables.views;

import static androidx.test.espresso.action.ViewActions.typeText;

import android.os.Parcel;
import android.view.View;

import androidx.test.espresso.ViewAction;

import org.mate.commons.interaction.action.espresso.executables.InstrumentationTestExecutable;
import org.mate.commons.interaction.action.espresso.executables.InstrumentationTestExecutableType;
import org.mate.commons.interaction.action.espresso.executables.PerformableEspressoViewExecutable;
import org.mate.commons.state.espresso.EspressoScreen;
import org.mate.commons.utils.MATELog;

import java.util.HashSet;
import java.util.Set;

/**
 * Implements a Type text Espresso action.
 */
public class TypeTextExecutable extends PerformableEspressoViewExecutable {

    /**
     * The string to be typed by this action.
     */
    private String stringToBeTyped;

    public TypeTextExecutable(String stringToBeTyped) {
        super(InstrumentationTestExecutableType.TYPE_TEXT);
        setText(stringToBeTyped);
    }

    /**
     * Saves the string to be typed by this action.
     *
     * @param stringToBeTyped a string
     */
    public void setText(String stringToBeTyped) {
        this.stringToBeTyped = stringToBeTyped;

        if (!stringToBeTyped.endsWith("\n")) {
            // Appending a \n to the end of the string translates to a ENTER key event.
            this.stringToBeTyped += "\n";
        }
    }

    @Override
    public ViewAction getViewAction() {
        return typeText(stringToBeTyped);
    }

    @Override
    public boolean isValidForEnabledViewInScreen(View view, EspressoScreen espressoScreen) {
        try {
            return getViewAction().getConstraints().matches(view);
        } catch (Exception e) {
            MATELog.log_warn("Unable to evaluate if view can be edited: " + e.getMessage());
            return false;
        }
    }

    @Override
    public String getPerformableCode() {
        return String.format("typeText(%s)", boxString(stringToBeTyped));
    }

    @Override
    public Set<String> getNeededClassImports() {
        return new HashSet<>();
    }

    @Override
    public Set<String> getNeededStaticImports() {
        Set<String> imports = new HashSet<>();
        imports.add("androidx.test.espresso.action.ViewActions.typeText");
        return imports;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.stringToBeTyped);
    }

    public TypeTextExecutable(Parcel in) {
        this(in.readString());
    }

    public static final Creator<TypeTextExecutable> CREATOR = new Creator<TypeTextExecutable>() {
        @Override
        public TypeTextExecutable createFromParcel(Parcel source) {
            // We need to use the EspressoViewAction.CREATOR here, because we want to make sure
            // to remove the EspressoViewAction's type integer from the beginning of Parcel and call
            // the appropriate constructor for this action.
            // Otherwise, the first integer will be read as data for an instance variable.
            return (TypeTextExecutable) InstrumentationTestExecutable.CREATOR.createFromParcel(source);
        }

        @Override
        public TypeTextExecutable[] newArray(int size) {
            return new TypeTextExecutable[size];
        }
    };
}
