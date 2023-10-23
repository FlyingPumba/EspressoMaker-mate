package org.mate.commons.interaction.action.espresso.interaction_builders;

import android.widget.AdapterView;

import org.mate.commons.interaction.action.espresso.EspressoView;
import org.mate.commons.interaction.action.espresso.interactions.EspressoDataInteraction;
import org.mate.commons.interaction.action.espresso.interactions.InstrumentationTestInteraction;
import org.mate.commons.interaction.action.espresso.view_matchers.EspressoViewMatcher;

import java.util.ArrayList;

class EspressoDataInteractionBuilder extends InteractionBuilder {
    @Override
    public ArrayList<InstrumentationTestInteraction> build(EspressoView espressoView, EspressoViewMatcher espressoViewMatcher) {
        ArrayList<InstrumentationTestInteraction> dataInteractions = new ArrayList<>();

        if (espressoView.isAdapterView()) {
            AdapterView adapter = (AdapterView) espressoView.getView();
            int itemCount = adapter.getCount();

            if (itemCount > 0) {
                // only build an EspressoDataInteraction if the AdapterView has at least one element.
                InstrumentationTestInteraction interaction = new EspressoDataInteraction((espressoViewMatcher));
                interaction.setParametersForView(espressoView);
                dataInteractions.add(interaction);
            }
        }

        return dataInteractions;
    }
}
