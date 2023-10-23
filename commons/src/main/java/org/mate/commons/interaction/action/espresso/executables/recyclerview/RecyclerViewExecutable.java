package org.mate.commons.interaction.action.espresso.executables.recyclerview;

import android.os.Parcel;
import android.view.View;

import org.mate.commons.interaction.action.espresso.EspressoView;
import org.mate.commons.interaction.action.espresso.executables.InstrumentationTestExecutableType;
import org.mate.commons.interaction.action.espresso.executables.PerformableEspressoViewExecutable;
import org.mate.commons.state.espresso.EspressoScreen;
import org.mate.commons.utils.Optional;
import org.mate.commons.utils.Randomness;

/**
 * Base class for all Espresso actions that operate on a specific item position of a RecyclerView.
 */
public abstract class RecyclerViewExecutable extends PerformableEspressoViewExecutable {

    /**
     * The position of the item to perform the action on.
     */
    protected int position = -1;

    RecyclerViewExecutable(InstrumentationTestExecutableType type) {
        super(type);
    }

    @Override
    public boolean isValidForEnabledViewInScreen(View view, EspressoScreen espressoScreen) {
        Optional<RecyclerViewWrapper> optionalWrapper = RecyclerViewWrapper.createFor(view);

        if (!optionalWrapper.hasValue()) {
            // View is not a RecyclerView
            return false;
        }

        RecyclerViewWrapper wrapper = optionalWrapper.getValue();
        return wrapper.hasAdapter() && wrapper.getItemCount() > 0;
    }

    @Override
    public void setParametersForView(EspressoView view) {
        Optional<RecyclerViewWrapper> optionalWrapper = RecyclerViewWrapper.createFor(view.getView());
        RecyclerViewWrapper wrapper = optionalWrapper.getValue();

        int itemCount = wrapper.getItemCount();
        this.position = Randomness.getRnd().nextInt(itemCount);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.position);
    }
}
