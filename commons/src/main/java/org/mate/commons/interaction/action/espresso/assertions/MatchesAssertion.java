package org.mate.commons.interaction.action.espresso.assertions;

import static androidx.test.espresso.assertion.ViewAssertions.matches;

import android.os.Parcel;

import androidx.test.espresso.ViewAssertion;

import org.mate.commons.interaction.action.espresso.view_matchers.EspressoViewMatcher;

import java.util.HashSet;
import java.util.Set;

/**
 * Implements a "View matches" assertion.
 * You need to provide a ViewMatcher that will be checked in this assertion (e.g., isDisplayed).
 */
public class MatchesAssertion extends EspressoViewAssertion {

    /**
     * The matcher used to assert the view.
     */
    private final EspressoViewMatcher viewMatcherForAssertion;

    public MatchesAssertion(EspressoViewMatcher viewMatcherForAssertion) {
        super(EspressoViewAssertionType.MATCHES);
        this.viewMatcherForAssertion = viewMatcherForAssertion;
    }

    @Override
    public ViewAssertion getViewAssertion() {
        return matches(viewMatcherForAssertion.getViewMatcher());
    }

    @Override
    public String getCode() {
        return String.format("matches(%s)", viewMatcherForAssertion.getCode());
    }

    @Override
    public Set<String> getNeededClassImports() {
        return new HashSet<>(viewMatcherForAssertion.getNeededClassImports());
    }

    @Override
    public Set<String> getNeededStaticImports() {
        Set<String> imports = new HashSet<>(viewMatcherForAssertion.getNeededStaticImports());
        imports.add("androidx.test.espresso.assertion.ViewAssertions.matches");
        return imports;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(this.viewMatcherForAssertion, flags);
    }

    public MatchesAssertion(Parcel in) {
        this((EspressoViewMatcher) in.readParcelable(EspressoViewMatcher.class.getClassLoader()));
    }

    public static final Creator<MatchesAssertion> CREATOR = new Creator<MatchesAssertion>() {
        @Override
        public MatchesAssertion createFromParcel(Parcel source) {
            // We need to use the EspressoViewAssertion.CREATOR here, because we want to make sure
            // to remove the EspressoViewAssertion's type integer from the beginning of Parcel and
            // call the appropriate constructor for this action.
            // Otherwise, the first integer will be read as data for an instance variable.
            return (MatchesAssertion) EspressoViewAssertion.CREATOR.createFromParcel(source);
        }

        @Override
        public MatchesAssertion[] newArray(int size) {
            return new MatchesAssertion[size];
        }
    };
}
