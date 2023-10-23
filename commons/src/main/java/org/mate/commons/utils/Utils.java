package org.mate.commons.utils;

/**
 * A collection of utility functions.
 */
public class Utils {

    private Utils() {
        throw new UnsupportedOperationException("Utility class!");
    }

    /**
     * Suspend the current thread for a given time amount.
     *
     * @param time The time amount in milliseconds describing how long the thread should sleep.
     */
    public static void sleep(long time) {
        if (time <= 0) {
            return;
        }

        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
