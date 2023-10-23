package org.mate.commons.interaction.action;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * The result of executing an action in the RepresentationLayer.
 */
public class ActionExecutionResult implements Parcelable {

    /**
     * Indicates whether the action's execution was successful or not.
     */
    private boolean success = false;

    /**
     * Indicates whether the action's execution caused the AUT to soft crash or not.
     * A soft crash is an exception in the AUT's code that caused the action to fail, but the AUT
     * is still running (i.e., it didn't bring the whole process down).
     */
    private boolean softCrash = false;

    /**
     * Indicates whether the action closed or not the soft keyboard after executing.
     */
    private boolean closedSoftKeyboard = false;

    /**
     * The exception that occurred when executing the action.
     */
    private Exception failureException;

    public ActionExecutionResult(boolean success) {
        this(success, false);
    }

    public ActionExecutionResult(boolean success, boolean softCrash) {
        this.success = success;
        this.softCrash = softCrash;
    }

    public static ActionExecutionResult wrapBooleanSuccess(boolean success) {
        return new ActionExecutionResult(success);
    }

    public static ActionExecutionResult success() {
        return new ActionExecutionResult(true);
    }

    public static ActionExecutionResult failure() {
        return new ActionExecutionResult(false);
    }

    public static ActionExecutionResult softCrash(Exception e) {
        ActionExecutionResult result = new ActionExecutionResult(false, true);
        result.setFailureException(e);
        return result;
    }

    public static ActionExecutionResult failure(Exception e) {
        ActionExecutionResult result = new ActionExecutionResult(false);
        result.setFailureException(e);
        return result;
    }

    public void setFailureException(Exception failureException) {
        this.failureException = failureException;
    }

    public void setClosedSoftKeyboard(boolean closedSoftKeyboard) {
        this.closedSoftKeyboard = closedSoftKeyboard;
    }

    public boolean isSuccess() {
        return success;
    }

    public boolean isClosedSoftKeyboard() {
        return closedSoftKeyboard;
    }

    public boolean isSoftCrash() {
        return softCrash;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.success ? (byte) 1 : (byte) 0);
        dest.writeByte(this.softCrash ? (byte) 1 : (byte) 0);
        dest.writeByte(this.closedSoftKeyboard ? (byte) 1 : (byte) 0);
        dest.writeSerializable(this.failureException);
    }

    public void readFromParcel(Parcel source) {
        this.success = source.readByte() != 0;
        this.softCrash = source.readByte() != 0;
        this.closedSoftKeyboard = source.readByte() != 0;
        this.failureException = (Exception) source.readSerializable();
    }

    protected ActionExecutionResult(Parcel in) {
        readFromParcel(in);
    }

    public static final Creator<ActionExecutionResult> CREATOR = new Creator<ActionExecutionResult>() {
        @Override
        public ActionExecutionResult createFromParcel(Parcel source) {
            return new ActionExecutionResult(source);
        }

        @Override
        public ActionExecutionResult[] newArray(int size) {
            return new ActionExecutionResult[size];
        }
    };
}
