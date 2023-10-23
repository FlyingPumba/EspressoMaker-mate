package org.mate.commons.interaction.action.espresso.view_matchers.base;

import static androidx.test.espresso.matcher.ViewMatchers.isNotSelected;
import static androidx.test.espresso.matcher.ViewMatchers.isSelected;

import android.os.Parcel;
import android.view.View;

import org.hamcrest.Matcher;
import org.mate.commons.interaction.action.espresso.view_matchers.EspressoViewMatcher;
import org.mate.commons.interaction.action.espresso.view_matchers.EspressoViewMatcherType;

import java.util.HashSet;
import java.util.Set;

/**
 * Implements an Espresso Matcher for targeting the views that are selected or not.
 */
public class IsSelectedMatcher extends EspressoViewMatcher {

    /**
     * The focus state to match against.
     */
    private final boolean selected;

    public IsSelectedMatcher(boolean selected) {
        super(EspressoViewMatcherType.IS_SELECTED);
        this.selected = selected;
    }

    public IsSelectedMatcher(String newValue) {
        this("true".equals(newValue));
    }

    @Override
    public String getCode() {
        if (selected) {
            return "isSelected()";
        } else {
            return "isNotSelected()";
        }
    }

    @Override
    public Matcher<View> getViewMatcher() {
        if (selected) {
            return isSelected();
        } else {
            return isNotSelected();
        }
    }

    @Override
    public Set<String> getNeededClassImports() {
        return new HashSet<>();
    }

    @Override
    public Set<String> getNeededStaticImports() {
        HashSet<String> imports = new HashSet<>();

        if (selected) {
            imports.add("androidx.test.espresso.matcher.ViewMatchers.isSelected");
        } else {
            imports.add("androidx.test.espresso.matcher.ViewMatchers.isNotSelected");
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
        dest.writeInt(this.selected ? 1 : 0);
    }

    public IsSelectedMatcher(Parcel source) {
        this(source.readInt() == 1);
    }

    public static final Creator<IsSelectedMatcher> CREATOR = new Creator<IsSelectedMatcher>() {
        @Override
        public IsSelectedMatcher createFromParcel(Parcel source) {
            // We need to use the EspressoViewMatcher.CREATOR here, because we want to make sure
            // to remove the EspressoViewMatcher's type integer from the beginning of Parcel and
            // call the appropriate constructor for this action.
            // Otherwise, the first integer will be read as data for an instance variable.
            return (IsSelectedMatcher) EspressoViewMatcher.CREATOR.createFromParcel(source);
        }

        @Override
        public IsSelectedMatcher[] newArray(int size) {
            return new IsSelectedMatcher[size];
        }
    };
}
