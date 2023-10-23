package org.mate.commons.interaction.action.espresso.root_matchers;

import static androidx.test.espresso.matcher.RootMatchers.isDialog;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.test.espresso.Root;

import org.hamcrest.Matcher;

import java.util.HashSet;
import java.util.Set;

/**
 * Implements a isDialog Root matcher.
 */
public class IsDialogMatcher extends EspressoRootMatcher implements Parcelable {

    public IsDialogMatcher() {
        super(EspressoRootMatcherType.IS_DIALOG);
    }

    @Override
    public Matcher<Root> getRootMatcher() {
        return isDialog();
    }

    @Override
    public String getCode() {
        return "isDialog()";
    }

    @Override
    public Set<String> getNeededClassImports() {
        return new HashSet<>();
    }

    @Override
    public Set<String> getNeededStaticImports() {
        Set<String> imports = new HashSet<>();
        imports.add("androidx.test.espresso.matcher.RootMatchers.isDialog");
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

    public IsDialogMatcher(Parcel in) {
        this();
    }

    public static final Creator<IsDialogMatcher> CREATOR = new Creator<IsDialogMatcher>() {
        @Override
        public IsDialogMatcher createFromParcel(Parcel source) {
            // We need to use the EspressoViewMatcher.CREATOR here, because we want to make sure
            // to remove the EspressoViewMatcher's type integer from the beginning of Parcel and
            // call the appropriate constructor for this action.
            // Otherwise, the first integer will be read as data for an instance variable.
            return (IsDialogMatcher) EspressoRootMatcher.CREATOR.createFromParcel(source);
        }

        @Override
        public IsDialogMatcher[] newArray(int size) {
            return new IsDialogMatcher[size];
        }
    };
}
