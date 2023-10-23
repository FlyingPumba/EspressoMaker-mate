package org.mate.commons.interaction.action.espresso.view_matchers.base;

import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;

import android.os.Parcel;
import android.view.View;

import org.hamcrest.Matcher;
import org.mate.commons.interaction.action.espresso.view_matchers.EspressoViewMatcher;
import org.mate.commons.interaction.action.espresso.view_matchers.EspressoViewMatcherType;

import java.util.HashSet;
import java.util.Set;

/**
 * Implements an Espresso Matcher for targeting the views that have a certain Error text.
 */
public class HasErrorTextMatcher extends EspressoViewMatcher {

    /**
     * The errorText to match against.
     */
    private final String errorText;

    public HasErrorTextMatcher(String errorText) {
        super(EspressoViewMatcherType.HAS_ERROR_TEXT);
        this.errorText = errorText;
    }

    @Override
    public String getCode() {
        return String.format("hasErrorText(%s)", boxString(errorText));
    }

    @Override
    public Matcher<View> getViewMatcher() {
        return hasErrorText(errorText);
    }

    @Override
    public Set<String> getNeededClassImports() {
        return new HashSet<>();
    }

    @Override
    public Set<String> getNeededStaticImports() {
        HashSet<String> imports = new HashSet<>();
        imports.add("androidx.test.espresso.matcher.ViewMatchers.hasErrorText");
        return imports;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.errorText);
    }

    public HasErrorTextMatcher(Parcel in) {
        this(in.readString());
    }

    public static final Creator<HasErrorTextMatcher> CREATOR = new Creator<HasErrorTextMatcher>() {
        @Override
        public HasErrorTextMatcher createFromParcel(Parcel source) {
            // We need to use the EspressoViewMatcher.CREATOR here, because we want to make sure
            // to remove the EspressoViewMatcher's type integer from the beginning of Parcel and
            // call the appropriate constructor for this action.
            // Otherwise, the first integer will be read as data for an instance variable.
            return (HasErrorTextMatcher) EspressoViewMatcher.CREATOR.createFromParcel(source);
        }

        @Override
        public HasErrorTextMatcher[] newArray(int size) {
            return new HasErrorTextMatcher[size];
        }
    };
}
