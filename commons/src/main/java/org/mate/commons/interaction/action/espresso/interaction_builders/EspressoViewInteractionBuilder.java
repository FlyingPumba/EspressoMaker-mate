package org.mate.commons.interaction.action.espresso.interaction_builders;

import org.mate.commons.interaction.action.espresso.EspressoView;
import org.mate.commons.interaction.action.espresso.interactions.InstrumentationTestInteraction;
import org.mate.commons.interaction.action.espresso.interactions.EspressoViewInteraction;
import org.mate.commons.interaction.action.espresso.view_matchers.EspressoViewMatcher;

import java.util.ArrayList;

class EspressoViewInteractionBuilder extends InteractionBuilder {
    @Override
    public ArrayList<InstrumentationTestInteraction> build(EspressoView espressoView, EspressoViewMatcher espressoViewMatcher) {
        ArrayList<InstrumentationTestInteraction> viewInteraction = new ArrayList<>();
        EspressoViewInteraction interaction = new EspressoViewInteraction(espressoViewMatcher);
        interaction.setParametersForView(espressoView);
        viewInteraction.add(interaction);
        return viewInteraction;
    }
}
