package org.mate.commons.state.espresso;

import android.app.Activity;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import org.mate.commons.interaction.action.espresso.interactions.InstrumentationTestInteraction;
import org.mate.commons.interaction.action.espresso.root_matchers.EspressoRootMatcher;
import org.mate.commons.interaction.action.espresso.root_matchers.EspressoRootMatcherType;
import org.mate.commons.utils.MATELog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EspressoScreenSummary implements Parcelable {

    private List<String> resumedActivitiesNames = new ArrayList<>();
    private List<EspressoWindowSummary> espressoWindowSummaries = new ArrayList<>();

    public EspressoScreenSummary(EspressoScreen espressoScreen) {
        for (EspressoWindow espressoWindow : espressoScreen.getWindows()) {
            espressoWindowSummaries.add(espressoWindow.getSummary());
        }
        for (Activity resumedActivity : espressoScreen.getResumedActivities()) {
            resumedActivitiesNames.add(resumedActivity.getClass().getName());
        }
    }

    public List<String> getResumedActivitiesNames() {
        return resumedActivitiesNames;
    }

    public int getTopWindowType() {
        return this.espressoWindowSummaries.get(0).getWindowType();
    }

    public Map<String, ArrayList<InstrumentationTestInteraction>> getInteractionsInScreen() {
        Map<String, ArrayList<InstrumentationTestInteraction>> result = new HashMap<>();

        for (EspressoWindowSummary espressoWindow : espressoWindowSummaries) {
            Map<String, ArrayList<InstrumentationTestInteraction>> viewInteractions = espressoWindow.getInteractions();
            result.putAll(viewInteractions);
        }

        return result;
    }

    public Map<String, ArrayList<InstrumentationTestInteraction>> getInteractionsInTopWindow() {
        return espressoWindowSummaries.get(0).getInteractions();
    }

    /**
     * Get Interactions from an old summary that not longer exist in this summary.
     *
     * @param oldSummary The old summary.
     * @return The Interactions that are not in this summary.
     */
    public Map<String, ArrayList<InstrumentationTestInteraction>> getDisappearingInteractions(EspressoScreenSummary oldSummary) {
        Map<String, ArrayList<InstrumentationTestInteraction>> disappearingInteractions = new HashMap<>();

        Map<String, ArrayList<InstrumentationTestInteraction>> oldInteractions = oldSummary.getInteractionsInScreen();
        List<InstrumentationTestInteraction> newInteractions = flatten(getInteractionsInScreen().values());

        for (Map.Entry<String, ArrayList<InstrumentationTestInteraction>> entry : oldInteractions.entrySet()) {
            ArrayList<InstrumentationTestInteraction> oldInteractionsForView = entry.getValue();

            for (InstrumentationTestInteraction oldInteraction : oldInteractionsForView) {
                if (!containsInteraction(newInteractions, oldInteraction)) {
                    addInteractionToDictionary(disappearingInteractions, entry.getKey(), oldInteraction);
                }
            }
        }

        return disappearingInteractions;
    }

    /**
     * Get Interactions from this summary that are not in an old summary.
     *
     * @param oldSummary The old summary.
     * @return The Interactions that are not in the old summary.
     */
    public Map<String, ArrayList<InstrumentationTestInteraction>> getAppearingInteractions(EspressoScreenSummary oldSummary) {
        Map<String, ArrayList<InstrumentationTestInteraction>> appearingInteractions = new HashMap<>();

        List<InstrumentationTestInteraction> oldInteractions = flatten(oldSummary.getInteractionsInScreen().values());
        Map<String, ArrayList<InstrumentationTestInteraction>> newInteractions = getInteractionsInScreen();

        for (Map.Entry<String, ArrayList<InstrumentationTestInteraction>> entry : newInteractions.entrySet()) {
            ArrayList<InstrumentationTestInteraction> newInteractionsForView = entry.getValue();

            for (InstrumentationTestInteraction newInteraction : newInteractionsForView) {
                if (!containsInteraction(oldInteractions, newInteraction)) {
                    addInteractionToDictionary(appearingInteractions, entry.getKey(), newInteraction);
                }
            }
        }

        return appearingInteractions;
    }

    /**
     * Get Interactions from the top window of this summary that are also in the top window of an
     * old summary.
     *
     * @param oldSummary The old summary.
     * @return The Interactions that are also in the old summary.
     */
    public Map<String, ArrayList<InstrumentationTestInteraction>> getCommonInteractionsInTopWindow(EspressoScreenSummary oldSummary) {
        Map<String, ArrayList<InstrumentationTestInteraction>> commonInteractions = new HashMap<>();

        List<InstrumentationTestInteraction> oldInteractions =
                flatten(oldSummary.getInteractionsInTopWindow().values());
        Map<String, ArrayList<InstrumentationTestInteraction>> newInteractions = getInteractionsInTopWindow();

        for (Map.Entry<String, ArrayList<InstrumentationTestInteraction>> entry : newInteractions.entrySet()) {
            ArrayList<InstrumentationTestInteraction> newInteractionsForView = entry.getValue();

            for (InstrumentationTestInteraction newInteraction : newInteractionsForView) {
                if (containsInteraction(oldInteractions, newInteraction)) {
                    addInteractionToDictionary(commonInteractions, entry.getKey(), newInteraction);
                }
            }
        }

        return commonInteractions;
    }

    public Map<String, String> getUiAttributes(String viewUniqueId) {
        for (EspressoWindowSummary espressoWindow : espressoWindowSummaries) {
            Map<String, String> uiAttributes = espressoWindow.getUIAttributes(viewUniqueId);

            if (uiAttributes != null) {
                return uiAttributes;
            }
        }

        MATELog.log_warn("Unable to find UiAttributes for View with unique id: " + viewUniqueId);
        return new HashMap<>();
    }

    /**
     * Returns true if a interaction with the same code is present in a collection.
     *
     * @param listOfInteractions The collection of interactions.
     * @param interaction        The interaction to check.
     * @return True if the interaction is present in the collection.
     */
    private boolean containsInteraction(List<InstrumentationTestInteraction> listOfInteractions,
                                        InstrumentationTestInteraction interaction) {
        String interactionCode = interaction.getCode();

        for (InstrumentationTestInteraction interactionInList : listOfInteractions) {
            if (interactionInList.getCode().equals(interactionCode)) {
                return true;
            }
        }

        return false;
    }

    private void addInteractionToDictionary(Map<String, ArrayList<InstrumentationTestInteraction>> interactionsByView,
                                            String viewUniqueId,
                                            InstrumentationTestInteraction interaction) {
        if (!interactionsByView.containsKey(viewUniqueId)) {
            interactionsByView.put(viewUniqueId, new ArrayList<>());
        }

        interactionsByView.get(viewUniqueId).add(interaction);
    }

    private <T> List<T> flatten(Collection<ArrayList<T>> values) {
        List<T> result = new ArrayList<>();
        for (ArrayList<T> c : values) {
            result.addAll(c);
        }
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(this.espressoWindowSummaries);
    }

    protected EspressoScreenSummary(Parcel in) {
        this.espressoWindowSummaries = new ArrayList<>();
        in.readList(this.espressoWindowSummaries, EspressoWindowSummary.class.getClassLoader());
    }

    public static final Creator<EspressoScreenSummary> CREATOR = new Creator<EspressoScreenSummary>() {
        @Override
        public EspressoScreenSummary createFromParcel(Parcel source) {
            return new EspressoScreenSummary(source);
        }

        @Override
        public EspressoScreenSummary[] newArray(int size) {
            return new EspressoScreenSummary[size];
        }
    };

    /**
     * Returns the root matcher for the window of the given view
     *
     * @param viewUniqueId The view unique id
     * @return A root matcher or null.
     */
    public @Nullable
    EspressoRootMatcher getRootMatcher(String viewUniqueId) {
        EspressoRootMatcher rootMatcher = null;

        for (int i = 0, espressoWindowSummariesSize = espressoWindowSummaries.size(); i < espressoWindowSummariesSize; i++) {
            EspressoWindowSummary espressoWindow = espressoWindowSummaries.get(i);
            ArrayList<InstrumentationTestInteraction> interactions = espressoWindow.getInteractions(viewUniqueId);

            if (interactions != null) {
                rootMatcher = espressoWindow.getRootMatcher();

                if (i == 0 && rootMatcher != null) {
                    // The view was found in the top-most window.
                    // When this happens, we only need to use a root matcher if the window is a
                    // popup or system alert.
                    if (rootMatcher.getType() != EspressoRootMatcherType.IS_PLATFORM_POPUP &&
                            rootMatcher.getType() != EspressoRootMatcherType.IS_SYSTEM_ALERT_WINDOW) {
                        rootMatcher = null;
                    }

                    break;
                }

                break;
            }
        }

        return rootMatcher;
    }
}
