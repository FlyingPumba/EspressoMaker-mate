package org.mate.commons.state.espresso;

import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static androidx.test.espresso.matcher.RootMatchers.isSystemAlertWindow;

import android.app.Activity;

import androidx.annotation.Nullable;
import androidx.test.espresso.Root;

import org.mate.commons.interaction.action.espresso.interactions.InstrumentationTestInteraction;
import org.mate.commons.utils.MATELog;

import java.util.ArrayList;
import java.util.List;

public class EspressoScreen {

    /**
     * A list of the Espresso Windows on the current AUT's screen.
     */
    private final List<EspressoWindow> espressoWindows = new ArrayList<>();

    /**
     * A list of the Activities in RESUMED stage for the current screen.
     */
    private final List<Activity> resumedActivities;

    public EspressoScreen(EspressoRoots espressoRoots) {
        this.resumedActivities = espressoRoots.getResumedActivities();
        List<Root> roots = espressoRoots.getRoots();

        MATELog.log_debug("Found " + roots.size() + " roots for " +
                resumedActivities.size() + " resumed activities.");

        List<Root> orderedRoots = getOrderedRoots(roots);

        for (int i = 0; i < orderedRoots.size(); i++) {
            MATELog.log_debug("Parsing root with index " + i);
            Root root = orderedRoots.get(i);
            String activityName = root.getDecorView().getContext().getClass().getName();
            EspressoWindow window = new EspressoWindow(activityName, root);
            espressoWindows.add(window);
        }

        MATELog.log_debug("Found " + espressoWindows.size() + " Espresso windows");
    }

    public List<Activity> getResumedActivities() {
        return resumedActivities;
    }

    public List<EspressoWindow> getWindows() {
        return this.espressoWindows;
    }

    public EspressoScreenSummary getSummary() {
        return new EspressoScreenSummary(this);
    }

    public @Nullable
    ArrayList<InstrumentationTestInteraction> getInteractionsInScreenForUniqueId(String uniqueId) {
        for (EspressoWindow espressoWindow : espressoWindows) {
            ArrayList<InstrumentationTestInteraction> interactions = espressoWindow.getInteractions(uniqueId);
            if (interactions != null) {
                return interactions;
            }
        }
        return null;
    }

    public EspressoWindow getTopWindow() {
        return espressoWindows.get(0);
    }

    /**
     * Returns a list of the roots ordered by top-most window.
     *
     * @param roots the list of roots to order.
     * @return a list of the roots ordered by top-most window.
     */
    private List<Root> getOrderedRoots(List<Root> roots) {
        List<Root> orderedRoots = new ArrayList<>();

        int originalSize = roots.size();
        for (int i = 0; i < originalSize; i++) {
            Root topMostRoot = getTopMostRoot(roots);

            orderedRoots.add(topMostRoot);
            roots.remove(topMostRoot);
        }

        return orderedRoots;
    }

    /**
     * Return the top-most root from a list of roots.
     * <p>
     * The implementation for this method was inspired from the getRootFromMultipleRoots method
     * taken from the Android test open source project.
     * URL: https://github.com/android/android-test/blob/eaddf2ba037e79ac13a2e70c716773ed4c7e3cf2/espresso/core/java/androidx/test/espresso/base/RootViewPicker.java
     *
     * <p>
     * If there are multiple roots, pick one root window to interact with. By default we try to
     * select the top most window, except for dialogs were we return the dialog window.
     *
     * <p>Multiple roots only occur:
     *
     * <ul>
     *   <li>When multiple activities are in some state of their lifecycle in the application. We
     *       don't care about this, since we only want to interact with the RESUMED activity, all
     *       other Activities windows are not visible to the user so, out of scope.
     *   <li>When a {@link android.widget.PopupWindow} or {@link
     *       android.support.v7.widget.PopupMenu} is used. This is a case where we definitely want
     *       to consider the top most window, since it probably has the most useful info in it.
     *   <li>When an {@link android.app.Dialog} is shown. Again, this is getting all the users
     *       attention, so it gets the test attention too.
     * </ul>
     *
     * @param roots the roots found in current window.
     */
    private Root getTopMostRoot(List<Root> roots) {
        Root topMostRoot = null;

        for (int i = 0; i < roots.size(); i++) {
            Root currentRoot = roots.get(i);

            boolean isDialogWindow = isDialog().matches(currentRoot);
            boolean isPopupWindow = isPlatformPopup().matches(currentRoot);
            boolean isSystemAlertWindow = isSystemAlertWindow().matches(currentRoot);
            if (isDialogWindow || isPopupWindow || isSystemAlertWindow) {
                return currentRoot;
            }

            if (topMostRoot == null) {
                topMostRoot = currentRoot;
            } else if (isTopmostRoot(topMostRoot, currentRoot)) {
                topMostRoot = currentRoot;
            }
        }

        return topMostRoot;
    }

    /**
     * Returns a boolean indicating whether a newly found root is in a higher position than the
     * top-most root found so far.
     *
     * @param topMostRoot the top most root found so far.
     * @param newRoot     the new root.
     * @return a boolean.
     */
    private boolean isTopmostRoot(Root topMostRoot, Root newRoot) {
        return newRoot.getWindowLayoutParams().get().type
                > topMostRoot.getWindowLayoutParams().get().type;
    }
}
