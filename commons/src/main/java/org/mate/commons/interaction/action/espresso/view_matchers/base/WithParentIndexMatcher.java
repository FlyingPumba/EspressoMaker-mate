package org.mate.commons.interaction.action.espresso.view_matchers.base;

import static androidx.test.espresso.matcher.ViewMatchers.withParentIndex;

import android.os.Parcel;
import android.view.View;

import org.hamcrest.Matcher;
import org.mate.commons.interaction.action.espresso.view_matchers.EspressoViewMatcher;
import org.mate.commons.interaction.action.espresso.view_matchers.EspressoViewMatcherType;

import java.util.HashSet;
import java.util.Set;

/**
 * Implements an Espresso Matcher for targeting the views that are in a certain index of the
 * parent's children.
 */
public class WithParentIndexMatcher extends EspressoViewMatcher {

    /**
     * The parent index to match against.
     */
    private final int parentIndex;

    public WithParentIndexMatcher(int parentIndex) {
        super(EspressoViewMatcherType.WITH_PARENT_INDEX);
        this.parentIndex = parentIndex;
    }

    public WithParentIndexMatcher(String newValue) {
        this(Integer.parseInt(newValue));
    }

    @Override
    public String getCode() {
        return String.format("withParentIndex(%d)", parentIndex);
    }

    @Override
    public Matcher<View> getViewMatcher() {
        return withParentIndex(parentIndex);
    }

    @Override
    public Set<String> getNeededClassImports() {
        return new HashSet<>();
    }

    @Override
    public Set<String> getNeededStaticImports() {
        HashSet<String> imports = new HashSet<>();
        imports.add("androidx.test.espresso.matcher.ViewMatchers.withParentIndex");
        return imports;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.parentIndex);
    }

    public WithParentIndexMatcher(Parcel source) {
        this(source.readInt());
    }

    public static final Creator<WithParentIndexMatcher> CREATOR = new Creator<WithParentIndexMatcher>() {
        @Override
        public WithParentIndexMatcher createFromParcel(Parcel source) {
            // We need to use the EspressoViewMatcher.CREATOR here, because we want to make sure
            // to remove the EspressoViewMatcher's type integer from the beginning of Parcel and
            // call the appropriate constructor for this action.
            // Otherwise, the first integer will be read as data for an instance variable.
            return (WithParentIndexMatcher) EspressoViewMatcher.CREATOR.createFromParcel(source);
        }

        @Override
        public WithParentIndexMatcher[] newArray(int size) {
            return new WithParentIndexMatcher[size];
        }
    };
}
