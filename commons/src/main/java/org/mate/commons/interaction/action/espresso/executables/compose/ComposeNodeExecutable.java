package org.mate.commons.interaction.action.espresso.executables.compose;

import android.view.View;

import androidx.test.uiautomator.UiObject2;

import org.mate.commons.interaction.action.espresso.executables.InstrumentationTestExecutable;
import org.mate.commons.interaction.action.espresso.executables.InstrumentationTestExecutableType;
import org.mate.commons.state.espresso.EspressoScreen;

/**
 * Represents an Action to be performed onto a compose node,
 * which may represent a complex hierarchy of SemanticNodes.
 */
public abstract class ComposeNodeExecutable extends InstrumentationTestExecutable {

    public ComposeNodeExecutable(InstrumentationTestExecutableType type) {
        super(type);
    }

    /**
     * Executes this executable's action on the given uiObject.
     * @param uiObject A uiObject2 representing a UI element, as provided by the UiAutomator API.
     */
    public abstract void performActionForNode(UiObject2 uiObject);

    @Override
    public boolean isValidForEnabledViewInScreen(View view, EspressoScreen espressoScreen) {
        return view.getClass().getSimpleName().contains("Compose");
    }
}
