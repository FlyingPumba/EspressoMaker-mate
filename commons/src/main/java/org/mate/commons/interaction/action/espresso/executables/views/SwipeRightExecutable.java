package org.mate.commons.interaction.action.espresso.executables.views;

import static androidx.test.espresso.action.ViewActions.swipeRight;

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
 * Implements a Swipe right Espresso action.
 */
public class SwipeRightExecutable extends PerformableEspressoViewExecutable {
    public SwipeRightExecutable() {
        super(InstrumentationTestExecutableType.SWIPE_RIGHT);
    }

    @Override
    public ViewAction getViewAction() {
        return swipeRight();
    }

    @Override
    public boolean isValidForEnabledViewInScreen(View view, EspressoScreen espressoScreen) {
        if (!view.canScrollHorizontally(-1)) {
            return false;
        }

        return getViewAction().getConstraints().matches(view);
    }

    @Override
    public String getPerformableCode() {
        return "swipeRight()";
    }

    @Override
    public Set<String> getNeededClassImports() {
        return new HashSet<>();
    }

    @Override
    public Set<String> getNeededStaticImports() {
        Set<String> imports = new HashSet<>();
        imports.add("androidx.test.espresso.action.ViewActions.swipeRight");
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

    public SwipeRightExecutable(Parcel in) {
        this();
    }

    public static final Creator<SwipeRightExecutable> CREATOR = new Creator<SwipeRightExecutable>() {
        @Override
        public SwipeRightExecutable createFromParcel(Parcel source) {
            // We need to use the EspressoViewAction.CREATOR here, because we want to make sure
            // to remove the EspressoViewAction's type integer from the beginning of Parcel and call
            // the appropriate constructor for this action.
            // Otherwise, the first integer will be read as data for an instance variable.
            return (SwipeRightExecutable) InstrumentationTestExecutable.CREATOR.createFromParcel(source);
        }

        @Override
        public SwipeRightExecutable[] newArray(int size) {
            return new SwipeRightExecutable[size];
        }
    };
}
