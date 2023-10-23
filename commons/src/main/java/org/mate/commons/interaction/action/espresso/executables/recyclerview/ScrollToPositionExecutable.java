package org.mate.commons.interaction.action.espresso.executables.recyclerview;

import static androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition;

import android.os.Parcel;
import android.view.View;

import androidx.test.espresso.ViewAction;

import org.mate.commons.interaction.action.espresso.executables.InstrumentationTestExecutableType;
import org.mate.commons.interaction.action.espresso.view_matchers.base.IsDisplayedMatcher;
import org.mate.commons.state.espresso.EspressoScreen;

import java.util.HashSet;
import java.util.Set;

/**
 * Implements an Espresso action to scroll to a certain position in a RecyclerView.
 */
public class ScrollToPositionExecutable extends RecyclerViewExecutable {

    private IsDisplayedMatcher isDisplayedMatcher = new IsDisplayedMatcher();

    public ScrollToPositionExecutable() {
        super(InstrumentationTestExecutableType.RECYCLER_SCROLL_TO_POSITION);
    }

    public ScrollToPositionExecutable(int index) {
        super(InstrumentationTestExecutableType.RECYCLER_SCROLL_TO_POSITION);
        this.position = index;
    }

    @Override
    public boolean isValidForEnabledViewInScreen(View view, EspressoScreen espressoScreen) {
        return super.isValidForEnabledViewInScreen(view, espressoScreen) &&
                isDisplayedMatcher.getViewMatcher().matches(view);
    }

    @Override
    public ViewAction getViewAction() {
        return scrollToPosition(position);
    }

    @Override
    protected String getPerformableCode() {
        return String.format("scrollToPosition(%d)", position);
    }

    @Override
    public Set<String> getNeededClassImports() {
        return new HashSet<>();
    }

    @Override
    public Set<String> getNeededStaticImports() {
        Set<String> imports = new HashSet<>();
        imports.add("androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition");
        return imports;
    }

    public ScrollToPositionExecutable(Parcel in) {
        this(in.readInt());
    }
}
