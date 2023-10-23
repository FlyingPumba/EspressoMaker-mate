package org.mate.representation.state.espresso;

import android.app.Activity;
import android.os.Looper;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.test.espresso.Root;
import androidx.test.espresso.base.ActiveRootLister;
import androidx.test.espresso.util.EspressoOptional;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import androidx.test.runner.lifecycle.Stage;

import org.mate.commons.state.espresso.EspressoRoots;
import org.mate.commons.utils.MATELog;
import org.mate.commons.utils.TimeoutRun;
import org.mate.representation.DeviceInfo;
import org.mate.representation.ExplorationInfo;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Parses the Roots on the current screen using the Espresso framework.
 */
public class EspressoRootsFetcher {

    /**
     * The max time to wait for the fetching of Espresso roots (5 seconds).
     * This is needed because the method runOnMainSync can get stuck (rare, but happens).
     */
    public static final int FETCH_ROOTS_TIMEOUT = 5 * 1000;

    /**
     * Fetches the roots of the current Activity in Resumed state.
     *
     * This method's implementation is inspired by code from the Android test open source project.
     * In particular, we will use reflection to access the {@link androidx.test.espresso.base.RootsOracle#listActiveRoots}
     * method. We need to use reflection because the RootsOracle class is private in the androidx
     * testing library.
     * URL: https://github.com/android/android-test/blob/master/espresso/core/java/androidx/test/espresso/base/RootsOracle.java
     *
     * Below is the documentation of the RootsOracle class, for context:
     *
     * Provides access to all root views in an application.
     *
     * <p>95% of the time this is unnecessary and we can operate solely on current Activity's
     * root view as indicated by getWindow().getDecorView(). However in the case of popup
     * windows, menus, and dialogs the actual view hierarchy we should be operating on is not
     * accessible through public apis.
     *
     * <p>In the spirit of degrading gracefully when new api levels break compatibility, callers
     * handle a list of size 0 by assuming getWindow().getDecorView() on the currently resumed
     * is the sole root - this assumption will be correct often enough.
     *
     * <p>Obviously, you need to be on the main thread to use this.
     */
    public static @Nullable EspressoRoots fetch() {
        final EspressoRoots[] espressoRoots = {null};

        TimeoutRun.timeoutRun(() -> {
            DeviceInfo.getInstance().getInstrumentation().runOnMainSync(() -> {
                MATELog.log_debug("Parsing Espresso Screen Roots on main thread");

                ArrayList<Activity> resumedActivities = new ArrayList<>(
                        ActivityLifecycleMonitorRegistry.getInstance()
                                .getActivitiesInStage(Stage.RESUMED));

                if (resumedActivities.size() == 0) {
                    // No activity is found in resumed state, we probably left the AUT.
                    MATELog.log_debug("No resumed activities found, unable to get root view");
                    return;
                }

                Activity topActivity = resumedActivities.get(0);

                if (!ExplorationInfo.getInstance().getTargetPackageName().equals(
                        topActivity.getPackageName())) {
                    // The resumed top activity is for a different package name than the one we are
                    // targeting. Exit this function as if we haven't found a root view.
                    MATELog.log_debug("Resumed activity is for a different package");
                    return;
                }

                List<Root> roots;
                try {
                    // Create a new instance of the RootsOracle class
                    Class rootsOracleClass = Class.forName("androidx.test.espresso.base.RootsOracle");
                    Constructor<ActiveRootLister> rootsOracleConstructor = rootsOracleClass.getDeclaredConstructor(new Class[]{Looper.class});
                    rootsOracleConstructor.setAccessible(true);
                    ActiveRootLister sRootsOracle = rootsOracleConstructor.newInstance(Looper.getMainLooper());

                    // Call the method listActiveRoots
                    Method sRootsOracle_listActiveRoots = rootsOracleClass.getMethod("listActiveRoots");
                    roots = (List) sRootsOracle_listActiveRoots.invoke(sRootsOracle);
                } catch (Exception e) {
                    MATELog.log_error("A problem occurred listing roots: " + e.getMessage());
                    throw new RuntimeException("A problem occurred listing roots", e);
                }

                if (roots != null && roots.size() > 0) {
                    List<Root> usableRoots = filterRoots(roots, topActivity);

                    if(usableRoots.size() > 0) {
                        MATELog.log("Usable roots were found");
                        espressoRoots[0] = new EspressoRoots(usableRoots, resumedActivities);
                    } else {
                        MATELog.log_error("Roots oracle found roots but we were unable to use them");
                    }
                } else {
                    MATELog.log_error("Roots oracle returned no roots");
                }
            });

            return null;
        }, FETCH_ROOTS_TIMEOUT);

        if (espressoRoots[0] == null) {
            MATELog.log_error("Unable to find roots on a resumed activity using the Espresso " +
                    "framework");
            return null;
        }

        return espressoRoots[0];
    }

    /**
     * Filter out the roots that belong to other activities that are not the current top activity.
     * @param roots The list of roots to filter.
     * @param topActivity The current top activity.
     * @return A list of roots that belong to the current top activity.
     */
    private static List<Root> filterRoots(List<Root> roots, Activity topActivity) {
        ArrayList<Root> usableRoots = new ArrayList<>();

        // We prepare the full name of the top activity
        // ("package_name/fully_qualified_activity_name")
        String topActivityPackageName = topActivity.getPackageName();
        String topActivityFullyQualifiedName = topActivity.getClass().getName();
        String topActivityFullName = topActivityPackageName + "/" + topActivityFullyQualifiedName;

        List<String> targetPackageActivities = ExplorationInfo.getInstance().getTargetPackageActivityNames();

        for (Root root : roots) {
            try {
                // For normal views, the layout params will hold a reference to the activity.
                EspressoOptional<WindowManager.LayoutParams> optionalLayoutParams = root.getWindowLayoutParams();

                String rootTitleString = null;
                String rootPackageNameString = null;

                if (optionalLayoutParams.isPresent()) {
                    WindowManager.LayoutParams layoutParams = optionalLayoutParams.get();

                    // Title contains the Class name of the activity where the root is begin
                    // referenced. Similar with the package name.
                    rootTitleString = layoutParams.getTitle() != null ?
                            layoutParams.getTitle().toString() : null;
                    rootPackageNameString = layoutParams.packageName;
                }

                // Check that the title is present (not null and not empty). If it is not, it likely
                // is because the view is attached by the system (e.g., an AlertDialog).
                if (rootTitleString != null && !rootTitleString.isEmpty()) {
                    if (shouldRootBeSkipped(root, rootTitleString, topActivityFullName,
                            targetPackageActivities)) {
                        MATELog.log_debug("Discarding root with title: " + rootTitleString +
                                " and package name: " + rootPackageNameString);
                    } else {
                        usableRoots.add(root);
                    }
                } else {
                    // The title was null or empty. We need to inspect the attach info to know to
                    // which activity does this root belongs to.
                    View decorView = root.getDecorView();

                    // Get value of decorView.mAttachInfo field.
                    // This field belongs to the View class, so we need to take it from there.
                    Field attachInfoField = View.class.getDeclaredField("mAttachInfo");
                    attachInfoField.setAccessible(true);
                    Object attachInfoObj = attachInfoField.get(decorView);

                    // Get value of attachInfoObj.mViewRootImpl field.
                    Field viewRootField = attachInfoObj.getClass().getDeclaredField("mViewRootImpl");
                    viewRootField.setAccessible(true);
                    Object viewRootObj = viewRootField.get(attachInfoObj);

                    // Get value of viewRootObj.mPendingInputEventQueueLengthCounterName field.
                    // Pending input events are input events waiting to be delivered to the input
                    // stages and handled by the application. This attribute holds its 'owner'.
                    Field mPendingInputField = viewRootObj.getClass().getDeclaredField("mPendingInputEventQueueLengthCounterName");
                    mPendingInputField.setAccessible(true);
                    Object pendingInputObj = mPendingInputField.get(viewRootObj);

                    String pendingInputString = (String) pendingInputObj;
                    if (pendingInputString != null) {
                        if (shouldRootBeSkipped(root, pendingInputString, topActivityFullName,
                                targetPackageActivities)) {
                            MATELog.log_debug("Discarding root with pending input: " +
                                    pendingInputString);
                        } else {
                            usableRoots.add(root);
                        }
                    }
                }
            } catch (Exception e) {
                MATELog.log_warn("Unable to get the activity owner of a root: " +
                        e.getMessage());
            }
        }

        return usableRoots;
    }

    /**
     * If present, root title string can have many different shapes, such as:
     * - "<package_name>/<fully_qualified_activity_name>".
     * - "PopupWindow:<hash>".
     * - "Toast"
     * - "TooltipPopup"
     * Or any other string that the user may have chosen for the title of an AlertDialog.
     * <p>
     * In any case, we only care that the root does not belong to another activity, and that it
     * is not a Toast or TooltipPopup (these two cause flakiness).
     * We can't enforce a whitelist of allowed titles because the user may have changed the title
     * of the AlertDialog. Thus, it is better to enforce a blacklist of titles that we know we
     * can skip.
     *
     * @param root                    The root to check.
     * @param rootTitle               The owner of the root.
     * @param topActivityFullName     The full name of the top activity.
     * @param targetPackageActivities The list of activities of the target package.
     * @return True if the root should be skipped, false otherwise.
     */
    public static boolean shouldRootBeSkipped(Root root, String rootTitle, String topActivityFullName,
                                              List<String> targetPackageActivities) {
        boolean rootBelongsToTopActivity = rootTitle.endsWith(topActivityFullName);
        if (rootBelongsToTopActivity) {
            // never skip a root that obviously belongs to the top activity
            MATELog.log_debug("Root belongs to top activity");
            return false;
        }

        for (String activity : targetPackageActivities) {
            if (rootTitle.endsWith(activity)) {
                // skip root if it belongs to an activity in AUT that is not the current top one.
                MATELog.log_debug("Root belongs to an activity in AUT that is not the current top one");
                return true;
            }
        }

        if (isActionBarContextPopup(root)) {
            // skip root if it is a PopupWindow that belongs to a Context ActionBar.
            MATELog.log_debug("Root is a PopupWindow that belongs to a Context ActionBar");
            return true;
        }

        boolean rootIsToast = rootTitle.equals("Toast");
        MATELog.log_debug("Root is toast: " + rootIsToast);
        boolean rootIsTooltipPopup = rootTitle.equals("TooltipPopup");
        MATELog.log_debug("Root is tooltip popup: " + rootIsTooltipPopup);

        // Skip roots that are Toasts or TooltipPopups, since trying to target them produces
        // flakiness.
        return rootIsToast || rootIsTooltipPopup;
    }

    /**
     * Returns true if the root is a PopupWindow that belongs to a Context ActionBar.
     * @param root
     * @return
     */
    public static boolean isActionBarContextPopup(Root root) {
        boolean isPopupViewContainer = root.getDecorView().getClass().getName().equals(
                "android.widget.PopupWindow$PopupViewContainer");
        if (!isPopupViewContainer) {
            return false;
        }

        FrameLayout decorView = (FrameLayout) root.getDecorView();
        boolean isActionBarContextView = decorView.getChildCount() > 0 &&
                decorView.getChildAt(0).getClass().getName().equals(
                        "com.android.internal.widget.ActionBarContextView");

        return isActionBarContextView;
    }
}
