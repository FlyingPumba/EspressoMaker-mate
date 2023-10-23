package org.mate.commons.interaction.action.espresso.executables.recyclerview;

import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;

import android.os.Parcel;
import android.view.View;

import androidx.test.espresso.ViewAction;

import org.mate.commons.interaction.action.espresso.executables.InstrumentationTestExecutableType;
import org.mate.commons.interaction.action.espresso.executables.views.ClickExecutable;
import org.mate.commons.interaction.action.espresso.view_matchers.base.IsDisplayedMatcher;
import org.mate.commons.state.espresso.EspressoScreen;

import java.util.HashSet;
import java.util.Set;

/**
 * Implements an Espresso action to click on a certain position in a RecyclerView.
 */
public class ClickOnPositionExecutable extends RecyclerViewExecutable {

    private ClickExecutable clickExecutable = new ClickExecutable();
    private IsDisplayedMatcher isDisplayedMatcher = new IsDisplayedMatcher();

    public ClickOnPositionExecutable() {
        super(InstrumentationTestExecutableType.RECYCLER_CLICK_ON_POSITION);
    }

    public ClickOnPositionExecutable(int position) {
        super(InstrumentationTestExecutableType.RECYCLER_CLICK_ON_POSITION);
        this.position = position;
    }

    @Override
    public boolean isValidForEnabledViewInScreen(View view, EspressoScreen espressoScreen) {
        return super.isValidForEnabledViewInScreen(view, espressoScreen) &&
                isDisplayedMatcher.getViewMatcher().matches(view);
    }

    @Override
    public ViewAction getViewAction() {
        return actionOnItemAtPosition(this.position, clickExecutable.getViewAction());
    }

    @Override
    protected String getPerformableCode() {
        return String.format("actionOnItemAtPosition(%d, %s)", position,
                clickExecutable.getPerformableCode());
    }

    @Override
    public Set<String> getNeededClassImports() {
        return new HashSet<>();
    }

    @Override
    public Set<String> getNeededStaticImports() {
        Set<String> imports = new HashSet<>();
        imports.add("androidx.test.espresso.action.ViewActions.click");
        imports.add("androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition");
        return imports;
    }

    public ClickOnPositionExecutable(Parcel in) {
        this(in.readInt());
    }
}
