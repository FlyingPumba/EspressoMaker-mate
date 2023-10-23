package org.mate.commons.interaction.action.espresso.root_matchers;

import static androidx.test.espresso.matcher.RootMatchers.isSystemAlertWindow;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.test.espresso.Root;

import org.hamcrest.Matcher;

import java.util.HashSet;
import java.util.Set;

/**
 * Implements a isSystemAlertWindow Root matcher.
 */
public class IsSystemAlertWindowMatcher extends EspressoRootMatcher implements Parcelable {

    public IsSystemAlertWindowMatcher() {
        super(EspressoRootMatcherType.IS_SYSTEM_ALERT_WINDOW);
    }

    @Override
    public Matcher<Root> getRootMatcher() {
        return isSystemAlertWindow();
    }

    @Override
    public String getCode() {
        return "isSystemAlertWindow()";
    }

    @Override
    public Set<String> getNeededClassImports() {
        return new HashSet<>();
    }

    @Override
    public Set<String> getNeededStaticImports() {
        Set<String> imports = new HashSet<>();
        imports.add("androidx.test.espresso.matcher.RootMatchers.isSystemAlertWindow");
        return imports;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    public IsSystemAlertWindowMatcher(Parcel in) {
        this();
    }

    public static final Creator<IsSystemAlertWindowMatcher> CREATOR = new Creator<IsSystemAlertWindowMatcher>() {
        @Override
        public IsSystemAlertWindowMatcher createFromParcel(Parcel source) {
            // We need to use the EspressoViewMatcher.CREATOR here, because we want to make sure
            // to remove the EspressoViewMatcher's type integer from the beginning of Parcel and
            // call the appropriate constructor for this action.
            // Otherwise, the first integer will be read as data for an instance variable.
            return (IsSystemAlertWindowMatcher) EspressoRootMatcher.CREATOR.createFromParcel(source);
        }

        @Override
        public IsSystemAlertWindowMatcher[] newArray(int size) {
            return new IsSystemAlertWindowMatcher[size];
        }
    };
}
