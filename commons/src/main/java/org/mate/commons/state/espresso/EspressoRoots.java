package org.mate.commons.state.espresso;

import android.app.Activity;

import androidx.test.espresso.Root;

import java.util.ArrayList;
import java.util.List;

/**
 * Auxiliary class to hold the Roots and resumed activities fetched from the current screen using
 * the Espresso framework.
 */
public class EspressoRoots {

    /**
     * The Window roots provided by the Espresso framework.
     */
    private final List<Root> roots;

    /**
     * The resumed activities provided by the Espresso framework.
     */
    private final ArrayList<Activity> resumedActivities;

    public EspressoRoots(List<Root> roots, ArrayList<Activity> resumedActivities) {
        this.roots = roots;
        this.resumedActivities = resumedActivities;
    }

    public ArrayList<Activity> getResumedActivities() {
        return resumedActivities;
    }

    public List<Root> getRoots() {
        return roots;
    }

    public int getTopWindowType() {
        int topWindowType = 0;
        if (roots.size() > 0) {
            topWindowType = roots.get(0).getWindowLayoutParams().get().type;
        }
        return topWindowType;
    }
}
