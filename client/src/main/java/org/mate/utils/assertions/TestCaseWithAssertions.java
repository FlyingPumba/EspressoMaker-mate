package org.mate.utils.assertions;

import org.mate.Registry;
import org.mate.commons.interaction.action.Action;
import org.mate.commons.interaction.action.espresso.EspressoAssertion;
import org.mate.commons.utils.MATELog;
import org.mate.model.TestCase;
import org.mate.utils.testcase.writer.InstrumentationTestCaseWriter;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Class that represents a test case with assertions.
 *
 * If the test case has N actions, then we have N+1 points in which we can add multiple
 * assertions to the test. The assertions at index 0 are the assertions before the first action
 * of the Test Case, the assertions at index 1 are the assertions before the second action, etc.
 * The assertions at index N+1 are the assertions after the last action.
 */
public class TestCaseWithAssertions extends TestCase {
    /**
     * The "full" Espresso assertions to write before each action into the final String.
     * Full assertions are those that assert every attribute of the views in the UI.
     */
    private Map<Integer, List<EspressoAssertion>> fullAssertions = new HashMap<>();

    /**
     * The "semi-full" Espresso assertions to write before each action into the final String.
     * Semi-full assertions are those that assert only some attributes of the views in the UI.
     * In particular, if a new view appears on the screen we will assert all its attributes,
     * otherwise we will assert only the attributes that have changed.
     */
    private Map<Integer, List<EspressoAssertion>> semiFullAssertions = new HashMap<>();

    /**
     * The "diff" Espresso assertions to write before each action into the final String.
     * Diff assertions are those that assert only the attributes that have changed in the views in the UI.
     */
    private Map<Integer, List<EspressoAssertion>> diffAssertions = new HashMap<>();

    FullAssertionsGenerator fullAssertionsGenerator = new FullAssertionsGenerator();
    SemiFullAssertionsGenerator semiFullAssertionsGenerator = new SemiFullAssertionsGenerator();
    DiffAssertionsGenerator diffAssertionsGenerator = new DiffAssertionsGenerator();

    /**
     * Creates a new test case object with the given id.
     *
     * @param id The (unique) test case id.
     */
    public TestCaseWithAssertions(String id) {
        super(id);
        buildAssertions();
    }

    /**
     * Initializes a new test case with a random id.
     *
     * @return Returns a new test case with a random id.
     */
    public static TestCase newInitializedTestCase() {
        return new TestCaseWithAssertions(UUID.randomUUID().toString());
    }

    @Override
    public boolean updateTestCase(Action action, int actionID) {
        boolean shouldContinueTestCase = super.updateTestCase(action, actionID);
        buildAssertions();

        return shouldContinueTestCase;
    }

    private void buildAssertions() {
        List<EspressoAssertion> fullAssertions = new ArrayList<>();
        List<EspressoAssertion> semiFullAssertions = new ArrayList<>();
        List<EspressoAssertion> diffAssertions = new ArrayList<>();

        // If last action took us outside the AUT, we skip generating assertions for it.
        List<String> packageActivities = Registry.getUiAbstractionLayer().getActivities();
        String lastActivity = getLastActivityName();
        if (packageActivities.contains(lastActivity)) {
            fullAssertions = fullAssertionsGenerator.generate();
            semiFullAssertions = semiFullAssertionsGenerator.generate();
            diffAssertions = diffAssertionsGenerator.generate();
        } else {
            MATELog.log_debug("Skipping assertions for current screen state: we are outside the " +
                    "AUT: " + lastActivity);
        }

        int actionIndex = actionSequence.size();

        this.fullAssertions.put(actionIndex, fullAssertions);
        this.semiFullAssertions.put(actionIndex, semiFullAssertions);
        this.diffAssertions.put(actionIndex, diffAssertions);
    }

    private String getLastActivityName() {
        return activitySequence.get(activitySequence.size() - 1);
    }

    @Override
    public void writeAsInstrumentationTestIfPossible() {
        if (actionSequence.size() == 0) {
            MATELog.log_debug("Not writing Instrumentation test case with assertions because it " +
                    "has no actions");
            return;
        }

        try {
            InstrumentationTestCaseWriter espressoTestWriter = new InstrumentationTestCaseWriter(this);

            espressoTestWriter.setTestCaseName(String.format("%s_with_assertions",
                    espressoTestWriter.getTestCaseName()));

            boolean success = espressoTestWriter.writeToDefaultFolder();
            if (!success) {
                MATELog.log_warn("Unable to write Espresso test case to internal storage");
            }
        } catch (IllegalArgumentException e) {
            // InstrumentationTestCaseWriter is probably not suitable for this test case.
            MATELog.log_debug("Unable to write Instrumentation test case with assertions: " +
                    e.getMessage());
        } catch (Exception e) {
            MATELog.log_warn("An exception happened while writing Espresso test case to " +
                    "internal storage: " + e.getMessage());

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            MATELog.log_warn(sw.toString());
        }
    }

    public Map<Integer, List<EspressoAssertion>> getFullAssertions() {
        return Collections.unmodifiableMap(fullAssertions);
    }

    public Map<Integer, List<EspressoAssertion>> getSemiFullAssertions() {
        return Collections.unmodifiableMap(semiFullAssertions);
    }

    public Map<Integer, List<EspressoAssertion>> getDiffAssertions() {
        return Collections.unmodifiableMap(diffAssertions);
    }
}
