package org.mate.commons.state.espresso;

import androidx.annotation.Nullable;
import androidx.test.espresso.Root;

import org.mate.commons.interaction.action.espresso.interactions.InstrumentationTestInteraction;
import org.mate.commons.interaction.action.espresso.root_matchers.EspressoRootMatcher;
import org.mate.commons.interaction.action.espresso.view_tree.EspressoViewTree;

import java.util.ArrayList;

public class EspressoWindow {

    private final EspressoViewTree viewTree;

    private final EspressoWindowSummary windowSummary;

    public EspressoWindow(String activityName, Root root) {
        this.viewTree = new EspressoViewTree(root, activityName);
        this.windowSummary = new EspressoWindowSummary(viewTree);
    }

    public EspressoWindowSummary getSummary() {
        return this.windowSummary;
    }

    public @Nullable
    ArrayList<InstrumentationTestInteraction> getInteractions(String uniqueId) {
        return this.windowSummary.getInteractions(uniqueId);
    }

    public EspressoViewTree getViewTree() {
        return this.viewTree;
    }

    public @Nullable
    EspressoRootMatcher getRootMatcher() {
        return this.windowSummary.getRootMatcher();
    }
}
