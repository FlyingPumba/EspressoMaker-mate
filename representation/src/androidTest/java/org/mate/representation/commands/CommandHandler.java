package org.mate.representation.commands;

import android.os.Debug;
import android.os.RemoteException;

import androidx.annotation.Nullable;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiSelector;

import org.mate.commons.IMATEServiceInterface;
import org.mate.commons.IRepresentationLayerInterface;
import org.mate.commons.exceptions.AUTCrashException;
import org.mate.commons.interaction.action.Action;
import org.mate.commons.interaction.action.ActionExecutionResult;
import org.mate.commons.interaction.action.espresso.EspressoAssertion;
import org.mate.commons.interaction.action.espresso.InstrumentationTestAction;
import org.mate.commons.interaction.action.ui.Widget;
import org.mate.commons.state.espresso.EspressoScreenSummary;
import org.mate.commons.utils.MATELog;
import org.mate.commons.utils.TimeoutRun;
import org.mate.representation.DeviceInfo;
import org.mate.representation.DynamicTest;
import org.mate.representation.ExplorationInfo;
import org.mate.representation.interaction.ActionExecutor;
import org.mate.representation.interaction.ActionExecutorFactory;
import org.mate.representation.state.widget.WidgetScreenParser;
import org.mate.representation.test.BuildConfig;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

/**
 * Handles commands requested by the MATE Service (e.g., fetch current available actions).
 */
public class CommandHandler extends IRepresentationLayerInterface.Stub {

    /**
     * The max time to wait for an action to be executed (1 min).
     */
    private final long EXECUTE_ACTION_TIMEOUT = 60 * 1000;

    public CommandHandler() {}

    @Override
    public void ping() throws RemoteException {
        MATELog.log_debug("RepresentationLayer received command ping");
        DynamicTest.updateLastCommandTimestamp();
        // Do nothing else. This method is used to test the connection between the MATE Service and
        // the Representation Layer.
    }

    @Override
    public void exit() throws RemoteException {
        MATELog.log_debug("RepresentationLayer received command exit");
        DynamicTest.keepRunning = false;
    }

    @Override
    public void waitForDebugger() throws RemoteException {
        MATELog.log_debug("RepresentationLayer received command waitForDebugger");
        DynamicTest.updateLastCommandTimestamp();
        if (!Debug.isDebuggerConnected()) {
            MATELog.log("MATE Representation Layer waiting for Debugger to be attached to Android " +
                    "Process");
            Debug.waitForDebugger();
        }
    }

    @Override
    public String getTargetPackageName() throws RemoteException {
        MATELog.log_debug("RepresentationLayer received command getTargetPackageName");
        DynamicTest.updateLastCommandTimestamp();
        return BuildConfig.TARGET_PACKAGE_NAME;
    }

    @Override
    public void setRandomSeed(long seed) throws RemoteException {
        MATELog.log_debug("RepresentationLayer received command setRandomSeed(" +
                seed + ")");
        DynamicTest.updateLastCommandTimestamp();
        try {
            ExplorationInfo.getInstance().setRandomSeed(seed);
        } catch (Exception e){
            logException(e);
            throw e;
        }
    }

    @Override
    public int getDisplayWidth() throws RemoteException {
        MATELog.log_debug("RepresentationLayer received command getDisplayWidth");
        DynamicTest.updateLastCommandTimestamp();
        try {
            return DeviceInfo.getInstance().getDisplayWidth();
        } catch (Exception e){
            logException(e);
            throw e;
        }
    }

    @Override
    public int getDisplayHeight() throws RemoteException {
        MATELog.log_debug("RepresentationLayer received command getDisplayHeight");
        DynamicTest.updateLastCommandTimestamp();
        try {
            return DeviceInfo.getInstance().getDisplayHeight();
        } catch (Exception e){
            logException(e);
            throw e;
        }
    }

    @Override
    public boolean grantRuntimePermission(String permission) throws RemoteException {
        MATELog.log_debug("RepresentationLayer received command grantRuntimePermission(" +
                permission + ")");
        DynamicTest.updateLastCommandTimestamp();
        try {
            return DeviceInfo.getInstance().grantRuntimePermission(permission);
        } catch (Exception e){
            logException(e);
            throw e;
        }
    }

    @Override
    public void disableAnimations() throws RemoteException {
        MATELog.log_debug("RepresentationLayer received command disableAnimations");
        DynamicTest.updateLastCommandTimestamp();
        try {
            DeviceInfo.getInstance().disableAnimations();
        } catch (Exception e){
            logException(e);
            throw e;
        }
    }

    @Override
    public boolean isCrashDialogPresent() throws RemoteException {
        MATELog.log_debug("RepresentationLayer received command isCrashDialogPresent");
        DynamicTest.updateLastCommandTimestamp();
        try {
            return DeviceInfo.getInstance().isCrashDialogPresent();
        } catch (Exception e){
            logException(e);
            throw e;
        }
    }

    @Override
    public String getTargetPackageFilesDir() throws RemoteException {
        MATELog.log_debug("RepresentationLayer received command getTargetPackageFilesDir");
        DynamicTest.updateLastCommandTimestamp();
        try {
            return DeviceInfo.getInstance().getTargetPackageFilesDir();
        } catch (Exception e){
            logException(e);
            throw e;
        }
    }

    @Override
    public void sendBroadcastToTracer() throws RemoteException {
        MATELog.log_debug("RepresentationLayer received command sendBroadcastToTracer");
        DynamicTest.updateLastCommandTimestamp();
        try {
            ExplorationInfo.getInstance().sendBroadcastToTracer();
        } catch (Exception e){
            logException(e);
            throw e;
        }
    }

    @Override
    public String getCurrentPackageName() throws RemoteException {
        MATELog.log_debug("RepresentationLayer received command getCurrentPackageName");
        DynamicTest.updateLastCommandTimestamp();
        try {
            return ExplorationInfo.getInstance().getCurrentPackageName();
        } catch (Exception e){
            logException(e);
            throw e;
        }
    }

    @Override
    public String getCurrentActivityName() throws RemoteException {
        MATELog.log_debug("RepresentationLayer received command getCurrentActivityName");
        DynamicTest.updateLastCommandTimestamp();
        try {
            return ExplorationInfo.getInstance().getCurrentActivityName();
        } catch (Exception e){
            logException(e);
            throw e;
        }
    }

    @Override
    public List<String> getTargetPackageActivityNames() throws RemoteException {
        MATELog.log_debug("RepresentationLayer received command getTargetPackageActivityNames");
        DynamicTest.updateLastCommandTimestamp();
        try {
            return ExplorationInfo.getInstance().getTargetPackageActivityNames();
        } catch (Exception e){
            logException(e);
            throw e;
        }
    }

    @Override
    public String executeShellCommand(String command) throws RemoteException {
        MATELog.log_debug("RepresentationLayer received command executeShellCommand(" +
                command + ")");
        DynamicTest.updateLastCommandTimestamp();
        try {
            return DeviceInfo.getInstance().executeShellCommand(command);
        } catch (Exception e){
            logException(e);
            throw e;
        }
    }

    @Override
    public ActionExecutionResult executeAction(Action action) throws RemoteException {
        MATELog.log_debug("RepresentationLayer received command executeAction(" +
                action + ")");
        DynamicTest.updateLastCommandTimestamp();
        try {
            if (action == null) {
                MATELog.log_error("Trying to execute null action");
                throw new IllegalStateException("executeAction method on representation layer was " +
                        "called for a null action");
            }

            ActionExecutor executor = ActionExecutorFactory.getExecutor(action);

            final ActionExecutionResult[] result = {ActionExecutionResult.failure()};

            TimeoutRun.timeoutRun(() -> {
                try {
                    result[0] = executor.perform(action);
                } catch (Exception e) {
                    MATELog.log_error(
                            "An exception occurred executing action on representation layer: " +
                                    e.getMessage());

                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    e.printStackTrace(pw);
                    MATELog.log_error(sw.toString());

                    if (e instanceof AUTCrashException) {
                        result[0] = ActionExecutionResult.softCrash(e);
                    } else {
                        result[0] = ActionExecutionResult.failure(e);
                    }
                } finally {
                    ExplorationInfo.getInstance().invalidateEspressoScreenInfo();
                }

                return null;
            }, EXECUTE_ACTION_TIMEOUT);

            return result[0];

        } catch (Exception e){
            logException(e);
            throw e;
        }
    }

    @Override
    public boolean clickUiObjectWithText(String text) throws RemoteException {
        MATELog.log_debug("RepresentationLayer received command clickUiObjectWithText(" +
                text + ")");
        DynamicTest.updateLastCommandTimestamp();

        try {
            UiDevice device = DeviceInfo.getInstance().getUiDevice();
            UiObject uiObject = device.findObject(new UiSelector().text(text));
            if (uiObject.exists()) {
                return uiObject.click();
            } else {
                return false;
            }
        } catch (Exception e){
            logException(e);
            throw new RemoteException(e.getMessage());
        }
    }

    @Override
    public boolean executeAssertion(EspressoAssertion assertion) throws RemoteException {
        MATELog.log_debug("RepresentationLayer received command executeAssertion(" +
                assertion + ")");
        DynamicTest.updateLastCommandTimestamp();
        return assertion.execute();
    }

    @Override
    public List<Widget> getCurrentScreenWidgets() throws RemoteException {
        MATELog.log_debug("RepresentationLayer received command getCurrentScreenWidgets");
        DynamicTest.updateLastCommandTimestamp();
        try {
            return new WidgetScreenParser().getWidgets();
        } catch (Exception e) {
            logException(e);
            throw e;
        }
    }

    @Override
    public @Nullable List<InstrumentationTestAction> getCurrentScreenInstrumentationTestActions() throws RemoteException {
        MATELog.log_debug("RepresentationLayer received command getCurrentScreenInstrumentationTestActions");
        DynamicTest.updateLastCommandTimestamp();
        try {
            return ExplorationInfo.getInstance().getEspressoScreenParser().getActions();
        } catch (Exception e) {
            logException(e);
            throw e;
        }
    }

    @Override
    public @Nullable
    EspressoScreenSummary getCurrentEspressoScreenSummary() throws RemoteException {
        MATELog.log_debug("RepresentationLayer received command getCurrentEspressoScreenSummary");
        DynamicTest.updateLastCommandTimestamp();
        try {
            return ExplorationInfo.getInstance().getEspressoScreenParser().getEspressoScreenSummary();
        } catch (Exception e) {
            logException(e);
            throw e;
        }
    }

    @Override
    public int getTopWindowType() throws RemoteException {
        MATELog.log_debug("RepresentationLayer received command getTopWindowType");
        DynamicTest.updateLastCommandTimestamp();
        try {
            return ExplorationInfo.getInstance().getTopWindowType();
        } catch (Exception e) {
            logException(e);
            throw e;
        }
    }

    @Override
    public void setReplayMode() throws RemoteException {
        MATELog.log_debug("RepresentationLayer received command setReplayMode");
        DynamicTest.updateLastCommandTimestamp();
        try {
            ExplorationInfo.getInstance().setReplayMode();
        } catch (Exception e) {
            logException(e);
            throw e;
        }
    }

    @Override
    public void setWidgetBasedActions() throws RemoteException {
        MATELog.log_debug("RepresentationLayer received command setWidgetBasedActions");
        DynamicTest.updateLastCommandTimestamp();
        try {
            ExplorationInfo.getInstance().setWidgetBasedActions();
        } catch (Exception e) {
            logException(e);
            throw e;
        }
    }

    public void setMateService(IMATEServiceInterface mateService) {
        // do nothing, for now
    }

    /**
     * Log an exception that happened in the Representation Layour
     * @param e the exception to log.
     */
    private void logException(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw, true);
        e.printStackTrace(pw);
        String stackTrace = sw.toString();

        MATELog.log_error(String.format("Exception occurred in Representation Layer: %s",
                stackTrace));
    }
}
