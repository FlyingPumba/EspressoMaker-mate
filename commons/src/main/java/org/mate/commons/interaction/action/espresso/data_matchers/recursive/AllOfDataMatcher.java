package org.mate.commons.interaction.action.espresso.data_matchers.recursive;

import static org.hamcrest.Matchers.allOf;

import android.os.Parcel;

import org.hamcrest.Matcher;
import org.mate.commons.interaction.action.espresso.data_matchers.EspressoDataMatcher;
import org.mate.commons.interaction.action.espresso.data_matchers.EspressoDataMatcherType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Implements an Espresso Data Matcher for targeting the ViewHolders that match against ALL
 * matchers in a list.
 */
public class AllOfDataMatcher extends EspressoDataMatcher {

    /**
     * The recursive matchers.
     */
    protected List<EspressoDataMatcher> matchers;

    public AllOfDataMatcher() {
        this(new ArrayList<>());
    }

    public AllOfDataMatcher(List<EspressoDataMatcher> matchers) {
        super(EspressoDataMatcherType.ALL_OFF);
        this.matchers = matchers;
    }

    @Override
    public Matcher<?> getDataMatcher() {
        return allOf();
    }

    public String getCode() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < matchers.size(); i++) {
            sb.append(matchers.get(i).getCode());
            if (i < matchers.size() - 1) {
                sb.append(", ");
            }
        }

        return String.format("allOf(%s)", sb);
    }

    @Override
    public Set<String> getNeededClassImports() {
        return new HashSet<>();
    }

    @Override
    public Set<String> getNeededStaticImports() {
        Set<String> imports = new HashSet<>();
        imports.add("org.hamcrest.Matchers.allOf");
        return imports;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeTypedList(this.matchers);
    }

    public AllOfDataMatcher(Parcel in) {
        this(in.createTypedArrayList(EspressoDataMatcher.CREATOR));
    }

    public static final Creator<AllOfDataMatcher> CREATOR = new Creator<AllOfDataMatcher>() {
        @Override
        public AllOfDataMatcher createFromParcel(Parcel source) {
            // We need to use the EspressoDataMatcher.CREATOR here, because we want to make sure
            // to remove the EspressoDataMatcher's type integer from the beginning of Parcel and
            // call the appropriate constructor for this action.
            // Otherwise, the first integer will be read as data for an instance variable.
            return (AllOfDataMatcher) EspressoDataMatcher.CREATOR.createFromParcel(source);
        }

        @Override
        public AllOfDataMatcher[] newArray(int size) {
            return new AllOfDataMatcher[size];
        }
    };
}
