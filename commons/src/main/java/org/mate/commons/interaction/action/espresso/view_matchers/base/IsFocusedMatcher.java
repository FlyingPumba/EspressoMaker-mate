package org.mate.commons.interaction.action.espresso.view_matchers.base;

import static androidx.test.espresso.matcher.ViewMatchers.isFocused;
import static androidx.test.espresso.matcher.ViewMatchers.isNotFocused;

import android.os.Parcel;
import android.view.View;

import org.hamcrest.Matcher;
import org.mate.commons.interaction.action.espresso.view_matchers.EspressoViewMatcher;
import org.mate.commons.interaction.action.espresso.view_matchers.EspressoViewMatcherType;

import java.util.HashSet;
import java.util.Set;

/**
 * Implements an Espresso Matcher for targeting the views that are focused or not.
 * Note, do not confuse this with the hasFocus property.
 * https://stackoverflow.com/a/33022378/2271834
 */
public class IsFocusedMatcher extends EspressoViewMatcher {

    /**
     * The focus state to match against.
     */
    private final boolean focused;

    public IsFocusedMatcher(boolean focused) {
        super(EspressoViewMatcherType.IS_FOCUSED);
        this.focused = focused;
    }

    public IsFocusedMatcher(String newValue) {
        this("true".equals(newValue));
    }

    @Override
    public String getCode() {
        if (focused) {
            return "isFocused()";
        } else {
            return "isNotFocused()";
        }
    }

    @Override
    public Matcher<View> getViewMatcher() {
        if (focused) {
            return isFocused();
        } else {
            return isNotFocused();
        }
    }

    @Override
    public Set<String> getNeededClassImports() {
        return new HashSet<>();
    }

    @Override
    public Set<String> getNeededStaticImports() {
        HashSet<String> imports = new HashSet<>();

        if (focused) {
            imports.add("androidx.test.espresso.matcher.ViewMatchers.isFocused");
        } else {
            imports.add("androidx.test.espresso.matcher.ViewMatchers.isNotFocused");
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
        dest.writeInt(this.focused ? 1 : 0);
    }

    public IsFocusedMatcher(Parcel source) {
        this(source.readInt() == 1);
    }

    public static final Creator<IsFocusedMatcher> CREATOR = new Creator<IsFocusedMatcher>() {
        @Override
        public IsFocusedMatcher createFromParcel(Parcel source) {
            // We need to use the EspressoViewMatcher.CREATOR here, because we want to make sure
            // to remove the EspressoViewMatcher's type integer from the beginning of Parcel and
            // call the appropriate constructor for this action.
            // Otherwise, the first integer will be read as data for an instance variable.
            return (IsFocusedMatcher) EspressoViewMatcher.CREATOR.createFromParcel(source);
        }

        @Override
        public IsFocusedMatcher[] newArray(int size) {
            return new IsFocusedMatcher[size];
        }
    };
}
