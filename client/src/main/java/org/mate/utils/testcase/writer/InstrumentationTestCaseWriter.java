package org.mate.utils.testcase.writer;

import org.mate.Registry;
import org.mate.commons.interaction.action.Action;
import org.mate.commons.interaction.action.espresso.InstrumentationTestAction;
import org.mate.model.TestCase;
import org.mate.service.MATEService;

import java.io.File;

/**
 * TestCaseWriter for Instrumentation test cases.
 */
public class InstrumentationTestCaseWriter extends TestCaseWriter {

    private String testCaseName;

    public InstrumentationTestCaseWriter(TestCase testCase) throws IllegalArgumentException {
        super(testCase);

        // set initial values for writing test case
        this.testCaseName = String.format("InstrumentationTestCase_%d", this.writeCounter);
    }

    @Override
    boolean isSuitableForTestCase() {
        // The Instrumentation test case writer is suitable for a test case if the Actions in the event
        // sequence are all instances of InstrumentationTestAction.

        for (Action action : testCase.getActionSequence()) {
            if (!(action instanceof InstrumentationTestAction)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public String getTestCaseString() {
        InstrumentationTestCaseStringConverter converter =
                new InstrumentationTestCaseStringConverter(
                        Registry.getPackageName(),
                        MATEService.getStartActivityName(),
                        getTestCaseName(),
                        "testMethod");

        converter.parseTestCase(testCase);

        return converter.getCode();
    }

    /**
     * Change the name to use when writing test case.
     * @param value the new test case name
     */
    public void setTestCaseName(String value) {
        this.testCaseName = value;
    }

    @Override
    public String getTestCaseName() {
        return testCaseName;
    }

    @Override
    public String getTestCaseFileName() {
        return String.format("%s.java", this.getTestCaseName());
    }

    @Override
    public String getDefaultWriteFolder() {
        File filesDir = Registry.getContext().getFilesDir();
        File instrumentationTestCasesFolder = new File(filesDir, "instrumentation-test-cases");
        return instrumentationTestCasesFolder.getAbsolutePath();
    }

    @Override
    protected void triggerMATEServerDownload() {
        boolean success = Registry.getEnvironmentManager()
                .fetchEspressoTest(getDefaultWriteFolder(), getTestCaseFileName());

        if (!success) {
            // re-try a second time
            success = Registry.getEnvironmentManager()
                    .fetchEspressoTest(getDefaultWriteFolder(), getTestCaseFileName());

            if (!success) {
                throw new IllegalStateException("Fetching TestCase " + writeCounter + " failed!");
            }
        }
    }
}
