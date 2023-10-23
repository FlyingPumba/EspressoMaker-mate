package org.mate.commons.interaction.action.espresso.view_matchers.base;

import static androidx.test.espresso.matcher.ViewMatchers.withAlpha;

import android.os.Parcel;
import android.view.View;

import org.hamcrest.Matcher;
import org.mate.commons.interaction.action.espresso.view_matchers.EspressoViewMatcher;
import org.mate.commons.interaction.action.espresso.view_matchers.EspressoViewMatcherType;

import java.util.HashSet;
import java.util.Set;

/**
 * Implements an Espresso Matcher for targeting the views that have a certain alpha value.
 */
public class WithAlphaMatcher extends EspressoViewMatcher {

    /**
     * The alpha value to match against.
     */
    private final float alpha;

    public WithAlphaMatcher(float alpha) {
        super(EspressoViewMatcherType.WITH_ALPHA);
        this.alpha = alpha;
    }

    public WithAlphaMatcher(String newValue) {
        this(Float.parseFloat(newValue));
    }

    @Override
    public String getCode() {
        return String.format("withAlpha(%ff)", alpha);
    }

    @Override
    public Matcher<View> getViewMatcher() {
        return withAlpha(alpha);
    }

    @Override
    public Set<String> getNeededClassImports() {
        return new HashSet<>();
    }

    @Override
    public Set<String> getNeededStaticImports() {
        HashSet<String> imports = new HashSet<>();
        imports.add("androidx.test.espresso.matcher.ViewMatchers.withAlpha");
        return imports;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeFloat(this.alpha);
    }

    public WithAlphaMatcher(Parcel source) {
        this(source.readFloat());
    }

    public static final Creator<WithAlphaMatcher> CREATOR = new Creator<WithAlphaMatcher>() {
        @Override
        public WithAlphaMatcher createFromParcel(Parcel source) {
            // We need to use the EspressoViewMatcher.CREATOR here, because we want to make sure
            // to remove the EspressoViewMatcher's type integer from the beginning of Parcel and
            // call the appropriate constructor for this action.
            // Otherwise, the first integer will be read as data for an instance variable.
            return (WithAlphaMatcher) EspressoViewMatcher.CREATOR.createFromParcel(source);
        }

        @Override
        public WithAlphaMatcher[] newArray(int size) {
            return new WithAlphaMatcher[size];
        }
    };
}
