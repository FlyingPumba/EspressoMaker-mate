package org.mate.commons.interaction.action.espresso.view_matchers.base;

import static androidx.test.espresso.matcher.ViewMatchers.hasContentDescription;

import android.os.Parcel;
import android.view.View;

import org.hamcrest.Matcher;
import org.mate.commons.interaction.action.espresso.view_matchers.EspressoViewMatcher;
import org.mate.commons.interaction.action.espresso.view_matchers.EspressoViewMatcherType;

import java.util.HashSet;
import java.util.Set;

/**
 * Implements an Espresso Matcher for targeting the views that have content description.
 */
public class HasContentDescriptionMatcher extends EspressoViewMatcher {

    public HasContentDescriptionMatcher() {
        super(EspressoViewMatcherType.HAS_CONTENT_DESCRIPTION);
    }

    @Override
    public String getCode() {
        return "hasContentDescription()";
    }

    @Override
    public Matcher<View> getViewMatcher() {
        return hasContentDescription();
    }

    @Override
    public Set<String> getNeededClassImports() {
        return new HashSet<>();
    }

    @Override
    public Set<String> getNeededStaticImports() {
        HashSet<String> imports = new HashSet<>();
        imports.add("androidx.test.espresso.matcher.ViewMatchers.hasContentDescription");
        return imports;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    public HasContentDescriptionMatcher(Parcel in) {
        this();
    }

    public static final Creator<HasContentDescriptionMatcher> CREATOR = new Creator<HasContentDescriptionMatcher>() {
        @Override
        public HasContentDescriptionMatcher createFromParcel(Parcel source) {
            // We need to use the EspressoViewMatcher.CREATOR here, because we want to make sure
            // to remove the EspressoViewMatcher's type integer from the beginning of Parcel and
            // call the appropriate constructor for this action.
            // Otherwise, the first integer will be read as data for an instance variable.
            return (HasContentDescriptionMatcher) EspressoViewMatcher.CREATOR.createFromParcel(source);
        }

        @Override
        public HasContentDescriptionMatcher[] newArray(int size) {
            return new HasContentDescriptionMatcher[size];
        }
    };
}
