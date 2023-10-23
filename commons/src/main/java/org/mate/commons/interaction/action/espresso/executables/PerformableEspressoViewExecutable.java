package org.mate.commons.interaction.action.espresso.executables;

import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewInteraction;

import org.mate.commons.interaction.action.espresso.interactions.EspressoViewInteraction;

/**
 * An Espresso View Action that needs to be executed using a "perform" prefix.
 * By inheritance it resolves the getCode properly for this kind of actions, and end up wrapping its real code with 
 * 'perform(...)'.
 */
abstract public class PerformableEspressoViewExecutable extends InstrumentationTestExecutable {
    public PerformableEspressoViewExecutable(InstrumentationTestExecutableType type) {
        super(type);
    }

    @Override
    public String getCode() {
        return String.format("perform(%s)", getPerformableCode());
    }

    protected abstract String getPerformableCode();

    /**
     * Get the actual Espresso's ViewAction instance represented by this EspressoViewAction.
     */
    public abstract ViewAction getViewAction();
}
