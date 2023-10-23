package org.mate.commons.interaction.action.espresso.view_matchers.base;

import static androidx.test.espresso.matcher.ViewMatchers.hasChildCount;

import android.os.Parcel;
import android.view.View;

import org.hamcrest.Matcher;
import org.mate.commons.interaction.action.espresso.view_matchers.EspressoViewMatcher;
import org.mate.commons.interaction.action.espresso.view_matchers.EspressoViewMatcherType;

import java.util.HashSet;
import java.util.Set;

/**
 * Implements an Espresso Matcher for targeting the views that have a certain number of children.
 */
public class HasChildCountMatcher extends EspressoViewMatcher {

    /**
     * The number of children to match against.
     */
    private final int childCount;

    public HasChildCountMatcher(int childCount) {
        super(EspressoViewMatcherType.HAS_CHILD_COUNT);
        this.childCount = childCount;
    }

    public HasChildCountMatcher(String newValue) {
        this(Integer.parseInt(newValue));
    }

    @Override
    public String getCode() {
        return String.format("hasChildCount(%d)", childCount);
    }

    @Override
    public Matcher<View> getViewMatcher() {
        return hasChildCount(childCount);
    }

    @Override
    public Set<String> getNeededClassImports() {
        return new HashSet<>();
    }

    @Override
    public Set<String> getNeededStaticImports() {
        HashSet<String> imports = new HashSet<>();
        imports.add("androidx.test.espresso.matcher.ViewMatchers.hasChildCount");
        return imports;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.childCount);
    }

    public HasChildCountMatcher(Parcel source) {
        this(source.readInt());
    }

    public static final Creator<HasChildCountMatcher> CREATOR = new Creator<HasChildCountMatcher>() {
        @Override
        public HasChildCountMatcher createFromParcel(Parcel source) {
            // We need to use the EspressoViewMatcher.CREATOR here, because we want to make sure
            // to remove the EspressoViewMatcher's type integer from the beginning of Parcel and
            // call the appropriate constructor for this action.
            // Otherwise, the first integer will be read as data for an instance variable.
            return (HasChildCountMatcher) EspressoViewMatcher.CREATOR.createFromParcel(source);
        }

        @Override
        public HasChildCountMatcher[] newArray(int size) {
            return new HasChildCountMatcher[size];
        }
    };
}
