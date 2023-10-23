package org.mate.commons.interaction.action.espresso.interaction_builders;

import androidx.compose.ui.platform.AndroidComposeView;
import androidx.compose.ui.platform.ComposeView;
import androidx.compose.ui.semantics.Role;
import androidx.compose.ui.semantics.SemanticsNode;
import androidx.compose.ui.semantics.SemanticsProperties;
import androidx.compose.ui.semantics.SemanticsPropertyKey;
import androidx.compose.ui.text.AnnotatedString;

import org.mate.commons.interaction.action.espresso.EspressoView;
import org.mate.commons.interaction.action.espresso.interactions.InstrumentationTestInteraction;
import org.mate.commons.interaction.action.espresso.interactions.UiDeviceInteraction;
import org.mate.commons.interaction.action.espresso.view_matchers.EspressoViewMatcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class UiDeviceInteractionBuilder extends InteractionBuilder {
    @Override
    public ArrayList<InstrumentationTestInteraction> build(EspressoView espressoView, EspressoViewMatcher espressoViewMatcher) {
        return extractAvailableInteractionsFromComposeView((ComposeView) espressoView.getView(), espressoViewMatcher);
    }

    /**
     * ComposeViews require a different treatment (similar to RecyclerViews)
     * since the inner views are not visible to Espresso. Therefore we need to extract
     * information from it's inner nodes ([SemanticsNode]).
     *
     * @param composeView the view that holds the Composables (nodes)
     * @param viewMatcher viewMatcher used to match the [composeView]
     */
    private ArrayList<InstrumentationTestInteraction> extractAvailableInteractionsFromComposeView(ComposeView composeView, EspressoViewMatcher viewMatcher) {
        AndroidComposeView androidComposeView = (AndroidComposeView) composeView.getChildAt(0);

        List<SemanticsNode> nodes = androidComposeView.getRootForTest().getSemanticsOwner().getRootSemanticsNode().getChildren();
        HashMap<SemanticsPropertyKey<?>, ArrayList<SemanticsNode>> nodesWithInteractions = traverseComposeViewForInteractions(nodes);

        ArrayList<InstrumentationTestInteraction> espressoComposeInteractions = new ArrayList<>();

        for (Map.Entry<SemanticsPropertyKey<?>, ArrayList<SemanticsNode>> entry : nodesWithInteractions.entrySet()) {
            SemanticsPropertyKey<?> semanticsKey = entry.getKey();
            ArrayList<SemanticsNode> semanticsNodes = entry.getValue();
            UiDeviceInteraction espressoComposeInteraction = new UiDeviceInteraction(viewMatcher);
            for (SemanticsNode interactiveNode : semanticsNodes) {
                // SemanticNode annotated with Role tag represents a Button.
                if (semanticsKey.getName().equals("Role")) {
                    espressoComposeInteraction.setParametersWithNodeButton(interactiveNode);
                    espressoComposeInteractions.add(espressoComposeInteraction);
                } else if (semanticsKey.getName().equals("ContentDescription")) {
                    espressoComposeInteraction.setParameterWithContentDescriptionNode(interactiveNode);
                    espressoComposeInteractions.add(espressoComposeInteraction);
                } else {
                    // TODO: More semanticsKeys will handle this case.
                }
            }
        }
        return espressoComposeInteractions;
    }

    /**
     * Recursively traverses the root semantic node provided from ComposeView in order to find useful SemanticsNodes (such as Buttons, editTexts, and so on).
     * Then, its information is extracted to create the [EspressoComposeInteraction].
     *
     * @param semanticsNodeList a list of nodes (tree) to be traversed.
     * @return A BucketList which entries are SemanticsProperties and its values are Nodes that fulfill such property.
     */
    private HashMap<SemanticsPropertyKey<?>, ArrayList<SemanticsNode>> traverseComposeViewForInteractions(List<SemanticsNode> semanticsNodeList) {
        HashMap<SemanticsPropertyKey<?>, ArrayList<SemanticsNode>> result = new HashMap<>();

        SemanticsPropertyKey<Role> roleKey = SemanticsProperties.INSTANCE.getRole();
        SemanticsPropertyKey<AnnotatedString> editableKey = SemanticsProperties.INSTANCE.getEditableText();
        SemanticsPropertyKey<List<String>> contentDescription = SemanticsProperties.INSTANCE.getContentDescription();

        result.put(roleKey, new ArrayList<>());
        result.put(editableKey, new ArrayList<>());
        result.put(contentDescription, new ArrayList<>());
        for (SemanticsNode aSemanticsNode : semanticsNodeList) {
            Role role = aSemanticsNode.getConfig().getOrElseNullable(roleKey, () -> null);
            AnnotatedString editable = aSemanticsNode.getConfig().getOrElseNullable(editableKey, () -> null);
            List<String> contentDesc = aSemanticsNode.getConfig().getOrElseNullable(contentDescription, () -> null);

            if (role != null && role.toString().equals("Button")) {
                result.get(roleKey).add(aSemanticsNode);
            } else if (editable != null) {
                result.get(editableKey).add(aSemanticsNode);
            } else if (contentDesc != null && !contentDesc.isEmpty()) {
                result.get(contentDescription).add(aSemanticsNode);
            }
            if (!aSemanticsNode.getChildren().isEmpty()) {
                HashMap<SemanticsPropertyKey<?>, ArrayList<SemanticsNode>> nextResult = traverseComposeViewForInteractions(aSemanticsNode.getChildren());
                result.get(roleKey).addAll(nextResult.get(roleKey));
                result.get(editableKey).addAll(nextResult.get(editableKey));
                result.get(contentDescription).addAll(nextResult.get(contentDescription));
            }
        }
        return result;
    }
}
