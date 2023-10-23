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

public class DiffAssertionsGenerator {

    /**
     * The UI abstraction layer to use during test cases re-execution.
     */
    protected final UIAbstractionLayer uiAbstractionLayer;

    /**
     * The last Espresso Screen fetched.
     */
    protected EspressoScreenSummary lastEspressoScreen;

    public DiffAssertionsGenerator() {
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

        // has any view in last UI disappeared?
        addAssertionsForDisappearingViews(espressoScreen, assertions);

        // has any view in new UI appeared?
        addAssertionsForAppearingViews(espressoScreen, assertions);

        // has any view appearing in both last and new UI changed an attribute's value?
        addAssertionsForChangingViews(espressoScreen, assertions);

        // save new screen
        lastEspressoScreen = espressoScreen;

        return assertions;
    }

    protected void addAssertionsForDisappearingViews(@Nullable EspressoScreenSummary espressoScreen,
                                                   List<EspressoAssertion> assertions) {
        if (espressoScreen == null || lastEspressoScreen == null) {
            // we do not have enough information to generate assertions for disappearing views.
            MATELog.log_debug("Unable to generate diff assertions for disappearing views.");
            return;
        }

        Map<String, ArrayList<InstrumentationTestInteraction>> disappearingViews = espressoScreen
                .getDisappearingInteractions(lastEspressoScreen);
        for (Map.Entry<String, ArrayList<InstrumentationTestInteraction>> entry : disappearingViews.entrySet()) {

            String viewUniqueId = entry.getKey();
            EspressoRootMatcher rootMatcher = lastEspressoScreen.getRootMatcher(viewUniqueId);
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

                List<EspressoAssertion> assertionCandidates = EspressoAssertionsFactory.viewIsGone(interaction, rootMatcher);
                addSuccessfulAssertions(assertions, assertionCandidates);
            }
        }
    }

    protected void addAssertionsForAppearingViews(@Nullable EspressoScreenSummary espressoScreen,
                                                List<EspressoAssertion> assertions) {
        if (espressoScreen == null || lastEspressoScreen == null) {
            // we do not have enough information to generate assertions for appearing views.
            MATELog.log_debug("Unable to generate diff assertions for appearing views.");
            return;
        }

        Map<String, ArrayList<InstrumentationTestInteraction>> appearingViews = espressoScreen.getAppearingInteractions(lastEspressoScreen);

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
            }
        }
    }

    protected void addAssertionsForChangingViews(@Nullable EspressoScreenSummary espressoScreen,
                                               List<EspressoAssertion> assertions) {
        if (espressoScreen == null || lastEspressoScreen == null) {
            // we do not have enough information to generate assertions for changing views.
            MATELog.log_debug("Unable to generate diff assertions for changing views.");
            return;
        }

        Map<String, ArrayList<InstrumentationTestInteraction>> commonViews =
                espressoScreen.getCommonInteractionsInTopWindow(lastEspressoScreen);
        for (Map.Entry<String, ArrayList<InstrumentationTestInteraction>> entry : commonViews.entrySet()) {

            String viewUniqueId = entry.getKey();
            EspressoRootMatcher rootMatcher = espressoScreen.getRootMatcher(viewUniqueId);

            Map<String, String> oldAttributes = lastEspressoScreen.getUiAttributes(viewUniqueId);
            Map<String, String> newAttributes = espressoScreen.getUiAttributes(viewUniqueId);

            if (oldAttributes == null || newAttributes == null) {
                // weird, we found the same matcher in last and new screen, but they have
                // different IDs
                continue;
            }
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

                for (String attrKey : oldAttributes.keySet()) {
                    if (!newAttributes.containsKey(attrKey)) {
                        // weird, a view has more attributes now than before -> skip this attribute.
                        continue;
                    }

                    String oldValue = oldAttributes.get(attrKey);
                    String newValue = newAttributes.get(attrKey);

                    boolean generateViewChangedAssertions = false;
                    if (oldValue == null && newValue != null) {
                        // Null value became non-null
                        generateViewChangedAssertions = true;
                    } else if (oldValue != null && newValue == null) {
                        // Non-null value became null
                        generateViewChangedAssertions = true;
                    } else if (oldValue != null && newValue != null && !oldValue.equals(newValue)) {
                        // an attibute's value has changed
                        generateViewChangedAssertions = true;
                    }

                    if (generateViewChangedAssertions) {
                        List<EspressoAssertion> assertionCandidates = EspressoAssertionsFactory.viewHasChanged(interaction,
                                rootMatcher, attrKey, oldValue, newValue);
                        addSuccessfulAssertions(assertions, assertionCandidates);
                    }
                }
            }
        }
    }

    protected void addSuccessfulAssertions(List<EspressoAssertion> assertions,
                                   List<EspressoAssertion> assertionCandidates) {
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
