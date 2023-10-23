package org.mate.commons.interaction.action.espresso.view_matchers.recursive;

import static androidx.test.espresso.matcher.ViewMatchers.withChild;

import android.os.Parcel;
import android.view.View;

import org.hamcrest.Matcher;
import org.mate.commons.interaction.action.espresso.view_matchers.EspressoViewMatcher;
import org.mate.commons.interaction.action.espresso.view_matchers.EspressoViewMatcherType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Implements an Espresso Matcher for targeting the views that have a direct descendant (children)
 * view matching ALL matchers in a list.
 */
public class WithChildMatcher extends RecursiveMatcher {

    /**
     * The index of the child to match.
     * This is only used for comparing WithChildMatchers, not for generating Espresso code.
     */
    private final int childIndex;

    /**
     * Auxiliary matcher to group all conditions.
     */
    private AllOfMatcher allOfMatcher;

    public WithChildMatcher(int childIndex) {
        this(childIndex, new ArrayList<>());
    }

    public WithChildMatcher(int childIndex, List<EspressoViewMatcher> matchers) {
        super(EspressoViewMatcherType.WITH_CHILD);
        this.childIndex = childIndex;
        this.matchers = matchers;
        allOfMatcher = new AllOfMatcher(matchers);
    }

    /**
     * Returns the index of the child to match.
     * @return an integer representing the index of the child to match.
     */
    public int getChildIndex() {
        return childIndex;
    }

    @Override
    public void addMatcher(EspressoViewMatcher matcher) {
        super.addMatcher(matcher);
        this.allOfMatcher = new AllOfMatcher(matchers);
    }

    @Override
    public String getCode() {
        return String.format("withChild(%s)", allOfMatcher.getCode());
    }

    @Override
    public Matcher<View> getViewMatcher() {
        return withChild(allOfMatcher.getViewMatcher());
    }

    @Override
    public Set<String> getNeededClassImports() {
        return allOfMatcher.getNeededClassImports();
    }

    @Override
    public Set<String> getNeededStaticImports() {
        HashSet<String> imports = new HashSet<>();
        imports.add("androidx.test.espresso.matcher.ViewMatchers.withChild");
        imports.addAll(allOfMatcher.getNeededStaticImports());
        return imports;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.childIndex);
        dest.writeTypedList(this.matchers);
    }

    public WithChildMatcher(Parcel in) {
        this(in.readInt(), in.createTypedArrayList(EspressoViewMatcher.CREATOR));
    }

    public static final Creator<WithChildMatcher> CREATOR = new Creator<WithChildMatcher>() {
        @Override
        public WithChildMatcher createFromParcel(Parcel source) {
            // We need to use the EspressoViewMatcher.CREATOR here, because we want to make sure
            // to remove the EspressoViewMatcher's type integer from the beginning of Parcel and
            // call the appropriate constructor for this action.
            // Otherwise, the first integer will be read as data for an instance variable.
            return (WithChildMatcher) EspressoViewMatcher.CREATOR.createFromParcel(source);
        }

        @Override
        public WithChildMatcher[] newArray(int size) {
            return new WithChildMatcher[size];
        }
    };
}
