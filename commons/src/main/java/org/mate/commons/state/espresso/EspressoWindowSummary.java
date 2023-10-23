package org.mate.commons.state.espresso;

import static org.mate.commons.interaction.action.espresso.root_matchers.EspressoRootMatcherType.WITH_WINDOW_TYPE;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;
import androidx.test.espresso.Root;

import org.mate.commons.interaction.action.espresso.EspressoView;
import org.mate.commons.interaction.action.espresso.interaction_builders.InteractionBuilder;
import org.mate.commons.interaction.action.espresso.interactions.InstrumentationTestInteraction;
import org.mate.commons.interaction.action.espresso.matchers_combination.RelativeMatcherCombination;
import org.mate.commons.interaction.action.espresso.root_matchers.EspressoRootMatcher;
import org.mate.commons.interaction.action.espresso.root_matchers.EspressoRootMatcherType;
import org.mate.commons.interaction.action.espresso.root_matchers.IsDialogMatcher;
import org.mate.commons.interaction.action.espresso.root_matchers.IsPlatformPopupMatcher;
import org.mate.commons.interaction.action.espresso.root_matchers.IsSystemAlertWindowMatcher;
import org.mate.commons.interaction.action.espresso.root_matchers.WithWindowTypeMatcher;
import org.mate.commons.interaction.action.espresso.view_matchers.EspressoViewMatcher;
import org.mate.commons.interaction.action.espresso.view_tree.EspressoViewTree;
import org.mate.commons.interaction.action.espresso.view_tree.EspressoViewTreeNode;
import org.mate.commons.utils.MATELog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EspressoWindowSummary implements Parcelable {

    /**
     * A list of discovered EspressoInteractions on this window.
     * The keys in the dictionary are the unique IDs of the views.
     */
    private Map<String, ArrayList<InstrumentationTestInteraction>> interactions = new HashMap<>();


    /**
     * A map of UI attributes for each view in this window.
     * The keys in the dictionary are the unique IDs of the views.
     */
    private Map<String, Map<String, String>> uiAttributes = new HashMap<>();

    /**
     * The type of window this is.
     */
    private final int windowType;

    /**
     * The root matcher type for this window.
     */
    private EspressoRootMatcherType rootMatcherType;

    public EspressoWindowSummary(EspressoViewTree viewTree) {
        buildInteractions(viewTree);
        parseUiAttributes(viewTree);
        parseRootMatcherType(viewTree);

        windowType = viewTree.getWindowRoot().getWindowLayoutParams().get().type;
    }

    public int getWindowType() {
        return this.windowType;
    }

    public @Nullable
    ArrayList<InstrumentationTestInteraction> getInteractions(String uniqueId) {
        return interactions.get(uniqueId);
    }

    public @Nullable
    Map<String, String> getUIAttributes(String viewUniqueId) {
        return uiAttributes.get(viewUniqueId);
    }

    public Map<String, ArrayList<InstrumentationTestInteraction>> getInteractions() {
        Map<String, ArrayList<InstrumentationTestInteraction>> result = new HashMap<>();

        for (Map.Entry<String, ArrayList<InstrumentationTestInteraction>> entry : interactions.entrySet()) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }

    private void buildInteractions(EspressoViewTree viewTree) {
        for (EspressoViewTreeNode node : viewTree.getAllNodes()) {
            EspressoView espressoView = node.getEspressoView();
            if (espressoView.shouldBeSkipped()) {
                continue;
            }
            String uniqueId = espressoView.getUniqueId();

            RelativeMatcherCombination matcherCombination = RelativeMatcherCombination.
                    buildUnequivocalCombination(node, viewTree);

            if (matcherCombination == null) {
                // we weren't able to generate a unequivocal matcher combination for this view, skip
                // it.
                MATELog.log_debug("Unable to generate unequivocal matcher combination for view.");
                continue;
            } else {
                MATELog.log_debug("Generated unequivocal matcher combination for view.");
            }

            EspressoViewMatcher viewMatcher = matcherCombination.getEspressoViewMatcher();
            MATELog.log_debug("Produced Espresso View matcher: " + viewMatcher.toString());

            // We generate the interactions for this view, using all available builders.
            List<InteractionBuilder> builderForEspressoView = InteractionBuilder.of(espressoView);
            ArrayList<InstrumentationTestInteraction> generatedInteractions = new ArrayList<>();

            for (InteractionBuilder builder : builderForEspressoView) {
                generatedInteractions.addAll(builder.build(espressoView, viewMatcher));
            }

            interactions.put(uniqueId, generatedInteractions);
        }
    }

    private void parseUiAttributes(EspressoViewTree viewTree) {
        for (EspressoViewTreeNode node : viewTree.getAllNodes()) {
            if (node.getEspressoView().shouldBeSkipped()) {
                continue;
            }

            String uniqueId = node.getEspressoView().getUniqueId();
            EspressoView espressoView = node.getEspressoView();

            Map<String, String> attributes = new HashMap<>(espressoView.getAllAttributes());

            uiAttributes.put(uniqueId, attributes);
        }
    }

    private void parseRootMatcherType(EspressoViewTree viewTree) {
        Root root = viewTree.getWindowRoot();

        IsDialogMatcher isDialogMatcher = new IsDialogMatcher();
        if (isDialogMatcher.getRootMatcher().matches(root)) {
            rootMatcherType = isDialogMatcher.getType();
            return;
        }

        IsPlatformPopupMatcher isPlatformPopupMatcher = new IsPlatformPopupMatcher();
        if (isPlatformPopupMatcher.getRootMatcher().matches(root)) {
            rootMatcherType = isPlatformPopupMatcher.getType();
            return;
        }

        IsSystemAlertWindowMatcher isSystemAlertWindowMatcher = new IsSystemAlertWindowMatcher();
        if (isSystemAlertWindowMatcher.getRootMatcher().matches(root)) {
            rootMatcherType = isSystemAlertWindowMatcher.getType();
            return;
        }

        rootMatcherType = WITH_WINDOW_TYPE;
    }

    public @Nullable
    EspressoRootMatcher getRootMatcher() {
        switch (rootMatcherType) {
            case IS_DIALOG:
                return new IsDialogMatcher();
            case IS_PLATFORM_POPUP:
                return new IsPlatformPopupMatcher();
            case IS_SYSTEM_ALERT_WINDOW:
                return new IsSystemAlertWindowMatcher();
            case WITH_WINDOW_TYPE:
                return new WithWindowTypeMatcher(windowType);
            default:
                throw new IllegalStateException("Invalid EspressoRootMatcher type found: " +
                        rootMatcherType);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.interactions.size());
        for (Map.Entry<String, ArrayList<InstrumentationTestInteraction>> entry : this.interactions.entrySet()) {
            dest.writeString(entry.getKey());
            int size = entry.getValue().size();
            dest.writeInt(size);
            for (int i = 0; i < size; i++) {
                dest.writeParcelable(entry.getValue().get(i), flags);
            }
        }

        dest.writeInt(this.uiAttributes.size());
        for (Map.Entry<String, Map<String, String>> entry : this.uiAttributes.entrySet()) {
            dest.writeString(entry.getKey());

            Map<String, String> attrs = entry.getValue();
            dest.writeInt(attrs.size());
            for (Map.Entry<String, String> auxEntry : attrs.entrySet()) {
                dest.writeString(auxEntry.getKey());
                dest.writeString(auxEntry.getValue());
            }
        }

        dest.writeInt(this.windowType);

        dest.writeInt(this.rootMatcherType == null ? -1 : this.rootMatcherType.ordinal());
    }

    protected EspressoWindowSummary(Parcel in) {
        int interactionsSize = in.readInt();
        this.interactions = new HashMap<>(interactionsSize);
        for (int i = 0; i < interactionsSize; i++) {
            String key = in.readString();
            int size = in.readInt();
            ArrayList<InstrumentationTestInteraction> interactions = new ArrayList<>();
            for (int j = 0; j < size; j++) {
                InstrumentationTestInteraction value = in.readParcelable(InstrumentationTestInteraction.class.getClassLoader());
                interactions.add(value);
            }
            this.interactions.put(key, interactions);
        }

        int uiAttributesSize = in.readInt();
        this.uiAttributes = new HashMap<>(uiAttributesSize);
        for (int i = 0; i < uiAttributesSize; i++) {
            String key = in.readString();

            int attrsSize = in.readInt();
            Map<String, String> attrs = new HashMap<>(attrsSize);
            for (int j = 0; j < attrsSize; j++) {
                String attrKey = in.readString();
                String attrValue = in.readString();
                attrs.put(attrKey, attrValue);
            }

            this.uiAttributes.put(key, attrs);
        }

        this.windowType = in.readInt();

        int tmpRootType = in.readInt();
        this.rootMatcherType = tmpRootType == -1 ? null :
                EspressoRootMatcherType.values()[tmpRootType];
    }

    public static final Creator<EspressoWindowSummary> CREATOR = new Creator<EspressoWindowSummary>() {
        @Override
        public EspressoWindowSummary createFromParcel(Parcel source) {
            return new EspressoWindowSummary(source);
        }

        @Override
        public EspressoWindowSummary[] newArray(int size) {
            return new EspressoWindowSummary[size];
        }
    };
}
