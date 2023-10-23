package org.mate.utils.assertions;

import org.mate.commons.interaction.action.espresso.EspressoAssertion;
import org.mate.commons.interaction.action.espresso.assertions.EspressoAssertionsFactory;
import org.mate.commons.interaction.action.espresso.interactions.EspressoDataInteraction;
import org.mate.commons.interaction.action.espresso.interactions.InstrumentationTestInteraction;
import org.mate.commons.interaction.action.espresso.interactions.UiDeviceInteraction;
import org.mate.commons.interaction.action.espresso.root_matchers.EspressoRootMatcher;
import org.mate.commons.state.espresso.EspressoScreenSummary;
import org.mate.commons.utils.MATELog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class SemiFullAssertionsGenerator extends DiffAssertionsGenerator {

    public SemiFullAssertionsGenerator() {
        super();
    }

    @Override
    protected void addAssertionsForAppearingViews(@Nullable EspressoScreenSummary espressoScreen,
                                                List<EspressoAssertion> assertions) {
        if (espressoScreen == null) {
            // we do not have enough information to generate assertions for appearing views.
            MATELog.log_debug("Unable to generate semifull assertions for appearing views.");
            return;
        }

        Map<String, ArrayList<InstrumentationTestInteraction>> appearingViews;

        if (lastEspressoScreen == null) {
            // this is the first screen we fetch, so we generate assertions for all views
            appearingViews = espressoScreen.getInteractionsInTopWindow();
        } else {
            // this is not the first screen we fetch, so we generate assertions only for new views
            appearingViews = espressoScreen.getAppearingInteractions(lastEspressoScreen);
        }

        for (Map.Entry<String, ArrayList<InstrumentationTestInteraction>> entry : appearingViews.entrySet()) {
            String viewUniqueId = entry.getKey();
            for (InstrumentationTestInteraction interaction : entry.getValue()) {
                if (interaction instanceof UiDeviceInteraction) {
                    // we do not generate assertions for UiDevice interactions (not supported).
                    continue;
                }
                if (interaction instanceof EspressoDataInteraction) {
                    // We do not generate assertions for Espresso DataInteractions.
                    // They alter the UI while we try them out, independently of whether they succeed or fail.
                    continue;
                }

                EspressoRootMatcher rootMatcher = espressoScreen.getRootMatcher(viewUniqueId);

                // Assert that the view has appeared
                List<EspressoAssertion> assertionCandidates = EspressoAssertionsFactory.viewHasAppeared(interaction,
                        rootMatcher, espressoScreen.getUiAttributes(viewUniqueId));
                addSuccessfulAssertions(assertions, assertionCandidates);

                // Assert the attributes of the appearing view
                Map<String, String> attributes = espressoScreen.getUiAttributes(viewUniqueId);
                for (String attrKey : attributes.keySet()) {
                    String attrValue = attributes.get(attrKey);
                    assertionCandidates = EspressoAssertionsFactory.viewHasChanged(interaction,
                            rootMatcher, attrKey, null, attrValue);
                    addSuccessfulAssertions(assertions, assertionCandidates);
                }
            }
        }
    }
}
