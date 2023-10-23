package org.mate.commons.interaction.action.espresso.assertions;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.test.espresso.ViewAssertion;

import org.mate.commons.utils.AbstractCodeProducer;

public abstract class EspressoViewAssertion extends AbstractCodeProducer implements Parcelable {

    /**
     * The type of Espresso ViewAssertion being represented by this instance.
     */
    private final EspressoViewAssertionType type;

    public EspressoViewAssertion(EspressoViewAssertionType type) {
        this.type = type;
    }

    /**
     * @return the type of Espresso ViewAssertion being represented by this instance.
     */
    public EspressoViewAssertionType getType() {
        return type;
    }

    /**
     * Get the actual Espresso's ViewAssertion instance represented by this EspressoViewAssertion.
     */
    public abstract ViewAssertion getViewAssertion();

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.type == null ? -1 : this.type.ordinal());
    }

    public static final Creator<EspressoViewAssertion> CREATOR =
            new Creator<EspressoViewAssertion>() {
        @Override
        public EspressoViewAssertion createFromParcel(Parcel source) {
            return EspressoViewAssertion.getConcreteClass(source);
        }

        @Override
        public EspressoViewAssertion[] newArray(int size) {
            return new EspressoViewAssertion[size];
        }
    };

    /**
     * Auxiliary method to build an EspressoViewAssertion from a Parcel, using the correct subclass.
     * In order to do so, this method looks at the first integer in the Parcel.
     * Depending on the value, it will use the appropriate constructor from a subclass.
     * <p>
     * DO NOT use here the CREATOR classes inside each of the EspressoViewAssertion subclasses.
     * Doing so will cause an infinite recursion, since they call this method in turn indirectly.
     *
     * @param source
     * @return
     */
    private static EspressoViewAssertion getConcreteClass(Parcel source) {
        int tmpType = source.readInt();
        EspressoViewAssertionType type = tmpType == -1 ? null :
                EspressoViewAssertionType.values()[tmpType];

        if (type == null) {
            throw new IllegalStateException("Found null value for EspressoViewMatcher type.");
        }

        switch (type) {
            case DOES_NOT_EXIST:
                return new DoesNotExistAssertion(source);
            case MATCHES:
                return new MatchesAssertion(source);
            default:
                throw new IllegalStateException("Invalid int for EspressoViewAssertion type " +
                        "found: " + type);
        }
    }
}
