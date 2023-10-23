package org.mate.commons.interaction.action.espresso.root_matchers;

import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.test.espresso.Root;

import org.hamcrest.Matcher;

import java.util.HashSet;
import java.util.Set;

/**
 * Implements a isPlatformPopup Root matcher.
 */
public class IsPlatformPopupMatcher extends EspressoRootMatcher implements Parcelable {

    public IsPlatformPopupMatcher() {
        super(EspressoRootMatcherType.IS_PLATFORM_POPUP);
    }

    @Override
    public Matcher<Root> getRootMatcher() {
        return isPlatformPopup();
    }

    @Override
    public String getCode() {
        return "isPlatformPopup()";
    }

    @Override
    public Set<String> getNeededClassImports() {
        return new HashSet<>();
    }

    @Override
    public Set<String> getNeededStaticImports() {
        Set<String> imports = new HashSet<>();
        imports.add("androidx.test.espresso.matcher.RootMatchers.isPlatformPopup");
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

    public IsPlatformPopupMatcher(Parcel in) {
        this();
    }

    public static final Creator<IsPlatformPopupMatcher> CREATOR = new Creator<IsPlatformPopupMatcher>() {
        @Override
        public IsPlatformPopupMatcher createFromParcel(Parcel source) {
            // We need to use the EspressoViewMatcher.CREATOR here, because we want to make sure
            // to remove the EspressoViewMatcher's type integer from the beginning of Parcel and
            // call the appropriate constructor for this action.
            // Otherwise, the first integer will be read as data for an instance variable.
            return (IsPlatformPopupMatcher) EspressoRootMatcher.CREATOR.createFromParcel(source);
        }

        @Override
        public IsPlatformPopupMatcher[] newArray(int size) {
            return new IsPlatformPopupMatcher[size];
        }
    };
}
