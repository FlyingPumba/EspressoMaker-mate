package org.mate.commons.interaction.action.espresso.executables.recyclerview;

import android.view.View;

import org.mate.commons.utils.MATELog;
import org.mate.commons.utils.Optional;

import java.lang.reflect.Method;

public class RecyclerViewWrapper {
    private final View wrappedView;

    public RecyclerViewWrapper(View view) {
        wrappedView = view;
    }

    public static Optional<RecyclerViewWrapper> createFor(View view) {
        String viewClassName = view.getClass().getName();
        String viewClassSimpleName = view.getClass().getSimpleName();

        // MATE imports the RecyclerViewActions from the AndroidX test library (and the same for
        // other common Espresso actions). These actions assume that classes, and in particular
        // the RecyclerView, are imported from the androidx library. E.g., check https://github.com/android/android-test/blob/8177e53cd87077825bfbf8879225369ff0a9c3ad/espresso/contrib/java/androidx/test/espresso/contrib/RecyclerViewActions.java#L25
        // This means that if we try to use RecyclerViewActions on an (old) app that still uses
        // the Android support libraries, it will throw a "NoClassDefFoundError: Failed resolution
        // of: Landroidx/recyclerview/widget/RecyclerView".
        // Unfortunately, there is no way to have Android support and AndroidX at the same time in
        // MATE (or in any APK for what is worth), so we ared force to choose one or the other.
        // We choose to use AndroidX libraries since it is the most recent one (and more used
        // nowadays).
        // Thus, we won't recognize views as RecyclerViews if they come from the Android support
        // package, since it will only lead to errors like the one mentioned above.
        boolean isRecyclerView = viewClassSimpleName.contains("RecyclerView") &&
                !viewClassName.contains("android.support");

        if (isRecyclerView) {
            return Optional.some(new RecyclerViewWrapper(view));
        }

        return Optional.none();
    }

    public boolean hasAdapter() {
        return getAdapter() != null;
    }

    public int getItemCount() {
        Object adapter = getAdapter();
        if (adapter == null) {
            throw new IllegalStateException("Cannot get item count from RecyclerView without adapter");
        }

        try {
            Method getItemCountMethod = adapter.getClass().getMethod("getItemCount");
            return (int) getItemCountMethod.invoke(adapter);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot get item count from RecyclerView adapter");
        }
    }

    private Object getAdapter() {
        try {
            Class recyclerViewClass = wrappedView.getClass();
            Method getAdapterMethod = recyclerViewClass.getMethod("getAdapter");
            return getAdapterMethod.invoke(wrappedView);
        } catch (Exception e) {
            MATELog.log_error("An error occurred trying to get adapter from RecyclerView: " +
                    e.getMessage());
        }

        return null;
    }
}
