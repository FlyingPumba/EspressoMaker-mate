package org.mate.commons.interaction.action.espresso.interactions;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.test.espresso.Root;

import org.hamcrest.Matcher;
import org.mate.commons.interaction.action.espresso.EspressoView;
import org.mate.commons.interaction.action.espresso.assertions.EspressoViewAssertion;
import org.mate.commons.interaction.action.espresso.executables.InstrumentationTestExecutable;
import org.mate.commons.interaction.action.espresso.view_matchers.EspressoViewMatcher;
import org.mate.commons.utils.AbstractCodeProducer;

/**
 * An abstract class representing a UI interaction that happens during an InstrumentationTest.
 * <p>
 * Android InstrumentationTests are the ones that are executed on an emulator or device by using the command `am
 * instrument`. When doing this, a special test execution environment is launched, where the targeted application
 * process is restarted and initialized with basic application context, and an instrumentation thread is started
 * inside the application process VM. The test code starts execution on this instrumentation thread and is provided
 * with an **Instrumentation** instance that provides access to the application context and APIs to manipulate the
 * application process under test.
 * More info can be found in <a href="https://source.android.com/docs/core/tests/development/instrumentation">this link</a>.
 */
public abstract class InstrumentationTestInteraction extends AbstractCodeProducer implements Parcelable {

    /**
     * The type of Interaction being represented by this instance.
     */
    protected final InteractionType type;

    /**
     * The view matcher used to identify the view to interact with.
     */
    protected final EspressoViewMatcher viewMatcher;

    /**
     * A root matcher to decide on which window the interaction should be performed.
     */
    protected Matcher<Root> rootMatcher = null;

    public InstrumentationTestInteraction(InteractionType type, EspressoViewMatcher viewMatcher) {
        this.type = type;
        this.viewMatcher = viewMatcher;
    }

    /**
     * @return the type of Interaction being represented by this instance.
     */
    public InteractionType getType() {
        return type;
    }

    /**
     * Set the root matcher for this interaction.
     *
     * @param rootMatcher The root matcher to be set.
     * @return this instance
     */
    public InstrumentationTestInteraction inRoot(Matcher<Root> rootMatcher) {
        this.rootMatcher = rootMatcher;
        return this;
    }

    /**
     * Some interactions may need to setup internal parameters based
     * on the specific view they are interacting with
     *
     * @param espressoView
     */
    public void setParametersForView(EspressoView espressoView) {
        // most interactions don't need to do anything
    }

    @Override
    public int describeContents() {
        return 0;
    }

    protected InstrumentationTestInteraction(Parcel in, InteractionType type) {
        this.type = type;
        this.viewMatcher = in.readParcelable(EspressoViewMatcher.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.type == null ? -1 : this.type.ordinal());
        dest.writeParcelable(this.viewMatcher, flags);
    }

    /**
     * Auxiliary method to build an InstrumentationTestInteraction from a Parcel, using the correct subclass.
     * In order to do so, this method looks at the first integer in the Parcel.
     * Depending on the value, it will use the appropriate constructor from a subclass.
     * <p>
     * DO NOT use here the CREATOR classes inside each of the InstrumentationTestInteraction subclasses.
     * Doing so will cause an infinite recursion, since they call this method in turn indirectly.
     *
     * @param source
     * @return
     */
    private static InstrumentationTestInteraction getConcreteClass(Parcel source) {
        int tmpType = source.readInt();
        InteractionType type = tmpType == -1 ? null :
                InteractionType.values()[tmpType];

        if (type == null) {
            throw new IllegalStateException("Found null value for EspressoInteraction type.");
        }

        switch (type) {
            case ESPRESSO_VIEW_INTERACTION:
                return new EspressoViewInteraction(source);
            case ESPRESSO_DATA_INTERACTION:
                return new EspressoDataInteraction(source);
            case UI_DEVICE_INTERACTION:
                return new UiDeviceInteraction(source);
            default:
                throw new IllegalStateException("Invalid int for EspressoInteraction type found: " +
                        type);
        }
    }

    public static final Creator<InstrumentationTestInteraction> CREATOR = new Creator<InstrumentationTestInteraction>() {
        @Override
        public InstrumentationTestInteraction createFromParcel(Parcel source) {
            return InstrumentationTestInteraction.getConcreteClass(source);
        }

        @Override
        public InstrumentationTestInteraction[] newArray(int size) {
            return new InstrumentationTestInteraction[size];
        }
    };

    /**
     * Given an action, the action itself will resolve the proper
     * method dispatch to be used with current interaction.
     * e.g: perform(composeAction) -> composeAction.performForComposeInteraction(this).
     */
    public abstract void perform(InstrumentationTestExecutable action);

    /**
     * Execute an assertion over the UI element located by this interaction.
     */
    public abstract void check(EspressoViewAssertion assertion);

    /**
     * Returns the specific EspressoView that this interaction will target inside a base
     * EspressoView.
     * @param baseEspressoView
     * @return
     */
    public abstract EspressoView getTargetEspressoView(EspressoView baseEspressoView);
}
