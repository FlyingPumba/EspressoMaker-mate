package org.mate.commons.interaction.action;

/**
 * The possible outcomes of applying an action.
 */
public enum ActionResult {
    FAILURE_UNKNOWN,
    FAILURE_EMULATOR_CRASH,
    FAILURE_APP_CRASH,
    SUCCESS,
    SUCCESS_OUTBOUND;

    /**
     * Indicates whether the action closed or not the soft keyboard after executing.
     */
    private boolean closedSoftKeyboard = false;

    public void setClosedSoftKeyboard(boolean closedSoftKeyboard) {
        this.closedSoftKeyboard = closedSoftKeyboard;
    }

    public boolean wasSoftKeyboardClosed() {
        return closedSoftKeyboard;
    }
}
