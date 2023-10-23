package org.mate.commons.interaction.action.espresso.assertions;

import static android.view.View.VISIBLE;

import androidx.annotation.Nullable;

import org.mate.commons.interaction.action.espresso.EspressoAssertion;
import org.mate.commons.interaction.action.espresso.interactions.InstrumentationTestInteraction;
import org.mate.commons.interaction.action.espresso.root_matchers.EspressoRootMatcher;
import org.mate.commons.interaction.action.espresso.view_matchers.base.HasChildCountMatcher;
import org.mate.commons.interaction.action.espresso.view_matchers.base.HasContentDescriptionMatcher;
import org.mate.commons.interaction.action.espresso.view_matchers.base.HasErrorTextMatcher;
import org.mate.commons.interaction.action.espresso.view_matchers.base.HasFocusMatcher;
import org.mate.commons.interaction.action.espresso.view_matchers.base.HasLinksMatcher;
import org.mate.commons.interaction.action.espresso.view_matchers.base.IsCheckedMatcher;
import org.mate.commons.interaction.action.espresso.view_matchers.base.IsClickableMatcher;
import org.mate.commons.interaction.action.espresso.view_matchers.base.IsDisplayedMatcher;
import org.mate.commons.interaction.action.espresso.view_matchers.base.IsEnabledMatcher;
import org.mate.commons.interaction.action.espresso.view_matchers.base.IsFocusableMatcher;
import org.mate.commons.interaction.action.espresso.view_matchers.base.IsFocusedMatcher;
import org.mate.commons.interaction.action.espresso.view_matchers.base.IsRootViewMatcher;
import org.mate.commons.interaction.action.espresso.view_matchers.base.IsSelectedMatcher;
import org.mate.commons.interaction.action.espresso.view_matchers.base.WithAlphaMatcher;
import org.mate.commons.interaction.action.espresso.view_matchers.base.WithClassNameMatcher;
import org.mate.commons.interaction.action.espresso.view_matchers.base.WithContentDescriptionMatcher;
import org.mate.commons.interaction.action.espresso.view_matchers.base.WithEffectiveVisibilityMatcher;
import org.mate.commons.interaction.action.espresso.view_matchers.base.WithHintMatcher;
import org.mate.commons.interaction.action.espresso.view_matchers.base.WithInputTypeMatcher;
import org.mate.commons.interaction.action.espresso.view_matchers.base.WithParentIndexMatcher;
import org.mate.commons.interaction.action.espresso.view_matchers.base.WithTextMatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EspressoAssertionsFactory {

    private static final IsRootViewMatcher IS_ROOT_VIEW_MATCHER = new IsRootViewMatcher();

    /**
     * Asserts that a view has disappeared from the screen.
     *
     * @param interaction the interaction of the view that has disappeared.
     * @return the assertion.
     */
    public static List<EspressoAssertion> viewIsGone(InstrumentationTestInteraction interaction,
                                                     @Nullable EspressoRootMatcher rootMatcher) {
        List<EspressoAssertion> assertions = new ArrayList<>();

        if (IS_ROOT_VIEW_MATCHER.getCode().equals(interaction.getCode())) {
            // We don't want to assert that the root view is gone
            return null;
        }

        // Never use a root matcher for the DoesNotExist assertion, since the window for that root
        // may be gone, and the assertion will fail when it tries to find the root.
        assertions.add(new EspressoAssertion(interaction, new DoesNotExistAssertion(), null));

        return assertions;
    }

    /**
     * Asserts that a view has appeared on the screen.
     *
     * @param attributes  the UI attributes of the view.
     * @param interaction the interaction of the view that has appeared.
     * @return the assertion.
     */
    public static List<EspressoAssertion> viewHasAppeared(
            InstrumentationTestInteraction interaction,
            @Nullable EspressoRootMatcher rootMatcher,
            Map<String, String> attributes) {
        List<EspressoAssertion> assertions = new ArrayList<>();

        if (IS_ROOT_VIEW_MATCHER.getCode().equals(interaction.getCode())) {
            // We don't want to assert that the root view has appeared
            return assertions;
        }

        if (!attributes.containsKey("width") || !attributes.containsKey("height") ||
                "0".equals(attributes.get("width")) || "0".equals(attributes.get("height"))) {
            // We don't want to assert that the view has appeared if it has no size
            return assertions;
        }

        if (!attributes.containsKey("visibility") ||
                VISIBLE != Integer.parseInt(attributes.get("visibility"))) {
            // We don't want to assert that the view has appeared if it is not actually visible
            return assertions;
        }

        if (!attributes.containsKey("is_displayed") ||
                "false".equals(attributes.get("is_displayed"))) {
            // We don't want to assert that the view has appeared if it is not actually displayed
            return assertions;
        }

        assertions.add(new EspressoAssertion(interaction,
                new MatchesAssertion(new IsDisplayedMatcher()),
                rootMatcher));

        return assertions;
    }

    /**
     * Asserts that a view has changed an attribute's value.
     *
     * @param attrKey     the attribute's key.
     * @param oldValue    the old value of the attribute.
     * @param newValue    the new value of the attribute.
     * @param interaction the interaction of the view that has changed the attribute.
     * @return the assertion.
     */
    public static List<EspressoAssertion> viewHasChanged(InstrumentationTestInteraction interaction,
                                                         @Nullable EspressoRootMatcher rootMatcher,
                                                         String attrKey,
                                                         @Nullable String oldValue,
                                                         @Nullable String newValue) {
        List<EspressoAssertion> assertions = new ArrayList<>();

        switch (attrKey) {
            case "class":
                if (newValue != null) {
                    assertions.add(new EspressoAssertion(interaction,
                            new MatchesAssertion(new WithClassNameMatcher(newValue)), rootMatcher));
                }
                break;
            case "enabled":
                if ("true".equals(newValue)) {
                    assertions.add(new EspressoAssertion(interaction,
                            new MatchesAssertion(new IsEnabledMatcher(newValue)), rootMatcher));
                }
                break;
            case "selected":
                if ("true".equals(newValue)) {
                    assertions.add(new EspressoAssertion(interaction,
                            new MatchesAssertion(new IsSelectedMatcher(newValue)), rootMatcher));
                }
                break;
            case "focused":
                if ("true".equals(newValue)) {
                    assertions.add(new EspressoAssertion(interaction,
                            new MatchesAssertion(new IsFocusedMatcher(newValue)), rootMatcher));
                }
                break;
            case "focusable":
                if ("true".equals(newValue)) {
                    assertions.add(new EspressoAssertion(interaction,
                            new MatchesAssertion(new IsFocusableMatcher(newValue)), rootMatcher));
                }
                break;
            case "checked":
                if ("true".equals(newValue)) {
                    assertions.add(new EspressoAssertion(interaction,
                            new MatchesAssertion(new IsCheckedMatcher(newValue)), rootMatcher));
                }
                break;
            case "hasFocus":
                if ("true".equals(newValue)) {
                    assertions.add(new EspressoAssertion(interaction,
                            new MatchesAssertion(new HasFocusMatcher(newValue)), rootMatcher));
                }
                break;
            case "clickable":
                if ("true".equals(newValue)) {
                    assertions.add(new EspressoAssertion(interaction,
                            new MatchesAssertion(new IsClickableMatcher(newValue)), rootMatcher));
                }
                break;
            case "text":
                if (newValue != null) {
                    assertions.add(new EspressoAssertion(interaction,
                            new MatchesAssertion(new WithTextMatcher(newValue)), rootMatcher));
                }
                break;
            case "errorText":
                if (newValue != null) {
                    assertions.add(new EspressoAssertion(interaction,
                            new MatchesAssertion(new HasErrorTextMatcher(newValue)), rootMatcher));
                }
                break;
            case "hasLinks":
                if ("true".equals(newValue)) {
                    assertions.add(new EspressoAssertion(interaction,
                            new MatchesAssertion(new HasLinksMatcher()), rootMatcher));
                }
                break;
            case "contentDescription":
                if (newValue != null) {
                    assertions.add(new EspressoAssertion(interaction,
                            new MatchesAssertion(new WithContentDescriptionMatcher(newValue)),
                            rootMatcher));

                    if (oldValue == null) {
                        assertions.add(new EspressoAssertion(interaction,
                                new MatchesAssertion(new HasContentDescriptionMatcher()),
                                rootMatcher));
                    }
                }
                break;
            case "hint":
                if (newValue != null) {
                    assertions.add(new EspressoAssertion(interaction,
                            new MatchesAssertion(new WithHintMatcher(newValue)), rootMatcher));
                }
                break;
            case "visibility":
                if (newValue != null) {
                    assertions.add(new EspressoAssertion(interaction,
                            new MatchesAssertion(new WithEffectiveVisibilityMatcher(newValue)),
                            rootMatcher));
                }
                break;
            case "alpha":
                if (newValue != null) {
                    assertions.add(new EspressoAssertion(interaction,
                            new MatchesAssertion(new WithAlphaMatcher(newValue)), rootMatcher));
                }
                break;
            case "childCount":
                if (newValue != null) {
                    assertions.add(new EspressoAssertion(interaction,
                            new MatchesAssertion(new HasChildCountMatcher(newValue)), rootMatcher));
                }
                break;
            case "inputType":
                if (newValue != null) {
                    assertions.add(new EspressoAssertion(interaction,
                            new MatchesAssertion(new WithInputTypeMatcher(newValue)), rootMatcher));
                }
                break;
            case "parentIndex":
                if (newValue != null) {
                    assertions.add(new EspressoAssertion(interaction,
                            new MatchesAssertion(new WithParentIndexMatcher(newValue)), rootMatcher));
                }
                break;
            case "is_displayed":
                boolean viewWasNotDisplayedBefore = oldValue == null || "false".equals(oldValue);
                boolean viewIsDisplayedNow = "true".equals(newValue);
                if (viewWasNotDisplayedBefore && viewIsDisplayedNow) {
                    // view has appeared into screen
                    assertions.add(new EspressoAssertion(interaction,
                            new MatchesAssertion(new IsDisplayedMatcher()), rootMatcher));
                }
                break;
        }

        return assertions;
    }
}
