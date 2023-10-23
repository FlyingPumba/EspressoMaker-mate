package org.mate.commons.interaction.action.espresso.executables.views;

import static androidx.test.espresso.action.ViewActions.pressImeActionButton;

import android.os.Parcel;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

import androidx.test.espresso.ViewAction;

import org.mate.commons.interaction.action.espresso.executables.InstrumentationTestExecutable;
import org.mate.commons.interaction.action.espresso.executables.InstrumentationTestExecutableType;
import org.mate.commons.interaction.action.espresso.executables.PerformableEspressoViewExecutable;
import org.mate.commons.state.espresso.EspressoScreen;
import org.mate.commons.utils.MATELog;

import java.util.HashSet;
import java.util.Set;

/**
 * Implements a Press IME key Espresso action.
 */
public class PressIMEExecutable extends PerformableEspressoViewExecutable {
    public PressIMEExecutable() {
        super(InstrumentationTestExecutableType.PRESS_IME);
    }

    @Override
    public ViewAction getViewAction() {
        return pressImeActionButton();
    }

    @Override
    public boolean isValidForEnabledViewInScreen(View view, EspressoScreen espressoScreen) {
        if (!hasIMEAction(view)) {
            return false;
        }

        return getViewAction().getConstraints().matches(view);
    }

    /**
     * Returns whether a given View has an IME action or not.
     */
    public boolean hasIMEAction(View view) {
        try {
            EditorInfo editorInfo = new EditorInfo();
            InputConnection inputConnection = view.onCreateInputConnection(editorInfo);
            if (inputConnection == null) {
                // View does not support input methods
                return false;
            }

            int actionId = editorInfo.actionId != 0
                    ? editorInfo.actionId
                    : editorInfo.imeOptions & EditorInfo.IME_MASK_ACTION;

            // No available action on view
            return actionId != EditorInfo.IME_ACTION_NONE;
        } catch (Exception e) {
            MATELog.log_warn("Unable to evaluate if view has IME action: " + e.getMessage());
            return false;
        }
    }

    @Override
    public String getPerformableCode() {
        return "pressImeActionButton()";
    }

    @Override
    public Set<String> getNeededClassImports() {
        return new HashSet<>();
    }

    @Override
    public Set<String> getNeededStaticImports() {
        Set<String> imports = new HashSet<>();
        imports.add("androidx.test.espresso.action.ViewActions.pressImeActionButton");
        return imports;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    public PressIMEExecutable(Parcel in) {
        this();
    }

    public static final Creator<PressIMEExecutable> CREATOR = new Creator<PressIMEExecutable>() {
        @Override
        public PressIMEExecutable createFromParcel(Parcel source) {
            // We need to use the EspressoViewAction.CREATOR here, because we want to make sure
            // to remove the EspressoViewAction's type integer from the beginning of Parcel and call
            // the appropriate constructor for this action.
            // Otherwise, the first integer will be read as data for an instance variable.
            return (PressIMEExecutable) InstrumentationTestExecutable.CREATOR.createFromParcel(source);
        }

        @Override
        public PressIMEExecutable[] newArray(int size) {
            return new PressIMEExecutable[size];
        }
    };
}
