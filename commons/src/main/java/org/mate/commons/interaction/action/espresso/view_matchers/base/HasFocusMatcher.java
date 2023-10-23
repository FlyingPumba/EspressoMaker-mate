package org.mate.commons.interaction.action.espresso.view_matchers.base;

import static androidx.test.espresso.matcher.ViewMatchers.doesNotHaveFocus;
import static androidx.test.espresso.matcher.ViewMatchers.hasFocus;

import android.os.Parcel;
import android.view.View;

import org.hamcrest.Matcher;
import org.mate.commons.interaction.action.espresso.view_matchers.EspressoViewMatcher;
import org.mate.commons.interaction.action.espresso.view_matchers.EspressoViewMatcherType;

import java.util.HashSet;
import java.util.Set;

/**
 * Implements an Espresso Matcher for targeting the views that have focus or not.
 * Note, do not confuse this with the isFocused property.
 * https://stackoverflow.com/a/33022378/2271834
 */
public class HasFocusMatcher extends EspressoViewMatcher {

    /**
     * The hasFocus state to match against.
     */
    private final boolean hasFocus;

    public HasFocusMatcher(boolean hasFocus) {
        super(EspressoViewMatcherType.HAS_FOCUS);
        this.hasFocus = hasFocus;
    }

    public HasFocusMatcher(String newValue) {
        this("true".equals(newValue));
    }

    @Override
    public String getCode() {
        if (hasFocus) {
            return "hasFocus()";
        } else {
            return "doesNotHaveFocus()";
        }
    }

    @Override
    public Matcher<View> getViewMatcher() {
        if (hasFocus) {
            return hasFocus();
        } else {
            return doesNotHaveFocus();
        }
    }

    @Override
    public Set<String> getNeededClassImports() {
        return new HashSet<>();
    }

    @Override
    public Set<String> getNeededStaticImports() {
        HashSet<String> imports = new HashSet<>();

        if (hasFocus) {
            imports.add("androidx.test.espresso.matcher.ViewMatchers.hasFocus");
        } else {
            imports.add("androidx.test.espresso.matcher.ViewMatchers.doesNotHaveFocus");
        }

        return imports;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.hasFocus ? 1 : 0);
    }

    public HasFocusMatcher(Parcel source) {
        this(source.readInt() == 1);
    }

    public static final Creator<HasFocusMatcher> CREATOR = new Creator<HasFocusMatcher>() {
        @Override
        public HasFocusMatcher createFromParcel(Parcel source) {
            // We need to use the EspressoViewMatcher.CREATOR here, because we want to make sure
            // to remove the EspressoViewMatcher's type integer from the beginning of Parcel and
            // call the appropriate constructor for this action.
            // Otherwise, the first integer will be read as data for an instance variable.
            return (HasFocusMatcher) EspressoViewMatcher.CREATOR.createFromParcel(source);
        }

        @Override
        public HasFocusMatcher[] newArray(int size) {
            return new HasFocusMatcher[size];
        }
    };
}
