package org.mate.representation.interaction;

import org.mate.commons.exceptions.AUTCrashException;
import org.mate.commons.input_generation.Mutation;
import org.mate.commons.interaction.action.Action;
import org.mate.commons.interaction.action.ActionExecutionResult;
import org.mate.commons.interaction.action.espresso.InstrumentationTestAction;
import org.mate.commons.interaction.action.espresso.executables.views.CloseSoftKeyboardExecutable;
import org.mate.commons.interaction.action.espresso.executables.views.ToggleRotationExecutable;
import org.mate.commons.interaction.action.espresso.interactions.EspressoViewInteraction;
import org.mate.commons.interaction.action.espresso.view_matchers.base.IsRootViewMatcher;
import org.mate.commons.state.espresso.EspressoRoots;
import org.mate.commons.utils.MATELog;
import org.mate.representation.DeviceInfo;
import org.mate.representation.ExplorationInfo;

/**
 * ActionExecutor class for InstrumentationTest actions.
 */
public class InstrumentationTestActionExecutor extends ActionExecutor {

    public InstrumentationTestActionExecutor() {
        super();

        Mutation.setRandom(ExplorationInfo.getInstance().getRandom());
    }

    /**
     * Executes a given action.
     *
     * @param action The action to be executed.
     * @throws AUTCrashException Thrown when the action causes a crash of the application.
     */
    @Override
    public ActionExecutionResult perform(Action action) throws AUTCrashException {
        return executeAction((InstrumentationTestAction) action);
    }

    /**
     * Executes an InstrumentationTest action.
     *
     * @param action The action to be executed.
     * @throws AUTCrashException Thrown when the action causes a crash of the application.
     */
    private ActionExecutionResult executeAction(InstrumentationTestAction action) throws AUTCrashException {
        String targetPackageName = ExplorationInfo.getInstance().getTargetPackageName();
        ActionExecutionResult result =
                ActionExecutionResult.wrapBooleanSuccess(action.execute(targetPackageName));

        if (result.isSuccess() && action.getExecutable() instanceof ToggleRotationExecutable) {
            // Rotation changed, update our internal status
            DeviceInfo.getInstance().toggleInPortraitMode();
        }

        // If it is opened, close the soft keyboard.
        // We only do this if we are in the AUT. If we are not, then we don't care about the soft
        // keyboard since the Test case will finish at this point anyway.

        String currentPackageName = ExplorationInfo.getInstance().getCurrentPackageName();
        if (currentPackageName == null) {
            // Somehow we lost connectivity with the UiAutomator. We treat this as an AUT crash,
            // since it is the most likely cause. Also, we can't keep evaluating the following code
            // if we don't know the current package name.
            throw new AUTCrashException("Lost connectivity with UiAutomator");
        }

        boolean explorationInAUT = currentPackageName.equals(targetPackageName);
        if (explorationInAUT && DeviceInfo.getInstance().isKeyboardOpened()) {
            MATELog.log_debug("Keyboard is opened after executing action");
            // Check that we can use the close soft keyboard action before actually executing it.
            CloseSoftKeyboardExecutable closeSoftKeyboardExecutable = new CloseSoftKeyboardExecutable();

            EspressoRoots espressoRoots = ExplorationInfo.getInstance().getEspressoRoots();
            if (espressoRoots.getResumedActivities().size() == 1) {
                MATELog.log_debug("Executing close soft keyboard action");
                InstrumentationTestAction closeSoftKeyboardAction = new InstrumentationTestAction(
                        closeSoftKeyboardExecutable,
                        new EspressoViewInteraction(new IsRootViewMatcher()),
                        null);
                result.setClosedSoftKeyboard(closeSoftKeyboardAction.execute(targetPackageName));
            } else {
                MATELog.log_debug("Unable to execute close soft keyboard action: not valid for view in screen.");
            }
        }

        return result;
    }
}
