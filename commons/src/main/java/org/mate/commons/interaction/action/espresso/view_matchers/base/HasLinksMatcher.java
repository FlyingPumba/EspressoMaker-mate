package org.mate.commons.interaction.action.espresso.view_matchers.base;

import static androidx.test.espresso.matcher.ViewMatchers.hasLinks;

import android.os.Parcel;
import android.view.View;

import org.hamcrest.Matcher;
import org.mate.commons.interaction.action.espresso.view_matchers.EspressoViewMatcher;
import org.mate.commons.interaction.action.espresso.view_matchers.EspressoViewMatcherType;

import java.util.HashSet;
import java.util.Set;

/**
 * Implements an Espresso Matcher for targeting the views that have links.
 */
public class HasLinksMatcher extends EspressoViewMatcher {

    public HasLinksMatcher() {
        super(EspressoViewMatcherType.HAS_LINKS);
    }

    @Override
    public String getCode() {
        return "hasLinks()";
    }

    @Override
    public Matcher<View> getViewMatcher() {
        return hasLinks();
    }

    @Override
    public Set<String> getNeededClassImports() {
        return new HashSet<>();
    }

    @Override
    public Set<String> getNeededStaticImports() {
        HashSet<String> imports = new HashSet<>();
        imports.add("androidx.test.espresso.matcher.ViewMatchers.hasLinks");
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

    public HasLinksMatcher(Parcel in) {
        this();
    }

    public static final Creator<HasLinksMatcher> CREATOR = new Creator<HasLinksMatcher>() {
        @Override
        public HasLinksMatcher createFromParcel(Parcel source) {
            // We need to use the EspressoViewMatcher.CREATOR here, because we want to make sure
            // to remove the EspressoViewMatcher's type integer from the beginning of Parcel and
            // call the appropriate constructor for this action.
            // Otherwise, the first integer will be read as data for an instance variable.
            return (HasLinksMatcher) EspressoViewMatcher.CREATOR.createFromParcel(source);
        }

        @Override
        public HasLinksMatcher[] newArray(int size) {
            return new HasLinksMatcher[size];
        }
    };
}
