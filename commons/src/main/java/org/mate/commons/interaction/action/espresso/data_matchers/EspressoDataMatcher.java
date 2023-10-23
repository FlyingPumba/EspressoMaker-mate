package org.mate.commons.interaction.action.espresso.data_matchers;

import android.os.Parcel;
import android.os.Parcelable;

import org.hamcrest.Matcher;
import org.mate.commons.interaction.action.espresso.data_matchers.recursive.AllOfDataMatcher;
import org.mate.commons.utils.AbstractCodeProducer;

/**
 * Represents an actual Espresso's DataMatcher.
 */
public abstract class EspressoDataMatcher extends AbstractCodeProducer implements Parcelable {

    /**
     * The type of Espresso DataMatcher being represented by this instance.
     */
    public final EspressoDataMatcherType type;

    protected EspressoDataMatcher(EspressoDataMatcherType type) {
        this.type = type;
    }

    /**
     * Get the actual Espresso's DataMatcher instance represented by this EspressoDataMatcher.
     */
    public abstract Matcher<?> getDataMatcher();

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.type == null ? -1 : this.type.ordinal());
    }

    public static final Creator<EspressoDataMatcher> CREATOR = new Creator<EspressoDataMatcher>() {
        @Override
        public EspressoDataMatcher createFromParcel(Parcel source) {
            return EspressoDataMatcher.getConcreteClass(source);
        }

        @Override
        public EspressoDataMatcher[] newArray(int size) {
            return new EspressoDataMatcher[size];
        }
    };

    private static EspressoDataMatcher getConcreteClass(Parcel source) {
        int tmpType = source.readInt();
        EspressoDataMatcherType type = tmpType == -1 ? null :
                EspressoDataMatcherType.values()[tmpType];

        if (type == null) {
            throw new IllegalStateException("Found null value for EspressoDataMatcher type.");
        }

        switch (type) {
            case ALL_OFF:
                return new AllOfDataMatcher(source);
            default:
                throw new IllegalStateException("Invalid int for EspressoDataMatcher type found: " +
                        type);
        }
    }

}
