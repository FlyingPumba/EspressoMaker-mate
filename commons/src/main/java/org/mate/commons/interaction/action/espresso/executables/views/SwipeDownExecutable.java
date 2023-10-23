package org.mate.commons.interaction.action.espresso.executables.views;

import static androidx.test.espresso.action.ViewActions.swipeDown;

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
 * Implements a Swipe down Espresso action.
 */
public class SwipeDownExecutable extends PerformableEspressoViewExecutable {
    public SwipeDownExecutable() {
        super(InstrumentationTestExecutableType.SWIPE_DOWN);
    }

    @Override
    public ViewAction getViewAction() {
        return swipeDown();
    }

    @Override
    public boolean isValidForEnabledViewInScreen(View view, EspressoScreen espressoScreen) {
        if (!view.canScrollVertically(1)) {
            return false;
        }

        return getViewAction().getConstraints().matches(view);
    }

    @Override
    public String getPerformableCode() {
        return "swipeDown()";
    }

    @Override
    public Set<String> getNeededClassImports() {
        return new HashSet<>();
    }

    @Override
    public Set<String> getNeededStaticImports() {
        Set<String> imports = new HashSet<>();
        imports.add("androidx.test.espresso.action.ViewActions.swipeDown");
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

    public SwipeDownExecutable(Parcel in) {
        this();
    }

    public static final Creator<SwipeDownExecutable> CREATOR = new Creator<SwipeDownExecutable>() {
        @Override
        public SwipeDownExecutable createFromParcel(Parcel source) {
            // We need to use the EspressoViewAction.CREATOR here, because we want to make sure
            // to remove the EspressoViewAction's type integer from the beginning of Parcel and call
            // the appropriate constructor for this action.
            // Otherwise, the first integer will be read as data for an instance variable.
            return (SwipeDownExecutable) InstrumentationTestExecutable.CREATOR.createFromParcel(source);
        }

        @Override
        public SwipeDownExecutable[] newArray(int size) {
            return new SwipeDownExecutable[size];
        }
    };
}
