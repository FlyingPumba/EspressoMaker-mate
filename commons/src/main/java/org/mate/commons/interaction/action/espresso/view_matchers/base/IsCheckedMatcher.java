package org.mate.commons.interaction.action.espresso.view_matchers.base;

import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.isNotChecked;

import android.os.Parcel;
import android.view.View;

import org.hamcrest.Matcher;
import org.mate.commons.interaction.action.espresso.view_matchers.EspressoViewMatcher;
import org.mate.commons.interaction.action.espresso.view_matchers.EspressoViewMatcherType;

import java.util.HashSet;
import java.util.Set;

/**
 * Implements an Espresso Matcher for targeting the views that are checked or not.
 */
public class IsCheckedMatcher extends EspressoViewMatcher {

    /**
     * The checked state to match against.
     */
    private final boolean checked;

    public IsCheckedMatcher(boolean checked) {
        super(EspressoViewMatcherType.IS_CHECKED);
        this.checked = checked;
    }

    public IsCheckedMatcher(String newValue) {
        this("true".equals(newValue));
    }

    @Override
    public String getCode() {
        if (checked) {
            return "isChecked()";
        } else {
            return "isNotChecked()";
        }
    }

    @Override
    public Matcher<View> getViewMatcher() {
        if (checked) {
            return isChecked();
        } else {
            return isNotChecked();
        }
    }

    @Override
    public Set<String> getNeededClassImports() {
        return new HashSet<>();
    }

    @Override
    public Set<String> getNeededStaticImports() {
        HashSet<String> imports = new HashSet<>();

        if (checked) {
            imports.add("androidx.test.espresso.matcher.ViewMatchers.isChecked");
        } else {
            imports.add("androidx.test.espresso.matcher.ViewMatchers.isNotChecked");
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
        dest.writeInt(this.checked ? 1 : 0);
    }

    public IsCheckedMatcher(Parcel source) {
        this(source.readInt() == 1);
    }

    public static final Creator<IsCheckedMatcher> CREATOR = new Creator<IsCheckedMatcher>() {
        @Override
        public IsCheckedMatcher createFromParcel(Parcel source) {
            // We need to use the EspressoViewMatcher.CREATOR here, because we want to make sure
            // to remove the EspressoViewMatcher's type integer from the beginning of Parcel and
            // call the appropriate constructor for this action.
            // Otherwise, the first integer will be read as data for an instance variable.
            return (IsCheckedMatcher) EspressoViewMatcher.CREATOR.createFromParcel(source);
        }

        @Override
        public IsCheckedMatcher[] newArray(int size) {
            return new IsCheckedMatcher[size];
        }
    };
}
