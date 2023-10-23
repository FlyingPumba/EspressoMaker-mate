package org.mate.commons.interaction.action.espresso.root_matchers;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.test.espresso.Root;

import org.hamcrest.Matcher;
import org.mate.commons.utils.AbstractCodeProducer;

public abstract class EspressoRootMatcher extends AbstractCodeProducer implements Parcelable {

    /**
     * The type of Espresso RootMatcher being represented by this instance.
     */
    private final EspressoRootMatcherType type;

    public EspressoRootMatcher(EspressoRootMatcherType type) {
        this.type = type;
    }

    /**
     * @return the type of Espresso RootMatcher being represented by this instance.
     */
    public EspressoRootMatcherType getType() {
        return type;
    }

    /**
     * Get the actual Espresso's RootMatcher instance represented by this EspressoRootMatcher.
     */
    public abstract Matcher<Root> getRootMatcher();

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.type == null ? -1 : this.type.ordinal());
    }

    public static final Creator<EspressoRootMatcher> CREATOR = new Creator<EspressoRootMatcher>() {
        @Override
        public EspressoRootMatcher createFromParcel(Parcel source) {
            return EspressoRootMatcher.getConcreteClass(source);
        }

        @Override
        public EspressoRootMatcher[] newArray(int size) {
            return new EspressoRootMatcher[size];
        }
    };

    @Override
    public String toString() {
        return this.getCode();
    }

    /**
     * Auxiliary method to build an EspressoRootMatcher from a Parcel, using the correct subclass.
     * In order to do so, this method looks at the first integer in the Parcel.
     * Depending on the value, it will use the appropriate constructor from a subclass.
     * <p>
     * DO NOT use here the CREATOR classes inside each of the EspressoRootMatcher subclasses.
     * Doing so will cause an infinite recursion, since they call this method in turn indirectly.
     *
     * @param source
     * @return
     */
    private static EspressoRootMatcher getConcreteClass(Parcel source) {
        int tmpType = source.readInt();
        EspressoRootMatcherType type = tmpType == -1 ? null :
                EspressoRootMatcherType.values()[tmpType];

        switch (type) {
            case IS_DIALOG:
                return new IsDialogMatcher(source);
            case IS_PLATFORM_POPUP:
                return new IsPlatformPopupMatcher(source);
            case IS_SYSTEM_ALERT_WINDOW:
                return new IsSystemAlertWindowMatcher(source);
            case WITH_WINDOW_TYPE:
                return new WithWindowTypeMatcher(source);
            default:
                throw new IllegalStateException("Invalid int for EspressoRootMatcher type found: " +
                        type);
        }
    }
}
