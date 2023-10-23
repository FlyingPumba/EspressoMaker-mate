package org.mate.commons.interaction.action.espresso.interactions;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

import android.app.Instrumentation;
import android.os.Parcel;

import androidx.compose.ui.semantics.SemanticsNode;
import androidx.compose.ui.semantics.SemanticsProperties;
import androidx.compose.ui.text.AnnotatedString;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.BySelector;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject2;

import org.mate.commons.interaction.action.espresso.EspressoView;
import org.mate.commons.interaction.action.espresso.assertions.EspressoViewAssertion;
import org.mate.commons.interaction.action.espresso.executables.InstrumentationTestExecutable;
import org.mate.commons.interaction.action.espresso.executables.compose.ComposeNodeExecutable;
import org.mate.commons.interaction.action.espresso.view_matchers.EspressoViewMatcher;
import org.mate.commons.utils.MATELog;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * An Interaction that uses the {@link UiDevice} class to find the UI elements on which to execute an action.
 */
public class UiDeviceInteraction extends InstrumentationTestInteraction {

    private enum BY_SELECTORS {
        BY_HAS_CHILD, BY_CONTENT_DESC
    }

    private BySelector matcher;
    private String matcherString;
    private BY_SELECTORS matcherId;
    private String lookUpString;

    public UiDeviceInteraction(EspressoViewMatcher viewMatcher) {
        super(InteractionType.UI_DEVICE_INTERACTION, viewMatcher);
    }

    public UiDeviceInteraction(Parcel in) {
        super(in, InteractionType.UI_DEVICE_INTERACTION);
        matcherString = in.readString();
        matcherId = BY_SELECTORS.valueOf(in.readString());
        lookUpString = in.readString();
        if (matcherId == BY_SELECTORS.BY_HAS_CHILD) {
            matcher = By.hasChild(By.text(lookUpString));
        } else {
            matcher = By.descContains(lookUpString);
        }
    }

    /**
     * Returns the UiObject2 that is represented by this interaction.
     * @return an UiObject2.
     */
    public UiObject2 getUiObject() {
        Instrumentation inst = getInstrumentation();
        UiDevice device = UiDevice.getInstance(inst);
        return device.findObject(matcher);
    }

    public void setParametersWithNodeButton(SemanticsNode aNode) {
        List<AnnotatedString> texts = aNode.getConfig().getOrElse(SemanticsProperties.INSTANCE.getText(), () -> null);
        String t = texts.get(0).getText();
        matcher = By.hasChild(By.text(t));
        matcherId = BY_SELECTORS.BY_HAS_CHILD;
        matcherString = "By.hasChild(By.text(\"" + t + "\"))";
        lookUpString = t;
    }

    public void setParameterWithContentDescriptionNode(SemanticsNode aNode) {
        List<String> texts = aNode.getConfig().get(SemanticsProperties.INSTANCE.getContentDescription());
        matcher = By.desc(texts.get(0));
        matcherId = BY_SELECTORS.BY_CONTENT_DESC;
        matcherString = "By.desc(\"" + texts.get(0) + "\")";
        lookUpString = texts.get(0);
    }

    @Override
    public void perform(InstrumentationTestExecutable action) {
        if (!(action instanceof ComposeNodeExecutable)) {
            MATELog.log_warn("IllegalArgument: Trying to execute an action with " +
                    "type " + action.getType() + " and class " + action.getClass() +
                    " using a UiDeviceInteraction.");
            throw new IllegalArgumentException("UiDeviceInteraction can only perform " +
                    "ComposeNodeExecutable actions.");
        }

        UiObject2 interactiveUiObject = getUiObject();
        ((ComposeNodeExecutable) action).performActionForNode(interactiveUiObject);
    }

    @Override
    public void check(EspressoViewAssertion assertion) {
        throw new UnsupportedOperationException("UiDeviceInteraction does not support check()");
    }

    @Override
    public String getCode() {
        return String.format("UiDevice.getInstance(getInstrumentation()).findObject(%s)", matcherString);
    }

    @Override
    public Set<String> getNeededClassImports() {
        HashSet<String> imports = new HashSet<>();
        imports.add("androidx.test.espresso.ViewAction");
        imports.add("androidx.test.espresso.ViewInteraction");
        imports.add("androidx.test.uiautomator.By");
        imports.add("androidx.test.uiautomator.UiDevice");
        imports.add("androidx.test.uiautomator.UiObject2");
        return imports;
    }

    @Override
    public Set<String> getNeededStaticImports() {
        HashSet<String> imports = new HashSet<>();
        imports.add("androidx.test.platform.app.InstrumentationRegistry.getInstrumentation");
        return imports;
    }

    @Override
    public EspressoView getTargetEspressoView(EspressoView baseEspressoView) {
        return baseEspressoView;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.matcherString);
        dest.writeString(this.matcherId.name());
        dest.writeString(this.lookUpString);
    }

    public static final Creator<UiDeviceInteraction> CREATOR = new Creator<UiDeviceInteraction>() {
        @Override
        public UiDeviceInteraction createFromParcel(Parcel source) {
            // We need to use the InstrumentationTestInteraction.CREATOR here, because we want to make sure
            // to remove the EspressoViewInteraction's type integer from the beginning of Parcel and
            // call the appropriate constructor for this action.
            // Otherwise, the first integer will be read as data for an instance variable.
            return (UiDeviceInteraction) InstrumentationTestInteraction.CREATOR.createFromParcel(source);
        }

        @Override
        public UiDeviceInteraction[] newArray(int size) {
            return new UiDeviceInteraction[size];
        }
    };
}
