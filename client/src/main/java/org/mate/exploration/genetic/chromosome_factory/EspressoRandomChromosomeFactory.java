package org.mate.exploration.genetic.chromosome_factory;

import androidx.annotation.NonNull;

import org.mate.Properties;
import org.mate.Registry;
import org.mate.commons.interaction.action.Action;
import org.mate.commons.interaction.action.espresso.InstrumentationTestAction;
import org.mate.commons.utils.Randomness;
import org.mate.exploration.genetic.core.GeneticAlgorithm;
import org.mate.model.TestCase;
import org.mate.utils.assertions.TestCaseWithAssertions;

import java.util.List;

/**
 * Provides a chromosome factory that generates {@link TestCase}s consisting of random
 * {@link InstrumentationTestAction}s.
 */
public class EspressoRandomChromosomeFactory extends AndroidRandomChromosomeFactory {

    /**
     * Initialises a new chromosome factory that is capable of generating random {@link TestCase}s.
     *
     * @param maxNumEvents The maximal number of actions per test case.
     */
    public EspressoRandomChromosomeFactory(int maxNumEvents) {
        this(true, maxNumEvents);
    }

    /**
     * Initialises a new chromosome factory that is capable of generating random {@link TestCase}s.
     *
     * @param resetApp Whether to reset the AUT before creating a new chromosome (test case).
     * @param maxNumEvents The maximal number of actions per test case.
     */
    public EspressoRandomChromosomeFactory(boolean resetApp, int maxNumEvents) {
        super(resetApp, maxNumEvents);
        // As per Espresso setup instructions: "To avoid flakiness, we highly recommend that you
        // turn off system animations on the virtual or physical devices used for testing."
        // URL: https://developer.android.com/training/testing/espresso/setup#set-up-environment
        Registry.getDeviceMgr().disableAnimations();
    }

    @Override
    protected Action selectAction() {
        List<InstrumentationTestAction> availableActions = uiAbstractionLayer.getExecutableInstrumentationTestActions();
        return Randomness.randomElement(availableActions);
    }

    @Override
    @NonNull
    protected TestCase getNewTest() {
        if (Properties.GENERATE_ASSERTIONS() && Registry.getRunningAlgorithm() instanceof GeneticAlgorithm) {
            return TestCaseWithAssertions.newInitializedTestCase();
        } else {
            return TestCase.newInitializedTestCase();
        }
    }
}
