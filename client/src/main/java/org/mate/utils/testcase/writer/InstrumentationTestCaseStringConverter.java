package org.mate.utils.testcase.writer;

import org.mate.Properties;
import org.mate.commons.interaction.action.Action;
import org.mate.commons.interaction.action.ActionResult;
import org.mate.commons.interaction.action.espresso.EspressoAssertion;
import org.mate.commons.interaction.action.espresso.InstrumentationTestAction;
import org.mate.commons.interaction.action.espresso.executables.views.CloseSoftKeyboardExecutable;
import org.mate.commons.interaction.action.espresso.interactions.EspressoViewInteraction;
import org.mate.commons.interaction.action.espresso.view_matchers.base.IsRootViewMatcher;
import org.mate.commons.utils.AbstractCodeProducer;
import org.mate.model.TestCase;
import org.mate.utils.assertions.TestCaseWithAssertions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

/**
 * Converts a TestCase composed of InstrumentationTest Actions into a String representation of a valid Java
 * file. You can get this String by calling the {@link #getCode} method.
 *
 * Its important to note that by calling the method {@link #setConvertingForAUTsCodeBase} you can
 * alter a bit the String output. If the value set is true, the converter will try to produce a
 * Java String that can be included into the AUT's codebase. Otherwise, the converter will
 * produce a Java String that is meant to be used with our own Espresso-test-apk-builder program.
 * For example, when convertingForAUTsCodeBase is true, the package name of the test case is set
 * as the AUT's package name.
 * The default for this value is false.
 */
public class InstrumentationTestCaseStringConverter extends AbstractCodeProducer {

    public static final int MAXIMUM_ASSERTIONS_PER_METHODS = 400;
    /**
     * The AUT's package name.
     */
    private final String packageName;

    /**
     * The AUT's Activity name in which this test starts.
     */
    private final String startingActivityName;

    /**
     * A class name for this test case.
     */
    private final String testCaseName;

    /**
     * A method name for this test case.
     */
    private final String testMethodName;

    /**
     * The InstrumentationTest actions to write into the final String.
     */
    private final List<InstrumentationTestAction> actions;
    private final InstrumentationTestAction closeSoftKeyboardAction;

    /**
     * The "full" Espresso assertions to write before each action into the final String.
     * Full assertions are those that assert every attribute of the views in the UI.
     */
    private Map<Integer, List<EspressoAssertion>> fullAssertions;

    /**
     * The "semi-full" Espresso assertions to write before each action into the final String.
     * Semi-full assertions are those that assert only some attributes of the views in the UI.
     * In particular, if a new view appears on the screen we will assert all its attributes,
     * otherwise we will assert only the attributes that have changed.
     */
    private Map<Integer, List<EspressoAssertion>> semiFullAssertions;

    /**
     * The "diff" Espresso assertions to write before each action into the final String.
     * Diff assertions are those that assert only the attributes that have changed in the views in the UI.
     */
    private Map<Integer, List<EspressoAssertion>> diffAssertions;

    /**
     * The Java classes to include in the imports of the final String.
     */
    private final Set<String> classImports;

    /**
     * The Java methods to include in the imports of the final String.
     */
    private final Set<String> staticImports;

    /**
     * The Espresso dependency prefix.
     */
    private String espressoDependencyGroup;

    /**
     * The Espresso dependency version.
     */
    private String espressoDependencyVersion;

    /**
     * Indicates whether we are making an Espresso test for running inside the AUT's codebase,
     * or to run it with our own Espresso-test-apk-builder.
     */
    private boolean convertingForAUTsCodeBase = false;

    /**
     * Indicates whether to add debug comments such as the Activity name after each action.
     */
    private boolean addDebugComments = true;

    /**
     * Internal StringBuilder to compose the final String.
     */
    private StringBuilder builder;

    /**
     * The actions after which the soft keyboard was closed.
     */
    private List<Action> actionsThatClosedSoftKeyboard;

    /**
     * The activity sequence (in order) that has been covered through the execution of the test
     * case actions. First element is the Activity name recorder **before** the first action, and
     * so on.
     */
    private List<String> activitySequence;

    /**
     * The last action result.
     */
    private ActionResult lastActionResult;

    public InstrumentationTestCaseStringConverter(String packageName,
                                                  String startingActivityName,
                                                  String testCaseName,
                                                  String testMethodName) {
        this.packageName = packageName;
        this.startingActivityName = startingActivityName;
        this.testCaseName = testCaseName;
        this.testMethodName = testMethodName;

        this.actions = new ArrayList<>();
        this.actionsThatClosedSoftKeyboard = new ArrayList<>();
        this.classImports = new HashSet<>();
        this.staticImports = new HashSet<>();

        // By default, we assume that we are going to write an Espresso test case for Espresso's
        // latest version (i.e., using the AndroidX testing package).
        // All versions of this dependency are listed in the following link:
        // https://mvnrepository.com/artifact/androidx.test.espresso/espresso-core
        espressoDependencyGroup = "androidx.test.espresso";
        espressoDependencyVersion = "3.4.0";

        closeSoftKeyboardAction = new InstrumentationTestAction(
                new CloseSoftKeyboardExecutable(),
                new EspressoViewInteraction(new IsRootViewMatcher()),
                null);

        addDefaultImports();
    }

    public void parseTestCase(TestCase testCase) {
        for (Action action : testCase.getActionSequence()) {
            InstrumentationTestAction InstrumentationTestAction = (InstrumentationTestAction) action;
            addAction(InstrumentationTestAction);
        }

        if (testCase instanceof TestCaseWithAssertions) {
            TestCaseWithAssertions testCaseWithAssertions = (TestCaseWithAssertions) testCase;
            addFullAssertions(testCaseWithAssertions.getFullAssertions());
            addSemiFullAssertions(testCaseWithAssertions.getSemiFullAssertions());
            addDiffAssertions(testCaseWithAssertions.getDiffAssertions());
        }

        this.actionsThatClosedSoftKeyboard = testCase.getActionsThatClosedSoftKeyboard();
        this.activitySequence = testCase.getActivitySequence();
        this.lastActionResult = testCase.getLastActionResult();
    }

    private void addFullAssertions(Map<Integer, List<EspressoAssertion>> fullAssertions) {
        this.fullAssertions = fullAssertions;
        loadAssertionsImports(fullAssertions);
    }

    private void addSemiFullAssertions(Map<Integer, List<EspressoAssertion>> semiFullAssertions) {
        this.semiFullAssertions = semiFullAssertions;
        loadAssertionsImports(semiFullAssertions);
    }

    private void addDiffAssertions(Map<Integer, List<EspressoAssertion>> diffAssertions) {
        this.diffAssertions = diffAssertions;
        loadAssertionsImports(diffAssertions);
    }

    private void loadAssertionsImports(Map<Integer, List<EspressoAssertion>> assertions) {
        for (List<EspressoAssertion> aux : assertions.values()) {
            for (EspressoAssertion assertion : aux) {
                this.classImports.addAll(assertion.getNeededClassImports());
                this.staticImports.addAll(assertion.getNeededStaticImports());
            }
        }
    }

    /**
     * Adds an InstrumentationTest action String representation into the final test case's body.
     * The class and static imports of the InstrumentationTest action are included as well.
     * @param InstrumentationTestAction the action to add.
     */
    private void addAction(InstrumentationTestAction InstrumentationTestAction) {
        this.actions.add(InstrumentationTestAction);
        this.classImports.addAll(InstrumentationTestAction.getNeededClassImports());
        this.staticImports.addAll(InstrumentationTestAction.getNeededStaticImports());
    }

    /**
     * Change whether this converter should produce a test case to be used inside AUT's codebase
     * or for our own Espresso-test-apk-builder program.
     * @param convertingForAUTsCodeBase boolean
     */
    public void setConvertingForAUTsCodeBase(boolean convertingForAUTsCodeBase) {
        this.convertingForAUTsCodeBase = convertingForAUTsCodeBase;
    }

    @Override
    public String getCode() {
        builder = new StringBuilder();

        writePackage();
        writeImports();

        writeTestClassHeader();

        writeTestActivityRule();

        writeTestMethodHeader();
        writeTestBody();
        writeMethodFooter();

        writeAssertionMethods();

        writeClassFooter();

        return builder.toString();
    }

    @Override
    public Set<String> getNeededClassImports() {
        return this.classImports;
    }

    @Override
    public Set<String> getNeededStaticImports() {
        return this.staticImports;
    }

    /**
     * Adds default imports that any Android test case must have.
     */
    private void addDefaultImports() {
        // Espresso stuff
        classImports.add("androidx.test.rule.ActivityTestRule");
        classImports.add("androidx.test.runner.AndroidJUnit4");
        classImports.add("androidx.test.filters.LargeTest");
        classImports.addAll(closeSoftKeyboardAction.getNeededClassImports());

        // JUnit stuff
        classImports.add("org.junit.Rule");
        classImports.add("org.junit.Test");
        classImports.add("org.junit.runner.RunWith");

        // UiDevice stuff
        classImports.add("androidx.test.uiautomator.UiDevice");
        staticImports.add("androidx.test.platform.app.InstrumentationRegistry.getInstrumentation");

        if (convertingForAUTsCodeBase) {
            // Add AUT's resources as a default import
            classImports.add(String.format("%s.R", packageName));
        }

        // Delays between actions
        classImports.add("android.os.SystemClock");

        staticImports.addAll(closeSoftKeyboardAction.getNeededStaticImports());
    }

    /**
     * Writes a line into the current In-Progress String test case.
     * The New Line character is added after it.
     * @param line string
     */
    private void writeLine(String line) {
        builder.append(String.format("%s\n", line));
    }

    /**
     * Writes a Java expression as a new line into the current In-Progress String test case.
     * The semi-colon character is added after it.
     * @param expression string
     */
    private void writeExpressionLine(String expression) {
        writeLine(String.format("%s;", expression));
    }

    /**
     * Writes a Java annotation as a new line into the current In-Progress String test case.
     * The "@" character is added before it.
     * @param annotation string
     */
    private void writeAnnotationLine(String annotation) {
        writeLine(String.format("@%s", annotation));
    }

    /**
     * Writes an empty line into the current In-Progress String test case.
     */
    private void writeEmptyLine() {
        writeLine("");
    }

    /**
     * Writes a comment into the current In-Progress String test case.
     * @param comment string
     */
    private void writeComment(String comment) {
        writeLine(String.format("// %s", comment));
    }

    /**
     * Writes appropriate package name header into the current In-Progress String test case.
     */
    private void writePackage() {
        if (convertingForAUTsCodeBase) {
            writeExpressionLine(String.format("package %s", packageName));
        } else {
            writeExpressionLine("package org.mate.espresso.tests");
        }
        writeEmptyLine();
    }

    /**
     * Writes class and static imports collected so far into the current In-Progress String test
     * case.
     */
    private void writeImports() {
        for (String fullyQualifiedClassName : classImports) {
            writeExpressionLine(String.format("import %s",
                    normalizeImport(fullyQualifiedClassName)));
        }

        writeEmptyLine();

        for (String fullyQualifiedMethodName : staticImports) {
            writeExpressionLine(String.format("import static %s",
                    normalizeImport(fullyQualifiedMethodName)));
        }

        writeEmptyLine();
    }

    /**
     * Writes appropriate test class header into the current In-Progress String test case.
     */
    private void writeTestClassHeader() {
        writeAnnotationLine("LargeTest");
        writeAnnotationLine("RunWith(AndroidJUnit4.class)");
        if (convertingForAUTsCodeBase) {
            writeLine(String.format("public class %s {", testCaseName));
        } else {
            writeLine(String.format("public class %s extends TestUtils {", testCaseName));
        }
        writeEmptyLine();
    }

    /**
     * Writes appropriate test activity rule into the current In-Progress String test case.
     */
    private void writeTestActivityRule() {
        if (convertingForAUTsCodeBase) {
            writeAnnotationLine("Rule");
            writeExpressionLine(String.format("public ActivityTestRule<%s> mActivityTestRule = " +
                    "new ActivityTestRule<>(%s.class)", startingActivityName, startingActivityName));
        } else {
            writeLine("static {");
            writeExpressionLine(String.format("PACKAGE_NAME = \"%s\"", packageName));
            writeExpressionLine(String.format("START_ACTIVITY_NAME = \"%s\"", startingActivityName));
            writeExpressionLine("UI_DEVICE = UiDevice.getInstance(getInstrumentation())");
            writeLine("}");
        }
        writeEmptyLine();
    }

    /**
     * Writes appropriate test method header into the current In-Progress String test case.
     */
    private void writeTestMethodHeader() {
        writeAnnotationLine("Test");
        writeLine(String.format("public void %s() {", testMethodName));
        writeEmptyLine();
    }

    /**
     * Writes the InstrumentationTest actions collected so far into the current In-Progress String test case.
     */
    private void writeTestBody() {
        writeDelayStatement(Properties.DELAY_AFTER_ACTIVITY_STARTED());

        for (int i = 0; i < actions.size(); i++) {
            writeAssertionsMethodCallAtIndex(i, "full");
            writeAssertionsMethodCallAtIndex(i, "semifull");
            writeAssertionsMethodCallAtIndex(i, "diff");

            if (addDebugComments) {
                writeComment(String.format(Locale.US, "Executing action %d", i));
            }

            InstrumentationTestAction action = actions.get(i);
            writeExpressionLine(action.getCode());

            writeDelayStatement(Properties.ACTIONS_THROTTLE());

            // Add a close soft keyboard statement to the test only if this is NOT the last
            // action and if the action actually closed the soft keyboard after execution.
            boolean isLastAction = i == actions.size() - 1;
            if (!isLastAction && actionsThatClosedSoftKeyboard.contains(action)) {
                writeExpressionLine(closeSoftKeyboardAction.getCode());
            }

            if (addDebugComments) {
                writeComment(String.format("Current Activity is %s",
                        this.activitySequence.get(i + 1)));
            }

            writeEmptyLine();
        }

        writeAssertionsMethodCallAtIndex(actions.size(), "full");
        writeAssertionsMethodCallAtIndex(actions.size(), "semifull");
        writeAssertionsMethodCallAtIndex(actions.size(), "diff");

        if (addDebugComments) {
            if (this.lastActionResult != null) {
                writeComment(String.format("Last action result: %s", this.lastActionResult));
            } else {
                writeComment("Last action result: NULL");
            }
        }
    }

    private void writeDelayStatement(long delay) {
        if (delay > 0) {
            writeExpressionLine(String.format(Locale.US, "SystemClock.sleep(%d)", delay));
            writeEmptyLine();
        }
    }

    private void writeAssertionsMethodCallAtIndex(int index, String assertionType) {
        writeExpressionLine(String.format(Locale.US,
                "%sAssertionsBeforeAction%d()", assertionType, index));
        writeEmptyLine();
    }

    /**
     * Writes test method closing brace character into the current In-Progress String test case.
     */
    private void writeMethodFooter() {
        writeLine("}");
        writeEmptyLine();
    }

    /**
     * Writes test class closing brace character into the current In-Progress String test case.
     */
    private void writeClassFooter() {
        writeLine("}");
        writeEmptyLine();
    }

    private void writeAssertionMethods() {
        for (int i = 0; i < actions.size(); i++) {
            writeAssertionMethodsAtIndex(i, "full", fullAssertions);
            writeAssertionMethodsAtIndex(i, "semifull", semiFullAssertions);
            writeAssertionMethodsAtIndex(i, "diff", diffAssertions);
        }

        writeAssertionMethodsAtIndex(actions.size(), "full", fullAssertions);
        writeAssertionMethodsAtIndex(actions.size(), "semifull", semiFullAssertions);
        writeAssertionMethodsAtIndex(actions.size(), "diff", diffAssertions);
    }

    private void writeAssertionMethodsAtIndex(int actionIndex, String assertionType,
                                              @Nullable Map<Integer, List<EspressoAssertion>> assertions) {
        String mainMethodName = String.format(Locale.US,
                "%sAssertionsBeforeAction%d", assertionType, actionIndex);
        String assertionsBeforeActionMainMethodLine = String.format("public void %s() {",
                mainMethodName);

        if (assertions != null && assertions.containsKey(actionIndex)) {
            List<EspressoAssertion> espressoAssertions = assertions.get(actionIndex);

            // If we have less than the maximum number of assertions, we can write them all in the
            // same method. Otherwise, we need to split them into multiple methods.
            if (espressoAssertions.size() < MAXIMUM_ASSERTIONS_PER_METHODS) {
                writeLine(assertionsBeforeActionMainMethodLine);
                for (EspressoAssertion assertion : espressoAssertions) {
                    writeExpressionLine(assertion.getCode());
                }
                writeMethodFooter();
            } else {
                // We need to split the assertions into multiple methods. We will have a maximum of
                // MAXIMUM_ASSERTIONS_PER_METHODS assertions per method, except for the last method
                // which will have the remaining assertions.
                // The main method will call all the assertions methods in order.
                int numberOfMethods = espressoAssertions.size() / MAXIMUM_ASSERTIONS_PER_METHODS;
                int numberOfAssertionsInLastMethod = espressoAssertions.size() % MAXIMUM_ASSERTIONS_PER_METHODS;

                // Write the main method.
                writeLine(assertionsBeforeActionMainMethodLine);
                for (int i = 0; i < numberOfMethods; i++) {
                    String assertionsBeforeActionMethodName = String.format(Locale.US,
                            "%s_%d()", mainMethodName, i);
                    writeExpressionLine(assertionsBeforeActionMethodName);
                }
                if (numberOfAssertionsInLastMethod > 0) {
                    String assertionsBeforeActionMethodName = String.format(Locale.US,
                            "%s_%d()", mainMethodName, numberOfMethods);
                    writeExpressionLine(assertionsBeforeActionMethodName);
                }
                writeMethodFooter();

                // Write the assertions methods.
                for (int i = 0; i < numberOfMethods; i++) {
                    String assertionsBeforeActionMethodName = String.format(Locale.US,
                            "public void %s_%d() {", mainMethodName, i);
                    writeLine(assertionsBeforeActionMethodName);
                    for (int j = 0; j < MAXIMUM_ASSERTIONS_PER_METHODS; j++) {
                        EspressoAssertion assertion = espressoAssertions.get(i * MAXIMUM_ASSERTIONS_PER_METHODS + j);
                        writeExpressionLine(assertion.getCode());
                    }
                    writeMethodFooter();
                }
                if (numberOfAssertionsInLastMethod > 0) {
                    String assertionsBeforeActionMethodName = String.format(Locale.US,
                            "public void %s_%d() {", mainMethodName, numberOfMethods);
                    writeLine(assertionsBeforeActionMethodName);
                    for (int j = 0; j < numberOfAssertionsInLastMethod; j++) {
                        EspressoAssertion assertion = espressoAssertions.get(numberOfMethods * MAXIMUM_ASSERTIONS_PER_METHODS + j);
                        writeExpressionLine(assertion.getCode());
                    }
                    writeMethodFooter();
                }
            }
        } else {
            // If there are no assertions, we still need to write an empty method to avoid
            // compilation errors.
            writeLine(assertionsBeforeActionMainMethodLine);
            writeMethodFooter();
        }
    }

    /**
     * Converts an import into androidx or android.support as needed.
     */
    private String normalizeImport(String anImport) {
        if (anImport.contains("android.support") && espressoDependencyGroup.contains("androidx")) {
            return anImport.replace("android.support", "androidx");
        }

        if (anImport.contains("androidx") && espressoDependencyGroup.contains("android.support")) {
            return anImport.replace("androidx", "android.support");
        }

        return anImport;
    }
}
