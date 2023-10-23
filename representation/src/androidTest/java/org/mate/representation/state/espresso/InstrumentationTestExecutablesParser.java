package org.mate.representation.state.espresso;

import androidx.annotation.NonNull;

import org.mate.commons.interaction.action.espresso.EspressoView;
import org.mate.commons.interaction.action.espresso.executables.InstrumentationTestExecutable;
import org.mate.commons.interaction.action.espresso.executables.recyclerview.ClickOnPositionExecutable;
import org.mate.commons.interaction.action.espresso.executables.recyclerview.ScrollToPositionExecutable;
import org.mate.commons.interaction.action.espresso.executables.views.BackExecutable;
import org.mate.commons.interaction.action.espresso.executables.views.ClearTextExecutable;
import org.mate.commons.interaction.action.espresso.executables.views.ClickExecutable;
import org.mate.commons.interaction.action.espresso.executables.views.CloseSoftKeyboardExecutable;
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
import org.mate.commons.interaction.action.espresso.executables.compose.ClickOnComposeNode;
import org.mate.commons.state.espresso.EspressoScreen;
import org.mate.representation.DeviceInfo;
import org.mate.representation.input_generation.TextDataGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Parses the actions available to be executed on a given View.
 */
public class InstrumentationTestExecutablesParser {

    private final EspressoScreen espressoScreen;
    private final EspressoView espressoView;

    public InstrumentationTestExecutablesParser(EspressoScreen espressoScreen, EspressoView espressoView) {
        this.espressoScreen = espressoScreen;
        this.espressoView = espressoView;
    }

    /**
     * @return a list of Espresso ViewActions available for the given View.
     */
    public List<InstrumentationTestExecutable> parse() {
        List<InstrumentationTestExecutable> parsedActions = new ArrayList<>();

        if (espressoView.shouldBeSkipped()) {
            return parsedActions;
        }

        // There are many actions that we can possible perform on a View (e.g., Click, ClearText,
        // etc). To determine if an action is valid for a View, we check that the later matches the
        // constraints imposed by the actual Espresso's ViewAction.
        List<InstrumentationTestExecutable> possibleActions = getAvailableExecutables();

        for (InstrumentationTestExecutable executable : possibleActions) {
            if (executable.isValidForViewInScreen(espressoView.getView(), espressoScreen)) {

                executable.setParametersForView(espressoView);

                if (executable instanceof TypeTextExecutable) {
                    // change empty text for a more interesting one
                    ((TypeTextExecutable) executable).setText(TextDataGenerator.getInstance().
                            generateTextData(espressoView));
                }

                parsedActions.add(executable);
            }
        }

        return parsedActions;
    }

    @NonNull
    private List<InstrumentationTestExecutable> getAvailableExecutables() {
        InstrumentationTestExecutable[] possibleExecutables = {
            new BackExecutable(),
            new ClearTextExecutable(),
            new ClickExecutable(),
            // Double click actions are disabled for now, since they can lead to flaky tests
            // new DoubleClickExecutable(),
            new EnterExecutable(),
            new LongClickExecutable(),
            new MenuExecutable(),
            // PressIMEExecutable actions are disabled for now, since they can lead to flaky tests.
            // new PressIMEExecutable(),
            // ScrollTo actions are disabled for now, since their implementation is not
            // completely right and are prone to fail (e.g., when items in ScrollView have
            // padding)
            // new ScrollToExecutable(),
            new SwipeDownExecutable(),
            new SwipeLeftExecutable(),
            new SwipeRightExecutable(),
            new SwipeUpExecutable(),
            // We use empty text for the TypeTextExecutable until we know if we can use it for this view
            new TypeTextExecutable(""),
            new ScrollToPositionExecutable(),
            new ClickOnPositionExecutable(),
            // ToggleRotation actions are disabled for now, since they can lead to flaky tests.
            // new ToggleRotationExecutable(),
            // Home actions are disabled for now. They don't really add much to the test case,
            // since it always leads to the same state (the home screen) and then the test finishes.
            // new HomeExecutable(),
            new SearchExecutable(),
            // We disable this action for the experiments
            // new ClickOnComposeNode()
        };

        List<InstrumentationTestExecutable> result = new ArrayList<>(Arrays.asList(possibleExecutables));

        if (DeviceInfo.getInstance().isKeyboardOpened()) {
            result.add(new CloseSoftKeyboardExecutable());
        }

        return result;
    }
}
