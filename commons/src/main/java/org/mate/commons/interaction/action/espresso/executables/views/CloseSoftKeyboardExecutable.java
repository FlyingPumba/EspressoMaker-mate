package org.mate.commons.interaction.action.espresso.executables.views;

import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;

import android.os.Parcel;
import android.view.View;

import androidx.test.espresso.ViewAction;

import org.mate.commons.interaction.action.espresso.executables.InstrumentationTestExecutable;
import org.mate.commons.interaction.action.espresso.executables.InstrumentationTestExecutableType;
import org.mate.commons.interaction.action.espresso.executables.PerformableEspressoViewExecutable;
import org.mate.commons.state.espresso.EspressoScreen;

import java.util.HashSet;
import java.util.Set;

/**
 * Implements a Close soft keyboard Espresso action.
 */
public class CloseSoftKeyboardExecutable extends PerformableEspressoViewExecutable {
    public CloseSoftKeyboardExecutable() {
        super(InstrumentationTestExecutableType.CLOSE_SOFT_KEYBOARD);
    }

    @Override
    public ViewAction getViewAction() {
        return closeSoftKeyboard();
    }

    @Override
    public boolean isValidForEnabledViewInScreen(View view, EspressoScreen espressoScreen) {
        // This action can only be performed on the root view. Also, due to Espresso internal
        // implementation details, this action can only be performed when there is only one
        // Activity in RESUMED stage.
        return getViewAction().getConstraints().matches(view) && isRoot().matches(view) &&
                espressoScreen.getResumedActivities().size() == 1;
    }

    @Override
    public String getPerformableCode() {
        return "closeSoftKeyboard()";
    }

    @Override
    public Set<String> getNeededClassImports() {
        return new HashSet<>();
    }

    @Override
    public Set<String> getNeededStaticImports() {
        Set<String> imports = new HashSet<>();
        imports.add("androidx.test.espresso.action.ViewActions.closeSoftKeyboard");
        return imports;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    public CloseSoftKeyboardExecutable(Parcel in) {
        this();
    }

    public static final Creator<CloseSoftKeyboardExecutable> CREATOR = new Creator<CloseSoftKeyboardExecutable>() {
        @Override
        public CloseSoftKeyboardExecutable createFromParcel(Parcel source) {
            // We need to use the EspressoViewAction.CREATOR here, because we want to make sure
            // to remove the EspressoViewAction's type integer from the beginning of Parcel and call
            // the appropriate constructor for this action.
            // Otherwise, the first integer will be read as data for an instance variable.
            return (CloseSoftKeyboardExecutable) InstrumentationTestExecutable.CREATOR.createFromParcel(source);
        }

        @Override
        public CloseSoftKeyboardExecutable[] newArray(int size) {
            return new CloseSoftKeyboardExecutable[size];
        }
    };
}
