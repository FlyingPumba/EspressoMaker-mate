package org.mate.commons.exceptions;

/**
 * Created by marceloeler on 09/03/17.
 */

public class AUTCrashException extends Exception {
    public AUTCrashException(String msg){
        super (msg);
    }
    public AUTCrashException(String msg, Throwable cause){
        super (msg, cause);
    }
}
