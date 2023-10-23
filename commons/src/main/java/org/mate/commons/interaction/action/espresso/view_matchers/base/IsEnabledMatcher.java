package org.mate.commons.interaction.action.espresso.view_matchers.base;

import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.isNotEnabled;

import android.os.Parcel;
import android.view.View;

import org.hamcrest.Matcher;
import org.mate.commons.interaction.action.espresso.view_matchers.EspressoViewMatcher;
import org.mate.commons.interaction.action.espresso.view_matchers.EspressoViewMatcherType;

import java.util.HashSet;
import java.util.Set;

/**
 * Implements an Espresso Matcher for targeting the views that are enabled or not.
 */
public class IsEnabledMatcher extends EspressoViewMatcher {

    /**
     * The enabled state to match against.
     */
    private final boolean enabled;

    public IsEnabledMatcher(boolean enabled) {
        super(EspressoViewMatcherType.IS_ENABLED);
        this.enabled = enabled;
    }

    public IsEnabledMatcher(String newValue) {
        this("true".equals(newValue));
    }

    @Override
    public String getCode() {
        if (enabled) {
            return "isEnabled()";
        } else {
            return "isNotEnabled()";
        }
    }

    @Override
    public Matcher<View> getViewMatcher() {
        if (enabled) {
            return isEnabled();
        } else {
            return isNotEnabled();
        }
    }

    @Override
    public Set<String> getNeededClassImports() {
        return new HashSet<>();
    }

    @Override
    public Set<String> getNeededStaticImports() {
        HashSet<String> imports = new HashSet<>();

        if (enabled) {
            imports.add("androidx.test.espresso.matcher.ViewMatchers.isEnabled");
        } else {
            imports.add("androidx.test.espresso.matcher.ViewMatchers.isNotEnabled");
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
        dest.writeInt(this.enabled ? 1 : 0);
    }

    public IsEnabledMatcher(Parcel source) {
        this(source.readInt() == 1);
    }

    public static final Creator<IsEnabledMatcher> CREATOR = new Creator<IsEnabledMatcher>() {
        @Override
        public IsEnabledMatcher createFromParcel(Parcel source) {
            // We need to use the EspressoViewMatcher.CREATOR here, because we want to make sure
            // to remove the EspressoViewMatcher's type integer from the beginning of Parcel and
            // call the appropriate constructor for this action.
            // Otherwise, the first integer will be read as data for an instance variable.
            return (IsEnabledMatcher) EspressoViewMatcher.CREATOR.createFromParcel(source);
        }

        @Override
        public IsEnabledMatcher[] newArray(int size) {
            return new IsEnabledMatcher[size];
        }
    };
}
