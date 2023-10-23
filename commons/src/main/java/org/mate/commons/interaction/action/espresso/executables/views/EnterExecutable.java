package org.mate.commons.interaction.action.espresso.executables.views;

import static android.view.KeyEvent.KEYCODE_ENTER;
import static androidx.test.espresso.action.ViewActions.pressKey;
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
 * Implements an Enter key Espresso action.
 */
public class EnterExecutable extends PerformableEspressoViewExecutable {
    public EnterExecutable() {
        super(InstrumentationTestExecutableType.ENTER);
    }

    @Override
    public ViewAction getViewAction() {
        return pressKey(KEYCODE_ENTER);
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
        return "pressKey(KEYCODE_ENTER)";
    }

    @Override
    public Set<String> getNeededClassImports() {
        return new HashSet<>();
    }

    @Override
    public Set<String> getNeededStaticImports() {
        Set<String> imports = new HashSet<>();
        imports.add("android.view.KeyEvent.KEYCODE_ENTER");
        imports.add("androidx.test.espresso.action.ViewActions.pressKey");
        return imports;
    }

    @Override
    public boolean allowsRootMatcher() {
        // This action is independent of the window on which it is performed.
        return false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    public EnterExecutable(Parcel in) {
        this();
    }

    public static final Creator<EnterExecutable> CREATOR = new Creator<EnterExecutable>() {
        @Override
        public EnterExecutable createFromParcel(Parcel source) {
            // We need to use the EspressoViewAction.CREATOR here, because we want to make sure
            // to remove the EspressoViewAction's type integer from the beginning of Parcel and call
            // the appropriate constructor for this action.
            // Otherwise, the first integer will be read as data for an instance variable.
            return (EnterExecutable) InstrumentationTestExecutable.CREATOR.createFromParcel(source);
        }

        @Override
        public EnterExecutable[] newArray(int size) {
            return new EnterExecutable[size];
        }
    };
}
