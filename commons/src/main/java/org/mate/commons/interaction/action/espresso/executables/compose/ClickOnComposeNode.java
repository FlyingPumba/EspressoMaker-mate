package org.mate.commons.interaction.action.espresso.executables.compose;

import android.os.Parcel;
import android.view.View;

import androidx.test.uiautomator.UiObject2;

import org.mate.commons.interaction.action.espresso.executables.InstrumentationTestExecutable;
import org.mate.commons.interaction.action.espresso.executables.InstrumentationTestExecutableType;
import org.mate.commons.state.espresso.EspressoScreen;

import java.util.HashSet;
import java.util.Set;

/**
 * Implements an [ComposeNodeExecutable] to click on a certain compose node inside
 * a [ComposeView]. Such node should be clickable.
 */
public class ClickOnComposeNode extends ComposeNodeExecutable {


    public ClickOnComposeNode() {
        super(InstrumentationTestExecutableType.COMPOSE_CLICK);
    }

    @Override
    public void performActionForNode(UiObject2 uiObject) {
        uiObject.click();
    }

    @Override
    public boolean isValidForEnabledViewInScreen(View view, EspressoScreen espressoScreen) {
        return view.getClass().getSimpleName().contains("Compose");
    }

    @Override
    public String getCode() {
        return "click()";
    }

    @Override
    public Set<String> getNeededClassImports() {
        return new HashSet<>();
    }

    @Override
    public Set<String> getNeededStaticImports() {
        return new HashSet<>();
    }

    public ClickOnComposeNode(Parcel in) {
        this();
    }

    public static final Creator<ClickOnComposeNode> CREATOR = new Creator<ClickOnComposeNode>() {
        @Override
        public ClickOnComposeNode createFromParcel(Parcel source) {
            return (ClickOnComposeNode) InstrumentationTestExecutable.CREATOR.createFromParcel(source);
        }

        @Override
        public ClickOnComposeNode[] newArray(int size) {
            return new ClickOnComposeNode[size];
        }
    };


}
