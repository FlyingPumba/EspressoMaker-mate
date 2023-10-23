package org.mate.exploration.genetic.mutation;

import static org.mate.exploration.genetic.chromosome_factory.ChromosomeFactory.ESPRESSO_RANDOM_CHROMOSOME_FACTORY;

import org.mate.Properties;
import org.mate.Registry;
import org.mate.commons.interaction.action.Action;
import org.mate.commons.interaction.action.ui.UIAction;
import org.mate.commons.utils.MATELog;
import org.mate.commons.utils.Randomness;
import org.mate.exploration.genetic.chromosome.Chromosome;
import org.mate.exploration.genetic.chromosome.IChromosome;
import org.mate.interaction.UIAbstractionLayer;
import org.mate.model.TestCase;
import org.mate.model.fsm.surrogate.SurrogateModel;
import org.mate.utils.FitnessUtils;
import org.mate.utils.coverage.CoverageUtils;

import java.util.List;

/**
 * Provides a cut point mutation function for {@link TestCase}s. Only applicable in combination
 * with {@link UIAction}s or {@link InstrumentationTestAction}s.
 */
public class CutPointMutationFunction implements IMutationFunction<TestCase> {

    /**
     * Provides primarily information about the current screen.
     */
    private final UIAbstractionLayer uiAbstractionLayer;

    /**
     * The maximal number of actions per test case.
     */
    private final int maxNumEvents;

    /**
     * Whether we deal with a test suite execution, i.e. whether the used chromosome factory
     * produces {@link org.mate.model.TestSuite}s or not.
     */
    private boolean isTestSuiteExecution = false;

    /**
     * Whether to use InstrumentationTest actions instead of UiActions or not.
     */
    private boolean useInstrumentationTestActions = false;

    /**
     * Initialises the cut point mutation function.
     *
     * @param maxNumEvents The maximal number of actions per test case.
     */
    public CutPointMutationFunction(int maxNumEvents) {
        this.uiAbstractionLayer = Registry.getUiAbstractionLayer();
        this.maxNumEvents = maxNumEvents;

        useInstrumentationTestActions = ESPRESSO_RANDOM_CHROMOSOME_FACTORY.equals(Properties.CHROMOSOME_FACTORY());
    }

    // TODO: might be replaceable with chromosome factory property in the future
    /**
     * Defines whether we deal with a test suite execution or not.
     *
     * @param testSuiteExecution Indicates if we deal with a test suite execution or not.
     */
    public void setTestSuiteExecution(boolean testSuiteExecution) {
        this.isTestSuiteExecution = testSuiteExecution;
    }

    /**
     * Performs a cut point mutation. First, the given test case is split at a chosen cut point.
     * Then, the mutated test case is filled with the original actions up to the cut point and
     * from the cut point onwards with random actions.
     *
     * @param chromosome The chromosome to be mutated.
     * @return Returns the mutated chromosome.
     */
    @Override
    public IChromosome<TestCase> mutate(IChromosome<TestCase> chromosome) {

        uiAbstractionLayer.resetApp();
        int cutPoint = chooseCutPoint(chromosome.getValue());

        TestCase mutant = TestCase.newInitializedTestCase();
        IChromosome<TestCase> mutatedChromosome = new Chromosome<>(mutant);

        try {
            for (int i = 0; i < maxNumEvents; i++) {
                Action newAction = chooseNextAction(chromosome, cutPoint, i);

                if (!getAvailableActions().contains(newAction)
                        || !mutant.updateTestCase(newAction, i)) {
                    break;
                }
            }
        } finally {

            if (Properties.SURROGATE_MODEL()) {
                // update sequences + write traces to external storage
                SurrogateModel surrogateModel
                        = (SurrogateModel) Registry.getUiAbstractionLayer().getGuiModel();
                surrogateModel.updateTestCase(mutant);
            }

            if (!isTestSuiteExecution) {
                /*
                 * If we deal with a test suite execution, the storing of coverage
                 * and fitness data is handled by the test suite mutation operator itself.
                 */
                FitnessUtils.storeTestCaseChromosomeFitness(mutatedChromosome);
                CoverageUtils.storeTestCaseChromosomeCoverage(mutatedChromosome);
                CoverageUtils.logChromosomeCoverage(mutatedChromosome);
            }

            mutant.finish();
        }

        return mutatedChromosome;
    }

    /**
     * Returns the available actions for the current screen.
     * It will return the InstrumentationTest actions if we deal with an Espresso execution, otherwise it will
     * return the UiActions.
     * @return The available actions for the current screen.
     */
    private List<? extends Action> getAvailableActions() {
        if (useInstrumentationTestActions) {
            return uiAbstractionLayer.getExecutableInstrumentationTestActions();
        } else {
            return uiAbstractionLayer.getExecutableUiActions();
        }
    }

    /**
     * Chooses a new action to be added to the test case.
     * @param chromosome The chromosome to be mutated.
     * @param cutPoint The cut point.
     * @param i The index of the action to be added.
     * @return Returns the new action.
     */
    private Action chooseNextAction(IChromosome<TestCase> chromosome, int cutPoint, int i) {
        Action newAction;
        if (i < cutPoint) {
            newAction = chromosome.getValue().getActionSequence().get(i);
        } else {
            newAction = Randomness.randomElement(getAvailableActions());
        }
        return newAction;
    }

    /**
     * Chooses a random cut point in the action sequence of the given test case.
     *
     * @param testCase The given test case.
     * @return Returns the selected cut point.
     */
    private int chooseCutPoint(TestCase testCase) {
        if (testCase.getActionSequence().isEmpty()) {
            MATELog.log_warn("Choosing cut point from empty test case " + testCase + "!");
            return 0;
        } else {
            return Randomness.getRnd().nextInt(testCase.getActionSequence().size());
        }
    }
}
