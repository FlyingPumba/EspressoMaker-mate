package org.mate.utils.testcase.espresso;

import org.mate.Properties;
import org.mate.Registry;
import org.mate.commons.interaction.action.Action;
import org.mate.commons.interaction.action.intent.IntentBasedAction;
import org.mate.commons.interaction.action.intent.SystemAction;
import org.mate.commons.interaction.action.ui.MotifAction;
import org.mate.commons.interaction.action.ui.UIAction;
import org.mate.commons.interaction.action.ui.WidgetAction;
import org.mate.model.TestCase;
import org.mate.utils.testcase.espresso.actions.ActionConverter;
import org.mate.utils.testcase.espresso.actions.IntentBasedActionConverter;
import org.mate.utils.testcase.espresso.actions.MotifActionConverter;
import org.mate.utils.testcase.espresso.actions.SystemActionConverter;
import org.mate.utils.testcase.espresso.actions.UIActionConverter;
import org.mate.utils.testcase.espresso.actions.WidgetActionConverter;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.mate.utils.testcase.espresso.EspressoDependency.ANDROID_JUNIT_4;
import static org.mate.utils.testcase.espresso.EspressoDependency.LARGE_TEST;
import static org.mate.utils.testcase.espresso.EspressoDependency.RUN_WITH;
import static org.mate.utils.testcase.espresso.EspressoDependency.TEST;
import static org.mate.utils.testcase.espresso.EspressoDependency.GET_INSTRUMENTATION;
import static org.mate.utils.testcase.espresso.EspressoDependency.UI_DEVICE;

/**
 * Assembles an espresso test from a given {@link TestCase}.
 */
public class EspressoTestBuilder {

    /**
     * A tab consists of four blanks.
     */
    private static final String TAB = "    ";

    /**
     * The new line separator.
     */
    private static final String NEW_LINE = System.lineSeparator();

    /**
     * A underlying builder that iteratively constructs the espresso test.
     */
    private final StringBuilder builder = new StringBuilder();

    /**
     * The start position of the imports.
     */
    private int startImportsPosition;

    /**
     * Keeps track of the necessary espresso dependencies.
     */
    private final EspressoDependencyBuilder DEPENDENCY_BUILDER = EspressoDependencyBuilder.getInstance();

    /**
     * Constructs an espresso test for the given test case.
     *
     * @param testCase The test case that should be converted.
     * @param testCaseId The numerical id of the test case.
     * @param packageName The package name of the espresso test.
     */
    public EspressoTestBuilder(TestCase testCase, int testCaseId, String packageName) {
        buildPackageHeader(packageName);
        emptyLines(2);
        trackImportsPosition();
        emptyLines(1);
        buildClass(testCase, testCaseId);
        buildImports();
    }

    /**
     * Builds the test class.
     *
     * @param testCase The test case to be converted to an espresso test.
     * @param testCaseId The numerical id of the test.
     */
    private void buildClass(TestCase testCase, int testCaseId) {

        // build the class header
        buildClassHeader(testCaseId);

        // build class variables
        emptyLines(1);
        buildSetup();
        emptyLines(2);

        // build the test method
        buildTest(testCase);

        // close the test class
        buildLine(0, "}");
    }

    /**
     * Builds an espresso test for the given test case.
     *
     * @param testCase The test case to be converted to an espresso test.
     */
    private void buildTest(TestCase testCase) {

        // build the test method header
        buildLine(1, "@" + TEST);
        buildLine(1, "public void test() throws Exception {");

        buildDelayStatement(Properties.DELAY_AFTER_ACTIVITY_STARTED());

        // convert the actions to statements
        List<Action> actionSequence = testCase.getActionSequence();
        for (int i = 0; i < actionSequence.size(); i++) {
            Action action = actionSequence.get(i);

            buildLine(2, String.format(Locale.US, "// Executing action %d", i));

            buildStatements(action);
            buildDelayStatement(Properties.ACTIONS_THROTTLE());

            buildLine(2, String.format(Locale.US, "// Current Activity is %s",
                    testCase.getActivityAfterAction(i)));
            buildLine(2, "");
        }

        buildLine(2, String.format(Locale.US, "// Last action result: %s",
                testCase.getLastActionResult()));

        // close the test method
        buildLine(1, "}");
    }

    /**
     * Builds one or more espresso statements for the given action.
     *
     * @param action The action to be converted to one or more espresso statements.
     */
    private void buildStatements(Action action) {

        ActionConverter actionConverter;

        if (action instanceof WidgetAction) {
            actionConverter = new WidgetActionConverter((WidgetAction) action);
        } else if (action instanceof MotifAction) {
            actionConverter = new MotifActionConverter((MotifAction) action);
        } else if (action instanceof UIAction) {
            actionConverter = new UIActionConverter((UIAction) action);
        } else if (action instanceof SystemAction) {
            actionConverter = new SystemActionConverter((SystemAction) action);
        } else if (action instanceof IntentBasedAction) {
            actionConverter = new IntentBasedActionConverter((IntentBasedAction) action);
        } else {
            throw new UnsupportedOperationException("Action " + action.getClass() + " not yet supported!");
        }

        buildLines(2, Arrays.asList(actionConverter.convert().split(System.lineSeparator())));
    }

    /**
     * Builds the package header.
     *
     * @param packageName The package name of the espresso test.
     */
    private void buildPackageHeader(String packageName) {
        buildLine(0, "package " + packageName + ";");
    }

    /**
     * Builds the class header.
     *
     * @param testCaseId The test case id.
     */
    private void buildClassHeader(final int testCaseId) {
        buildLine(0, "@" + LARGE_TEST);
        buildLine(0, "@" + RUN_WITH + "(" + ANDROID_JUNIT_4 + ".class)");
        buildLine(0, "public class InstrumentationTestCase_" + testCaseId + "_translation extends TestUtils {");
    }

    /**
     * Builds the necessary (static) variables.
     */
    private void buildSetup() {
        buildLine(1, "static {");
        buildLine(2, "PACKAGE_NAME = \"" + Registry.getPackageName() + "\";");
        buildLine(2, "START_ACTIVITY_NAME = \"" + Registry.getMainActivity() + "\";");
	    buildLine(2, String.format("UI_DEVICE = %s.getInstance(%s());", UI_DEVICE, GET_INSTRUMENTATION));
        buildLine(1, "}");
    }

    /**
     * Builds the import statements.
     */
    private void buildImports() {

        StringBuilder importStatementsBuilder = new StringBuilder();

        for (EspressoDependency dependency : DEPENDENCY_BUILDER.getOrderedDependencies()) {
            if (dependency != null) {
                String importStatement = dependency.isStaticDependency() ? "import static " : "import ";
                importStatement += dependency.getFullQualifiedName() + ";";
                importStatementsBuilder.append(importStatement).append(NEW_LINE);
            } else {
                // separate imports in groups where different imports belong into a different group
                importStatementsBuilder.append(NEW_LINE);
            }
        }

        builder.insert(startImportsPosition, importStatementsBuilder.toString());
    }

    /**
     * Appends {@code count} many new lines.
     *
     * @param count The number of new lines that should be inserted.
     */
    private void emptyLines(int count) {
        for (int i = 0; i < count; i++) {
            builder.append(NEW_LINE);
        }
    }

    /**
     * Appends the given {@code lines} intended by {@code tabs}.
     *
     * @param tabs The number of tabs.
     * @param lines The lines that should be inserted.
     */
    private void buildLines(int tabs, List<String> lines) {
        for (String line : lines) {
            buildLine(tabs, line);
        }
    }

    /**
     * Appends the given {@code line} intended by {@code tabs} many tabs. Also inserts a new line
     * at the end.
     *
     * @param tabs The number of tabs.
     * @param line The line that should be inserted.
     */
    private void buildLine(int tabs, String line) {
        for (int i = 0; i < tabs; i++) {
            builder.append(TAB);
        }
        builder.append(line);
        builder.append(NEW_LINE);
    }

    private void buildDelayStatement(long delay) {
        if (delay > 0) {
            buildLine(2, String.format(Locale.US, "SystemClock.sleep(%d);", delay));
            emptyLines(1);
        }
    }

    /**
     * Tracks the start position of the imports.
     */
    private void trackImportsPosition() {
        startImportsPosition = builder.length();
    }

    /**
     * Builds the espresso test.
     *
     * @return Returns the assembled espresso test.
     */
    public String build() {
        DEPENDENCY_BUILDER.reset();
        return builder.toString();
    }
}
