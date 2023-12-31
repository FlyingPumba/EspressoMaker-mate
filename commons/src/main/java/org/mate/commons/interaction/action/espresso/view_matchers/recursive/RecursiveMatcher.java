package org.mate.commons.interaction.action.espresso.view_matchers.recursive;

import org.mate.commons.interaction.action.espresso.matchers_combination.RelativeMatcher;
import org.mate.commons.interaction.action.espresso.view_matchers.EspressoViewMatcher;
import org.mate.commons.interaction.action.espresso.view_matchers.EspressoViewMatcherType;
import org.mate.commons.interaction.action.espresso.view_matchers.base.IsRootViewMatcher;
import org.mate.commons.interaction.action.espresso.view_matchers.base.WithClassNameMatcher;
import org.mate.commons.interaction.action.espresso.view_matchers.base.WithContentDescriptionMatcher;
import org.mate.commons.interaction.action.espresso.view_matchers.base.WithIdMatcher;
import org.mate.commons.interaction.action.espresso.view_matchers.base.WithResourceNameMatcher;
import org.mate.commons.interaction.action.espresso.view_matchers.base.WithTextMatcher;
import org.mate.commons.interaction.action.espresso.view_tree.EspressoViewTreeNode;
import org.mate.commons.interaction.action.espresso.view_tree.PathStep;
import org.mate.commons.interaction.action.espresso.view_tree.PathStepType;

import java.util.Collections;
import java.util.List;

/**
 * This class represents the Espresso ViewMatchers that are recursive, i.e., that use another
 * matchers inside.
 * They can NOT be used by themselves. They impose more than one constraint or they impose
 * constraints on other parts of the UI hierarchy (e.g., on the children of a View).
 */
public abstract class RecursiveMatcher extends EspressoViewMatcher {

    /**
     * The recursive matchers.
     */
    protected List<EspressoViewMatcher> matchers;

    public RecursiveMatcher(EspressoViewMatcherType type) {
        super(type);
    }

    /**
     * @return the recursive matchers.
     */
    public List<EspressoViewMatcher> getMatchers() {
        return Collections.unmodifiableList(matchers);
    }

    /**
     * Add a matcher to this recursive matcher.
     *
     * @param matcher the matcher to add
     */
    public void addMatcher(EspressoViewMatcher matcher) {
        matchers.add(matcher);
    }

    public void addMatcherForPathInTree(RelativeMatcher relativeMatcher,
                                        EspressoViewTreeNode node) {
        if (relativeMatcher.getPath().isEmpty()) {
            EspressoViewMatcher newMatcher = null;

            switch (relativeMatcher.getType()) {
                case IS_ROOT:
                    newMatcher = new IsRootViewMatcher();
                    break;
                case WITH_RESOURCE_NAME:
                    newMatcher = new WithResourceNameMatcher(node.getEspressoView().getResourceEntryName());
                    break;
                case WITH_ID:
                    newMatcher = new WithIdMatcher(node.getEspressoView().getId());
                    break;
                case WITH_TEXT:
                    newMatcher = new WithTextMatcher(node.getEspressoView().getText());
                    break;
                case WITH_CONTENT_DESCRIPTION:
                    newMatcher =
                            new WithContentDescriptionMatcher(node.getEspressoView().getContentDescription());
                    break;
                case WITH_CLASS_NAME:
                    newMatcher =
                            new WithClassNameMatcher(node.getEspressoView().getClassName());
                    break;
                default:
                    throw new IllegalStateException(String.format("Adding matcher in tree not " +
                            "implemented for matcher type: %s", relativeMatcher.getType()));
            }

            addMatcher(newMatcher);

            return;
        }

        // If the path is not empty, it means that we need to keep traversing the nested
        // Matchers, while at the same time traversing the view tree.

        PathStep nextStepInPath = relativeMatcher.getPath().getHead();
        EspressoViewTreeNode nodeAfterStep = nextStepInPath.moveFromNodeUsingDirectionIndexes(node);

        if (nextStepInPath.getType() == PathStepType.MOVE_TO_PARENT) {
            // Going "up" one level in the hierarchy tree
            // We need to use a WithParent matcher, but first we need to check that there isn't
            // one already
            WithParentMatcher withParentMatcher = null;
            for (EspressoViewMatcher matcher : matchers) {
                if (matcher instanceof WithParentMatcher) {
                    withParentMatcher = (WithParentMatcher) matcher;
                }
            }

            if (withParentMatcher == null) {
                // No parent matcher found. Create one new and add it to the existing matchers.
                withParentMatcher = new WithParentMatcher();
                matchers.add(withParentMatcher);
            }

            withParentMatcher.addMatcherForPathInTree(new RelativeMatcher(
                    relativeMatcher.getPath().getTail(),
                    relativeMatcher.getType()), nodeAfterStep);
        } else {
            // Going "down" one level in the hierarchy tree
            // We need to use a WithChild matcher, but first we need to check that there isn't
            // one already for the same child index.
            int directionIndex = nextStepInPath.getDirectionIndex();
            WithChildMatcher withChildMatcher = null;
            for (EspressoViewMatcher matcher : matchers) {
                if (matcher instanceof WithChildMatcher &&
                        ((WithChildMatcher) matcher).getChildIndex() == directionIndex) {
                    withChildMatcher = (WithChildMatcher) matcher;
                }
            }

            if (withChildMatcher == null) {
                // No child matcher found for the same child index. Create one new and add it to
                // the existing matchers.
                withChildMatcher = new WithChildMatcher(directionIndex);
                matchers.add(withChildMatcher);
            }

            withChildMatcher.addMatcherForPathInTree(new RelativeMatcher(
                    relativeMatcher.getPath().getTail(),
                    relativeMatcher.getType()), nodeAfterStep);
        }

    }
}
