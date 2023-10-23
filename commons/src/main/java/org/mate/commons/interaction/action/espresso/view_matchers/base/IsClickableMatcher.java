package org.mate.commons.interaction.action.espresso.view_matchers.base;

import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isNotClickable;

import android.os.Parcel;
import android.view.View;

import org.hamcrest.Matcher;
import org.mate.commons.interaction.action.espresso.view_matchers.EspressoViewMatcher;
import org.mate.commons.interaction.action.espresso.view_matchers.EspressoViewMatcherType;

import java.util.HashSet;
import java.util.Set;

/**
 * Implements an Espresso Matcher for targeting the views that are clickable or not.
 */
public class IsClickableMatcher extends EspressoViewMatcher {

    /**
     * The clickable state to match against.
     */
    private final boolean clickable;

    public IsClickableMatcher(boolean clickable) {
        super(EspressoViewMatcherType.IS_CLICKABLE);
        this.clickable = clickable;
    }

    public IsClickableMatcher(String newValue) {
        this("true".equals(newValue));
    }

    @Override
    public String getCode() {
        if (clickable) {
            return "isClickable()";
        } else {
            return "isNotClickable()";
        }
    }

    @Override
    public Matcher<View> getViewMatcher() {
        if (clickable) {
            return isClickable();
        } else {
            return isNotClickable();
        }
    }

    @Override
    public Set<String> getNeededClassImports() {
        return new HashSet<>();
    }

    @Override
    public Set<String> getNeededStaticImports() {
        HashSet<String> imports = new HashSet<>();

        if (clickable) {
            imports.add("androidx.test.espresso.matcher.ViewMatchers.isClickable");
        } else {
            imports.add("androidx.test.espresso.matcher.ViewMatchers.isNotClickable");
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
        dest.writeInt(this.clickable ? 1 : 0);
    }

    public IsClickableMatcher(Parcel source) {
        this(source.readInt() == 1);
    }

    public static final Creator<IsClickableMatcher> CREATOR = new Creator<IsClickableMatcher>() {
        @Override
        public IsClickableMatcher createFromParcel(Parcel source) {
            // We need to use the EspressoViewMatcher.CREATOR here, because we want to make sure
            // to remove the EspressoViewMatcher's type integer from the beginning of Parcel and
            // call the appropriate constructor for this action.
            // Otherwise, the first integer will be read as data for an instance variable.
            return (IsClickableMatcher) EspressoViewMatcher.CREATOR.createFromParcel(source);
        }

        @Override
        public IsClickableMatcher[] newArray(int size) {
            return new IsClickableMatcher[size];
        }
    };
}
