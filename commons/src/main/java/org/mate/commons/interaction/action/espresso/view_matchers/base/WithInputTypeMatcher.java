package org.mate.commons.interaction.action.espresso.view_matchers.base;

import static androidx.test.espresso.matcher.ViewMatchers.withInputType;

import android.os.Parcel;
import android.view.View;

import org.hamcrest.Matcher;
import org.mate.commons.interaction.action.espresso.view_matchers.EspressoViewMatcher;
import org.mate.commons.interaction.action.espresso.view_matchers.EspressoViewMatcherType;

import java.util.HashSet;
import java.util.Set;

/**
 * Implements an Espresso Matcher for targeting the views that have a certain input type.
 */
public class WithInputTypeMatcher extends EspressoViewMatcher {

    /**
     * The input type to match against.
     */
    private final int inputType;

    public WithInputTypeMatcher(int inputType) {
        super(EspressoViewMatcherType.WITH_INPUT_TYPE);
        this.inputType = inputType;
    }

    public WithInputTypeMatcher(String newValue) {
        this(Integer.parseInt(newValue));
    }

    @Override
    public String getCode() {
        return String.format("withInputType(%d)", inputType);
    }

    @Override
    public Matcher<View> getViewMatcher() {
        return withInputType(inputType);
    }

    @Override
    public Set<String> getNeededClassImports() {
        return new HashSet<>();
    }

    @Override
    public Set<String> getNeededStaticImports() {
        HashSet<String> imports = new HashSet<>();
        imports.add("androidx.test.espresso.matcher.ViewMatchers.withInputType");
        return imports;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.inputType);
    }

    public WithInputTypeMatcher(Parcel source) {
        this(source.readInt());
    }

    public static final Creator<WithInputTypeMatcher> CREATOR = new Creator<WithInputTypeMatcher>() {
        @Override
        public WithInputTypeMatcher createFromParcel(Parcel source) {
            // We need to use the EspressoViewMatcher.CREATOR here, because we want to make sure
            // to remove the EspressoViewMatcher's type integer from the beginning of Parcel and
            // call the appropriate constructor for this action.
            // Otherwise, the first integer will be read as data for an instance variable.
            return (WithInputTypeMatcher) EspressoViewMatcher.CREATOR.createFromParcel(source);
        }

        @Override
        public WithInputTypeMatcher[] newArray(int size) {
            return new WithInputTypeMatcher[size];
        }
    };
}
