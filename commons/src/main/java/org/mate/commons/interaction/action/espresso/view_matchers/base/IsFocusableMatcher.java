package org.mate.commons.interaction.action.espresso.view_matchers.base;

import static androidx.test.espresso.matcher.ViewMatchers.isFocusable;
import static androidx.test.espresso.matcher.ViewMatchers.isNotFocusable;

import android.os.Parcel;
import android.view.View;

import org.hamcrest.Matcher;
import org.mate.commons.interaction.action.espresso.view_matchers.EspressoViewMatcher;
import org.mate.commons.interaction.action.espresso.view_matchers.EspressoViewMatcherType;

import java.util.HashSet;
import java.util.Set;

/**
 * Implements an Espresso Matcher for targeting the views that are focusable or not.
 */
public class IsFocusableMatcher extends EspressoViewMatcher {

    /**
     * The focusable state to match against.
     */
    private final boolean focusable;

    public IsFocusableMatcher(boolean focusable) {
        super(EspressoViewMatcherType.IS_FOCUSABLE);
        this.focusable = focusable;
    }

    public IsFocusableMatcher(String newValue) {
        this("true".equals(newValue));
    }

    @Override
    public String getCode() {
        if (focusable) {
            return "isFocusable()";
        } else {
            return "isNotFocusable()";
        }
    }

    @Override
    public Matcher<View> getViewMatcher() {
        if (focusable) {
            return isFocusable();
        } else {
            return isNotFocusable();
        }
    }

    @Override
    public Set<String> getNeededClassImports() {
        return new HashSet<>();
    }

    @Override
    public Set<String> getNeededStaticImports() {
        HashSet<String> imports = new HashSet<>();

        if (focusable) {
            imports.add("androidx.test.espresso.matcher.ViewMatchers.isFocusable");
        } else {
            imports.add("androidx.test.espresso.matcher.ViewMatchers.isNotFocusable");
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
        dest.writeInt(this.focusable ? 1 : 0);
    }

    public IsFocusableMatcher(Parcel source) {
        this(source.readInt() == 1);
    }

    public static final Creator<IsFocusableMatcher> CREATOR = new Creator<IsFocusableMatcher>() {
        @Override
        public IsFocusableMatcher createFromParcel(Parcel source) {
            // We need to use the EspressoViewMatcher.CREATOR here, because we want to make sure
            // to remove the EspressoViewMatcher's type integer from the beginning of Parcel and
            // call the appropriate constructor for this action.
            // Otherwise, the first integer will be read as data for an instance variable.
            return (IsFocusableMatcher) EspressoViewMatcher.CREATOR.createFromParcel(source);
        }

        @Override
        public IsFocusableMatcher[] newArray(int size) {
            return new IsFocusableMatcher[size];
        }
    };
}
