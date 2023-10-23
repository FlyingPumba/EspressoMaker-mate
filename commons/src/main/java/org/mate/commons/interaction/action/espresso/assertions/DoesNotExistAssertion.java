package org.mate.commons.interaction.action.espresso.assertions;

import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;

import android.os.Parcel;

import androidx.test.espresso.ViewAssertion;

import java.util.HashSet;
import java.util.Set;

/**
 * Implements a "View does not exist" assertion.
 */
public class DoesNotExistAssertion extends EspressoViewAssertion {

    public DoesNotExistAssertion() {
        super(EspressoViewAssertionType.DOES_NOT_EXIST);
    }

    @Override
    public ViewAssertion getViewAssertion() {
        return doesNotExist();
    }

    @Override
    public String getCode() {
        return "doesNotExist()";
    }

    @Override
    public Set<String> getNeededClassImports() {
        return new HashSet<>();
    }

    @Override
    public Set<String> getNeededStaticImports() {
        Set<String> imports = new HashSet<>();
        imports.add("androidx.test.espresso.assertion.ViewAssertions.doesNotExist");
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

    public DoesNotExistAssertion(Parcel in) {
        this();
    }

    public static final Creator<DoesNotExistAssertion> CREATOR = new Creator<DoesNotExistAssertion>() {
        @Override
        public DoesNotExistAssertion createFromParcel(Parcel source) {
            // We need to use the EspressoViewAssertion.CREATOR here, because we want to make sure
            // to remove the EspressoViewAssertion's type integer from the beginning of Parcel and
            // call the appropriate constructor for this action.
            // Otherwise, the first integer will be read as data for an instance variable.
            return (DoesNotExistAssertion) EspressoViewAssertion.CREATOR.createFromParcel(source);
        }

        @Override
        public DoesNotExistAssertion[] newArray(int size) {
            return new DoesNotExistAssertion[size];
        }
    };
}
