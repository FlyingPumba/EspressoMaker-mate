package org.mate.commons.interaction.action.espresso.executables.views;

import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

import android.os.Parcel;
import android.view.View;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.uiautomator.UiDevice;

import org.hamcrest.Matcher;
import org.mate.commons.interaction.action.espresso.executables.InstrumentationTestExecutable;
import org.mate.commons.interaction.action.espresso.executables.InstrumentationTestExecutableType;
import org.mate.commons.interaction.action.espresso.executables.PerformableEspressoViewExecutable;
import org.mate.commons.state.espresso.EspressoScreen;

import java.util.HashSet;
import java.util.Set;

/**
 * Implements a Press Home "Espresso" action (we actually end up using the UiAutomator).
 */
public class HomeExecutable extends PerformableEspressoViewExecutable {

    public HomeExecutable() {
        super(InstrumentationTestExecutableType.HOME);
    }

    @Override
    public ViewAction getViewAction() {
        return pressHome();
    }

    @Override
    public boolean isValidForEnabledViewInScreen(View view, EspressoScreen espressoScreen) {
        // This action can only be performed on the root view.
        return isRoot().matches(view);
    }

    @Override
    public String getPerformableCode() {
        return "pressHome()";
    }

    @Override
    public Set<String> getNeededClassImports() {
        return new HashSet<>();
    }

    @Override
    public Set<String> getNeededStaticImports() {
        Set<String> imports = new HashSet<>();
        imports.add("org.mate.espresso.tests.TestUtils.pressHome");
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

    public HomeExecutable(Parcel in) {
        this();
    }

    public static final Creator<HomeExecutable> CREATOR = new Creator<HomeExecutable>() {
        @Override
        public HomeExecutable createFromParcel(Parcel source) {
            // We need to use the EspressoViewAction.CREATOR here, because we want to make sure
            // to remove the EspressoViewAction's type integer from the beginning of Parcel and call
            // the appropriate constructor for this action.
            // Otherwise, the first integer will be read as data for an instance variable.
            return (HomeExecutable) InstrumentationTestExecutable.CREATOR.createFromParcel(source);
        }

        @Override
        public HomeExecutable[] newArray(int size) {
            return new HomeExecutable[size];
        }
    };

    public static ViewAction pressHome() {
        return new PressHomeViewAction();
    }

    static final class PressHomeViewAction implements ViewAction {

        @Override
        public Matcher<View> getConstraints() {
            return isRoot();
        }

        @Override
        public String getDescription() {
            return "press home";
        }

        @Override
        public void perform(UiController uiController, View view) {
            UiDevice.getInstance(getInstrumentation()).pressHome();
        }
    }
}
