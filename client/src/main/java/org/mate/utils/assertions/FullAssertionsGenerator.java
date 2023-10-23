package org.mate.utils.assertions;

import org.mate.Registry;
import org.mate.commons.interaction.action.espresso.EspressoAssertion;
import org.mate.commons.interaction.action.espresso.assertions.EspressoAssertionsFactory;
import org.mate.commons.interaction.action.espresso.interactions.EspressoDataInteraction;
import org.mate.commons.interaction.action.espresso.interactions.InstrumentationTestInteraction;
import org.mate.commons.interaction.action.espresso.interactions.UiDeviceInteraction;
import org.mate.commons.interaction.action.espresso.root_matchers.EspressoRootMatcher;
import org.mate.commons.state.espresso.EspressoScreenSummary;
import org.mate.commons.utils.MATELog;
import org.mate.interaction.UIAbstractionLayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class FullAssertionsGenerator {

    /**
     * The UI abstraction layer to use during test cases re-execution.
     */
    private final UIAbstractionLayer uiAbstractionLayer;

    public FullAssertionsGenerator() {
        this.uiAbstractionLayer = Registry.getUiAbstractionLayer();
    }

    /**
     * Generate assertions for a new Espresso ViewTree.
     * @return a list of assertions.
     */
    public List<EspressoAssertion> generate() {
        List<EspressoAssertion> assertions = new ArrayList<>();

        EspressoScreenSummary espressoScreen =
                uiAbstractionLayer.getLastScreenState().getEspressoScreenSummary();

        if (espressoScreen == null) {
            // An error occurred while fetching the information from current screen
            MATELog.log_debug("Unable to fetch Espresso Screen Summary when generating assertions.");
        }

        addAllAssertions(espressoScreen, assertions);

        return assertions;
    }

    private void addAllAssertions(@Nullable EspressoScreenSummary espressoScreen,
                                  List<EspressoAssertion> assertions) {
        if (espressoScreen == null) {
            // we do not have enough information to generate assertions.
            MATELog.log_debug("Unable to generate full assertions: EspressoScreenSummary is null.");
            return;
        }

        // We generate assertions for all views on screen.
        Map<String, ArrayList<InstrumentationTestInteraction>> interactions =
                espressoScreen.getInteractionsInTopWindow();

        for (Map.Entry<String, ArrayList<InstrumentationTestInteraction>> entry : interactions.entrySet()) {
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

                // Assert that the view exists on the screen
                List<EspressoAssertion> assertionCandidates = EspressoAssertionsFactory.viewHasAppeared(interaction,
                        rootMatcher, espressoScreen.getUiAttributes(viewUniqueId));
                addSuccessfulAssertions(assertions, assertionCandidates);

                // Assert the attributes of the view
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

    private void addSuccessfulAssertions(List<EspressoAssertion> assertions, List<EspressoAssertion> assertionCandidates) {
        for (EspressoAssertion assertion : assertionCandidates) {
            // We try out the assertion and check if it is valid before adding it to the list.
            boolean successful = uiAbstractionLayer.executeAssertion(assertion);
            if (successful) {
                assertions.add(assertion);
            } else {
                MATELog.log_debug("Discarded Espresso assertion candidate: " + assertion.toString());
            }
        }
    }
}
