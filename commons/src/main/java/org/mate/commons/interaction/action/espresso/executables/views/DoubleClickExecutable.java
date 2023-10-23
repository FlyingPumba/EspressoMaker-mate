package org.mate.commons.interaction.action.espresso.executables.views;

import static androidx.test.espresso.action.ViewActions.doubleClick;

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
 * Implements a Double click Espresso action.
 */
public class DoubleClickExecutable extends PerformableEspressoViewExecutable {
    public DoubleClickExecutable() {
        super(InstrumentationTestExecutableType.DOUBLE_CLICK);
    }

    @Override
    public ViewAction getViewAction() {
        return doubleClick();
    }

    @Override
    public boolean isValidForEnabledViewInScreen(View view, EspressoScreen espressoScreen) {
        return getViewAction().getConstraints().matches(view);
    }

    @Override
    public String getPerformableCode() {
        return "doubleClick()";
    }

    @Override
    public Set<String> getNeededClassImports() {
        return new HashSet<>();
    }

    @Override
    public Set<String> getNeededStaticImports() {
        Set<String> imports = new HashSet<>();
        imports.add("androidx.test.espresso.action.ViewActions.doubleClick");
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

    public DoubleClickExecutable(Parcel in) {
        this();
    }

    public static final Creator<DoubleClickExecutable> CREATOR = new Creator<DoubleClickExecutable>() {
        @Override
        public DoubleClickExecutable createFromParcel(Parcel source) {
            // We need to use the EspressoViewAction.CREATOR here, because we want to make sure
            // to remove the EspressoViewAction's type integer from the beginning of Parcel and call
            // the appropriate constructor for this action.
            // Otherwise, the first integer will be read as data for an instance variable.
            return (DoubleClickExecutable) InstrumentationTestExecutable.CREATOR.createFromParcel(source);
        }

        @Override
        public DoubleClickExecutable[] newArray(int size) {
            return new DoubleClickExecutable[size];
        }
    };
}
