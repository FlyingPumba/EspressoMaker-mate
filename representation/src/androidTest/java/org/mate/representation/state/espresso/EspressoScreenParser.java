package org.mate.representation.state.espresso;

import androidx.annotation.Nullable;

import org.mate.commons.interaction.action.espresso.EspressoView;
import org.mate.commons.interaction.action.espresso.InstrumentationTestAction;
import org.mate.commons.interaction.action.espresso.executables.InstrumentationTestExecutable;
import org.mate.commons.interaction.action.espresso.executables.views.ClickExecutable;
import org.mate.commons.interaction.action.espresso.interactions.InstrumentationTestInteraction;
import org.mate.commons.interaction.action.espresso.interactions.InteractionType;
import org.mate.commons.interaction.action.espresso.root_matchers.EspressoRootMatcher;
import org.mate.commons.interaction.action.espresso.view_tree.EspressoViewTreeNode;
import org.mate.commons.state.espresso.EspressoRoots;
import org.mate.commons.state.espresso.EspressoScreen;
import org.mate.commons.state.espresso.EspressoScreenSummary;
import org.mate.commons.state.espresso.EspressoWindow;
import org.mate.commons.utils.MATELog;

import java.util.ArrayList;
import java.util.List;

/**
 * Parses the InstrumentationTest actions on the current screen.
 */
public class EspressoScreenParser {
    /**
     * A list of discovered InstrumentationTestActions on the current AUT's screen.
     */
    private List<InstrumentationTestAction> instrumentationTestActions;

    /**
     * The parsed Espresso Screen, or null if it was not available.
     */
    private @Nullable EspressoScreen espressoScreen;

    public EspressoScreenParser(@Nullable EspressoRoots espressoRoots) {
        if (espressoRoots != null) {
            espressoScreen = new EspressoScreen(espressoRoots);
        }
    }

    /**
     * @return A list of discovered InstrumentationTestActions on the current AUT's screen.
     */
    public @Nullable List<InstrumentationTestAction> getActions() {
        if (espressoScreen == null) {
            MATELog.log_debug("No Espresso screen found, unable to get actions");
            return null;
        }

        if (instrumentationTestActions == null) {
            parseInstrumentationTestActions();

            for (InstrumentationTestAction action : instrumentationTestActions) {
                MATELog.log("InstrumentationTest action: " + action.getCode());
            }
        }

        return instrumentationTestActions;
    }

    /**
     * Parses the InstrumentationTest actions available in the top window of current Espresso Screen.
     * An InstrumentationTest action is found when we find a View for which we can execute a ViewAction, and we
     * also find a ViewMatcher that unequivocally targets it.
     *
     * This method relies on the InstrumentationTestExecutablesParser class to determine which ViewActions can
     * be executed on each View. If a View has no suitable actions, we skip the phase of finding a
     * unequivocal ViewMatcher all together.
     *
     * If a View does not have a unequivocal matcher combination, it is skipped as well.
     */
    private void parseInstrumentationTestActions() {
        long startTime = System.nanoTime();

        if (instrumentationTestActions == null) {
            instrumentationTestActions = new ArrayList<>();
        }

        if (this.espressoScreen == null) {
            throw new RuntimeException("Espresso screen is null in parseInstrumentationTestActions method");
        }

        EspressoWindow topWindow = this.espressoScreen.getTopWindow();
        for (EspressoViewTreeNode node : topWindow.getViewTree().getAllNodes()) {
            ArrayList<InstrumentationTestInteraction> interactions =
                    this.espressoScreen.getInteractionsInScreenForUniqueId(
                            node.getEspressoView().getUniqueId());
            if (interactions == null) {
                // we weren't able to generate a unequivocal matcher combination for this view, skip it.
                continue;
            }

            EspressoRootMatcher rootMatcher = espressoScreen.getSummary().getRootMatcher(node.getEspressoView().getUniqueId());

            for (InstrumentationTestInteraction interaction : interactions) {
                EspressoView interactionTargetEspressoView =
                        interaction.getTargetEspressoView(node.getEspressoView());

                InstrumentationTestExecutablesParser executablesParser =
                        new InstrumentationTestExecutablesParser(espressoScreen,
                                interactionTargetEspressoView);
                List<InstrumentationTestExecutable> executables = executablesParser.parse();

                if (executables.size() == 0) {
                    // nothing to do on this interaction, skip it.
                    continue;
                }

                // Create and save the InstrumentationTestAction instances
                for (InstrumentationTestExecutable executable : executables) {
                    InstrumentationTestAction InstrumentationTestAction = new InstrumentationTestAction(
                            executable,
                            interaction,
                            rootMatcher);
                    instrumentationTestActions.add(InstrumentationTestAction);
                }
            }
        }

        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1000000;  //divide by 1000000 to get ms.
        MATELog.log_debug(String.format("Loading of InstrumentationTest actions took %d ms", duration));
    }

    @Nullable
    public EspressoScreen getEspressoScreen() {
        return espressoScreen;
    }

    public @Nullable EspressoScreenSummary getEspressoScreenSummary() {
        if (espressoScreen == null) {
            return null;
        }

        return espressoScreen.getSummary();
    }
}
