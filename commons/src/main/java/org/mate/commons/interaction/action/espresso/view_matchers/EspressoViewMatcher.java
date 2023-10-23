package org.mate.commons.interaction.action.espresso.view_matchers;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

import org.hamcrest.Matcher;
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
import org.mate.commons.interaction.action.espresso.view_matchers.base.WithIdMatcher;
import org.mate.commons.interaction.action.espresso.view_matchers.base.WithInputTypeMatcher;
import org.mate.commons.interaction.action.espresso.view_matchers.base.WithParentIndexMatcher;
import org.mate.commons.interaction.action.espresso.view_matchers.base.WithResourceNameMatcher;
import org.mate.commons.interaction.action.espresso.view_matchers.base.WithTextMatcher;
import org.mate.commons.interaction.action.espresso.view_matchers.recursive.AllOfMatcher;
import org.mate.commons.interaction.action.espresso.view_matchers.recursive.AnyOfMatcher;
import org.mate.commons.interaction.action.espresso.view_matchers.recursive.HasDescendantMatcher;
import org.mate.commons.interaction.action.espresso.view_matchers.recursive.IsDescendantOfAMatcher;
import org.mate.commons.interaction.action.espresso.view_matchers.recursive.WithChildMatcher;
import org.mate.commons.interaction.action.espresso.view_matchers.recursive.WithParentMatcher;
import org.mate.commons.utils.AbstractCodeProducer;

/**
 * Represents an actual Espresso's ViewMatcher.
 */
public abstract class EspressoViewMatcher extends AbstractCodeProducer implements Parcelable {

    /**
     * The type of Espresso ViewMatcher being represented by this instance.
     */
    private final EspressoViewMatcherType type;

    public EspressoViewMatcher(EspressoViewMatcherType type) {
        this.type = type;
    }

    /**
     * @return the type of Espresso ViewMatcher being represented by this instance.
     */
    public EspressoViewMatcherType getType() {
        return type;
    }

    /**
     * Get the actual Espresso's ViewMatcher instance represented by this EspressoViewMatcher.
     */
    public abstract Matcher<View> getViewMatcher();

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.type == null ? -1 : this.type.ordinal());
    }

    public static final Creator<EspressoViewMatcher> CREATOR = new Creator<EspressoViewMatcher>() {
        @Override
        public EspressoViewMatcher createFromParcel(Parcel source) {
            return EspressoViewMatcher.getConcreteClass(source);
        }

        @Override
        public EspressoViewMatcher[] newArray(int size) {
            return new EspressoViewMatcher[size];
        }
    };

    @Override
    public String toString() {
        return this.getCode();
    }

    /**
     * Auxiliary method to build an EspressoViewMatcher from a Parcel, using the correct subclass.
     * In order to do so, this method looks at the first integer in the Parcel.
     * Depending on the value, it will use the appropriate constructor from a subclass.
     * <p>
     * DO NOT use here the CREATOR classes inside each of the EspressoViewMatcher subclasses.
     * Doing so will cause an infinite recursion, since they call this method in turn indirectly.
     *
     * @param source
     * @return
     */
    private static EspressoViewMatcher getConcreteClass(Parcel source) {
        int tmpType = source.readInt();
        EspressoViewMatcherType type = tmpType == -1 ? null :
                EspressoViewMatcherType.values()[tmpType];

        if (type == null) {
            throw new IllegalStateException("Found null value for EspressoViewMatcher type.");
        }

        switch (type) {
            case IS_ROOT:
                return new IsRootViewMatcher(source);
            case WITH_CLASS_NAME:
                return new WithClassNameMatcher(source);
            case WITH_CONTENT_DESCRIPTION:
                return new WithContentDescriptionMatcher(source);
            case WITH_ID:
                return new WithIdMatcher(source);
            case WITH_RESOURCE_NAME:
                return new WithResourceNameMatcher(source);
            case WITH_TEXT:
                return new WithTextMatcher(source);
            case WITH_HINT:
                return new WithHintMatcher(source);
            case IS_DISPLAYED:
                return new IsDisplayedMatcher(source);
            case IS_FOCUSED:
                return new IsFocusedMatcher(source);
            case HAS_FOCUS:
                return new HasFocusMatcher(source);
            case IS_ENABLED:
                return new IsEnabledMatcher(source);
            case IS_SELECTED:
                return new IsSelectedMatcher(source);
            case IS_CHECKED:
                return new IsCheckedMatcher(source);
            case IS_CLICKABLE:
                return new IsClickableMatcher(source);
            case WITH_EFFECTIVE_VISIBILITY:
                return new WithEffectiveVisibilityMatcher(source);
            case WITH_ALPHA:
                return new WithAlphaMatcher(source);
            case HAS_CONTENT_DESCRIPTION:
                return new HasContentDescriptionMatcher(source);
            case IS_FOCUSABLE:
                return new IsFocusableMatcher(source);
            case HAS_ERROR_TEXT:
                return new HasErrorTextMatcher(source);
            case HAS_LINKS:
                return new HasLinksMatcher(source);
            case HAS_CHILD_COUNT:
                return new HasChildCountMatcher(source);
            case WITH_INPUT_TYPE:
                return new WithInputTypeMatcher(source);
            case WITH_PARENT_INDEX:
                return new WithParentIndexMatcher(source);
            case ALL_OF:
                return new AllOfMatcher(source);
            case ANY_OF:
                return new AnyOfMatcher(source);
            case HAS_DESCENDANT:
                return new HasDescendantMatcher(source);
            case IS_DESCENDANT_OF_A:
                return new IsDescendantOfAMatcher(source);
            case WITH_CHILD:
                return new WithChildMatcher(source);
            case WITH_PARENT:
                return new WithParentMatcher(source);
            default:
                throw new IllegalStateException("Invalid int for EspressoViewMatcher type found: " +
                        type);
        }
    }
}
