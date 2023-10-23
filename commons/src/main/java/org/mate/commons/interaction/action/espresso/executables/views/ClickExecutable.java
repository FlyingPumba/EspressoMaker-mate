package org.mate.commons.interaction.action.espresso.executables.views;

import static androidx.test.espresso.action.ViewActions.click;

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
 * Implements a Click Espresso action.
 */
public class ClickExecutable extends PerformableEspressoViewExecutable {
    public ClickExecutable() {
        super(InstrumentationTestExecutableType.CLICK);
    }

    @Override
    public ViewAction getViewAction() {
        return click();
    }

    @Override
    public boolean isValidForEnabledViewInScreen(View view, EspressoScreen espressoScreen) {
        return getViewAction().getConstraints().matches(view);
    }

    @Override
    public String getPerformableCode() {
        return "click()";
    }

    @Override
    public Set<String> getNeededClassImports() {
        return new HashSet<>();
    }

    @Override
    public Set<String> getNeededStaticImports() {
        Set<String> imports = new HashSet<>();
        imports.add("androidx.test.espresso.action.ViewActions.click");
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

    public ClickExecutable(Parcel in) {
        this();
    }

    public static final Creator<ClickExecutable> CREATOR = new Creator<ClickExecutable>() {
        @Override
        public ClickExecutable createFromParcel(Parcel source) {
            // We need to use the EspressoViewAction.CREATOR here, because we want to make sure
            // to remove the EspressoViewAction's type integer from the beginning of Parcel and call
            // the appropriate constructor for this action.
            // Otherwise, the first integer will be read as data for an instance variable.
            return (ClickExecutable) InstrumentationTestExecutable.CREATOR.createFromParcel(source);
        }

        @Override
        public ClickExecutable[] newArray(int size) {
            return new ClickExecutable[size];
        }
    };
}
