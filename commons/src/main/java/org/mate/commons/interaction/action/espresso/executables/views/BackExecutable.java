package org.mate.commons.interaction.action.espresso.executables.views;

import static androidx.test.espresso.action.ViewActions.pressBackUnconditionally;
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
 * Implements a Back Espresso action.
 */
public class BackExecutable extends PerformableEspressoViewExecutable {
    public BackExecutable() {
        super(InstrumentationTestExecutableType.BACK);
    }

    @Override
    public ViewAction getViewAction() {
        return pressBackUnconditionally();
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
        return "pressBackUnconditionally()";
    }

    @Override
    public Set<String> getNeededClassImports() {
        return new HashSet<>();
    }

    @Override
    public Set<String> getNeededStaticImports() {
        Set<String> imports = new HashSet<>();
        imports.add("androidx.test.espresso.action.ViewActions.pressBackUnconditionally");
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

    public BackExecutable(Parcel in) {
        this();
    }

    public static final Creator<BackExecutable> CREATOR = new Creator<BackExecutable>() {
        @Override
        public BackExecutable createFromParcel(Parcel source) {
            // We need to use the EspressoViewAction.CREATOR here, because we want to make sure
            // to remove the EspressoViewAction's type integer from the beginning of Parcel and call
            // the appropriate constructor for this action.
            // Otherwise, the first integer will be read as data for an instance variable.
            return (BackExecutable) InstrumentationTestExecutable.CREATOR.createFromParcel(source);
        }

        @Override
        public BackExecutable[] newArray(int size) {
            return new BackExecutable[size];
        }
    };
}
