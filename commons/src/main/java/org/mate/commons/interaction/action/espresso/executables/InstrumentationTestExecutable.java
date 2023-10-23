package org.mate.commons.interaction.action.espresso.executables;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

import androidx.annotation.NonNull;

import org.mate.commons.interaction.action.espresso.EspressoView;
import org.mate.commons.interaction.action.espresso.executables.compose.ClickOnComposeNode;
import org.mate.commons.interaction.action.espresso.executables.recyclerview.ClickOnPositionExecutable;
import org.mate.commons.interaction.action.espresso.executables.recyclerview.ScrollToPositionExecutable;
import org.mate.commons.interaction.action.espresso.executables.views.BackExecutable;
import org.mate.commons.interaction.action.espresso.executables.views.ClearTextExecutable;
import org.mate.commons.interaction.action.espresso.executables.views.ClickExecutable;
import org.mate.commons.interaction.action.espresso.executables.views.CloseSoftKeyboardExecutable;
import org.mate.commons.interaction.action.espresso.executables.views.DoubleClickExecutable;
import org.mate.commons.interaction.action.espresso.executables.views.EnterExecutable;
import org.mate.commons.interaction.action.espresso.executables.views.HomeExecutable;
import org.mate.commons.interaction.action.espresso.executables.views.LongClickExecutable;
import org.mate.commons.interaction.action.espresso.executables.views.MenuExecutable;
import org.mate.commons.interaction.action.espresso.executables.views.PressIMEExecutable;
import org.mate.commons.interaction.action.espresso.executables.views.ScrollToExecutable;
import org.mate.commons.interaction.action.espresso.executables.views.SearchExecutable;
import org.mate.commons.interaction.action.espresso.executables.views.SwipeDownExecutable;
import org.mate.commons.interaction.action.espresso.executables.views.SwipeLeftExecutable;
import org.mate.commons.interaction.action.espresso.executables.views.SwipeRightExecutable;
import org.mate.commons.interaction.action.espresso.executables.views.SwipeUpExecutable;
import org.mate.commons.interaction.action.espresso.executables.views.ToggleRotationExecutable;
import org.mate.commons.interaction.action.espresso.executables.views.TypeTextExecutable;
import org.mate.commons.interaction.action.espresso.interactions.EspressoDataInteraction;
import org.mate.commons.interaction.action.espresso.interactions.EspressoViewInteraction;
import org.mate.commons.interaction.action.espresso.interactions.UiDeviceInteraction;
import org.mate.commons.state.espresso.EspressoScreen;
import org.mate.commons.utils.AbstractCodeProducer;

/**
 * Represents an actual action that can be executed during an Android's InstrumentationTest.
 */
public abstract class InstrumentationTestExecutable extends AbstractCodeProducer implements Parcelable {

    /**
     * The type of action being represented by this instance.
     */
    private final InstrumentationTestExecutableType type;

    public InstrumentationTestExecutable(InstrumentationTestExecutableType type) {
        this.type = type;
    }

    /**
     * @return the type of action being represented by this instance.
     */
    public InstrumentationTestExecutableType getType() {
        return type;
    }

    /**
     * Returns a boolean indicating whether this EspressoViewAction can be performed on the given
     * View.
     */
    public boolean isValidForViewInScreen(View view, EspressoScreen espressoScreen) {
        if (!view.isEnabled()) {
            // We don't perform actions on disabled views.
            return false;
        }

        return isValidForEnabledViewInScreen(view, espressoScreen);
    }

    /**
     * Returns a boolean indicating whether this EspressoViewAction allows the use of RootMatcher
     * with it when available.
     *
     * @return a boolean.
     */
    public boolean allowsRootMatcher() {
        return true;
    }

    /**
     * Returns a boolean indicating whether this EspressoViewAction can be performed on the given
     * (enabled) View.
     * Each implementation of this method should use the actual constraints provided by the
     * actual Espresso's ViewAction. E.g., click().getConstraints().matches(view)
     */
    public abstract boolean isValidForEnabledViewInScreen(View view, EspressoScreen espressoScreen);

    /**
     * Some actions may need to setup internal parameters based
     * on the specific view they are interacting with
     *
     * @param espressoView
     */
    public void setParametersForView(EspressoView espressoView) {
        // most actions don't need to do anything
    }

    @NonNull
    @Override
    public String toString() {
        return this.getCode();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.type == null ? -1 : this.type.ordinal());
    }

    public static final Creator<InstrumentationTestExecutable> CREATOR = new Creator<InstrumentationTestExecutable>() {
        @Override
        public InstrumentationTestExecutable createFromParcel(Parcel source) {
            return InstrumentationTestExecutable.getConcreteClass(source);
        }

        @Override
        public InstrumentationTestExecutable[] newArray(int size) {
            return new InstrumentationTestExecutable[size];
        }
    };

    /**
     * Auxiliary method to build an EspressoViewAction from a Parcel, using the correct subclass.
     * In order to do so, this method looks at the first integer in the Parcel.
     * Depending on the value, it will use the appropriate constructor from a subclass.
     * <p>
     * DO NOT use here the CREATOR classes inside each of the EspressoViewAction subclasses.
     * Doing so will cause an infinite recursion, since they call this method in turn indirectly.
     *
     * @param source
     * @return
     */
    private static InstrumentationTestExecutable getConcreteClass(Parcel source) {
        int tmpType = source.readInt();
        InstrumentationTestExecutableType type = tmpType == -1 ? null : InstrumentationTestExecutableType.values()[tmpType];

        switch (type) {
            case CLICK:
                return new ClickExecutable(source);
            case DOUBLE_CLICK:
                return new DoubleClickExecutable(source);
            case LONG_CLICK:
                return new LongClickExecutable(source);
            case CLEAR_TEXT:
                return new ClearTextExecutable(source);
            case TYPE_TEXT:
                return new TypeTextExecutable(source);
            case SWIPE_UP:
                return new SwipeUpExecutable(source);
            case SWIPE_DOWN:
                return new SwipeDownExecutable(source);
            case SWIPE_LEFT:
                return new SwipeLeftExecutable(source);
            case SWIPE_RIGHT:
                return new SwipeRightExecutable(source);
            case SCROLL_TO:
                return new ScrollToExecutable(source);
            case BACK:
                return new BackExecutable(source);
            case MENU:
                return new MenuExecutable(source);
            case ENTER:
                return new EnterExecutable(source);
            case HOME:
                return new HomeExecutable(source);
            case SEARCH:
                return new SearchExecutable(source);
            case PRESS_IME:
                return new PressIMEExecutable(source);
            case CLOSE_SOFT_KEYBOARD:
                return new CloseSoftKeyboardExecutable(source);
            case RECYCLER_SCROLL_TO_POSITION:
                return new ScrollToPositionExecutable(source);
            case RECYCLER_CLICK_ON_POSITION:
                return new ClickOnPositionExecutable(source);
            case TOGGLE_ROTATION:
                return new ToggleRotationExecutable(source);
            case COMPOSE_CLICK:
                return new ClickOnComposeNode(source);
            default:
                throw new IllegalStateException("Invalid int for EspressoViewAction type found: " +
                        type);
        }
    }
}
