package org.mate.commons.interaction.action.espresso.interaction_builders;

import org.mate.commons.interaction.action.espresso.EspressoView;
import org.mate.commons.interaction.action.espresso.interactions.InstrumentationTestInteraction;
import org.mate.commons.interaction.action.espresso.view_matchers.EspressoViewMatcher;

import java.util.ArrayList;
import java.util.List;

/**
 * Builder class to be used at the creation of interactions.
 * Abstracts from determining how to create the interactions depending on the particular view.
 */
public abstract class InteractionBuilder {

    /**
     * Builds the list of UiInteractions for current view.
     * Generally, a list with more than one element will be produced by ComposeViews.
     */
    public abstract ArrayList<InstrumentationTestInteraction> build(EspressoView espressoView,
                                                                    EspressoViewMatcher espressoViewMatcher);
    /**
     * Static method to determine which Builders we may need depending on the view.
     */
    public static List<InteractionBuilder> of(EspressoView espressoView) {
        List<InteractionBuilder> builders = new ArrayList<>();

        if (espressoView.isComposeView()) {
            builders.add(new UiDeviceInteractionBuilder());
        }

        if (espressoView.isAdapterView()) {
            builders.add(new EspressoDataInteractionBuilder());
        }

        builders.add(new EspressoViewInteractionBuilder());

        return builders;
    }
}
