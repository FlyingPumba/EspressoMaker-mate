package org.mate.representation.interaction;

import org.mate.commons.exceptions.AUTCrashException;
import org.mate.commons.interaction.action.Action;
import org.mate.commons.interaction.action.ActionExecutionResult;
import org.mate.commons.interaction.action.intent.SystemAction;
import org.mate.commons.utils.MATELog;
import org.mate.representation.ExplorationInfo;

import java.io.IOException;
import java.util.regex.Matcher;

/**
 * ActionExecutor class for System actions.
 */
public class SystemActionExecutor extends ActionExecutor {

    /**
     * Executes a given action.
     *
     * @param action The action to be executed.
     * @throws AUTCrashException Thrown when the action causes a crash of the application.
     */
    @Override
    public ActionExecutionResult perform(Action action) throws AUTCrashException {
        return executeAction((SystemAction) action);
    }

    /**
     * Simulates the occurrence of a system event.
     *
     * @param action The system event.
     */
    private ActionExecutionResult executeAction(SystemAction action) throws AUTCrashException {

        // the inner class separator '$' needs to be escaped
        String receiver = action.getReceiver().replaceAll("\\$", Matcher.quoteReplacement("\\$"));

        String tag;
        String component;

        if (action.isDynamicReceiver()) {
            /*
             * In the case we deal with a dynamic receiver, we can't specify the full component name,
             * since dynamic receivers can't be triggered by explicit intents! Instead, we can only
             * specify the package name in order to limit the receivers of the broadcast.
             */
            tag = "-p";
            component = ExplorationInfo.getInstance().getTargetPackageName();
        } else {
            tag = "-n";
            component = ExplorationInfo.getInstance().getTargetPackageName() + "/" + receiver;
        }

        try {
            device.executeShellCommand("su root am broadcast -a " + action.getAction()
                    + " " + tag + " " + component);
        } catch (IOException e) {
            MATELog.log_warn("Executing system action failed!");
            if (e.getMessage() != null) {
                MATELog.log_warn(e.getMessage());
            }

            return ActionExecutionResult.failure(e);
        }

        return ActionExecutionResult.success();
    }
}
