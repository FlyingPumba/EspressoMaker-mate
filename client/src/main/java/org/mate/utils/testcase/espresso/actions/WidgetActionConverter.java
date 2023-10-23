package org.mate.utils.testcase.espresso.actions;

import org.mate.Registry;
import org.mate.commons.interaction.action.ui.ActionType;
import org.mate.commons.interaction.action.ui.Widget;
import org.mate.commons.interaction.action.ui.WidgetAction;
import org.mate.utils.testcase.espresso.EspressoDependency;

import java.util.EnumSet;

import static org.mate.utils.testcase.espresso.EspressoDependency.ALL_OF;
import static org.mate.utils.testcase.espresso.EspressoDependency.ANY_OF;
import static org.mate.utils.testcase.espresso.EspressoDependency.CLEAR_TEXT;
import static org.mate.utils.testcase.espresso.EspressoDependency.CLOSE_SOFT_KEYBOARD;
import static org.mate.utils.testcase.espresso.EspressoDependency.SCROLL_TO;
import static org.mate.utils.testcase.espresso.EspressoDependency.WITH_CONTENT_DESCRIPTION;
import static org.mate.utils.testcase.espresso.EspressoDependency.WITH_RESOURCE_NAME;
import static org.mate.utils.testcase.espresso.EspressoDependency.WITH_TEXT;
import static org.mate.utils.testcase.espresso.EspressoDependency.WITH_CLASS_NAME;
import static org.mate.utils.testcase.espresso.EspressoDependency.EQUAL_TO;

/**
 * Converts a {@link WidgetAction} to an espresso action.
 */
public class WidgetActionConverter extends ActionConverter {

    /**
     * The widget on which an action should be applied.
     */
    private final Widget widget;

    /**
     * The type of action that should be applied.
     */
    private final ActionType actionType;

    /**
     * Stores the number of applicable view matchers.
     */
    private final int numberOfUsableViewMatchers;

    /**
     * Constructs a converter for the given widget action.
     *
     * @param action The widget action that should be converted to an espresso action.
     */
    public WidgetActionConverter(WidgetAction action) {
        super(action);
        this.widget = action.getWidget();
        this.actionType = action.getActionType();
        this.numberOfUsableViewMatchers = getNumberOfUsableViewMatchers();
    }

    /**
     * Returns a short form of the resource id.
     *
     * @return Returns a short form of the resource id or {@code null} if the resource id doesn't
     *         start with the package name or isn't valid.
     */
    private String getShortResourceName() {

        // the resource id might look as follows: android:id/statusBarBackground
        if (!widget.getResourceID().startsWith(Registry.getPackageName())) {
            return null;
        }

        // or it might look as follows: com.zola.bmi:id/resultLabel
        String[] tokens = widget.getResourceID().split("/");
        if (tokens.length > 1) {
            return tokens[1];
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void buildComment() {
        super.buildComment();
        builder.append(actionType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void buildPerform() {

        builder.append(".perform(");

        EnumSet<ActionType> textActions = EnumSet.of(ActionType.CLEAR_TEXT,
                ActionType.TYPE_SPECIFIC_TEXT, ActionType.TYPE_TEXT);

        if (textActions.contains(actionType)) {
            // we need to close the soft keyboard first
            builder.append(CLOSE_SOFT_KEYBOARD).append("()").append(", ");
        }

        if (widget.isSonOfScrollView()) {
            builder.append(SCROLL_TO).append("()").append(", ");
        }

        // remove any text before we insert some new text
        if (actionType == ActionType.TYPE_TEXT || actionType == ActionType.TYPE_SPECIFIC_TEXT) {
            builder.append(CLEAR_TEXT).append("()").append(", ");
        }

        // insert the actual action
        buildAction();

        if (textActions.contains(actionType)) {
            // close the soft keyboard in case we inserted some new text
            builder.append(", ").append(CLOSE_SOFT_KEYBOARD).append("()");
        }

        // close 'perform()' clause
        builder.append(");");
    }

    /**
     * Builds the actual action together.
     */
    private void buildAction() {

        EspressoDependency dependency;

        switch (actionType) {
            case CLICK:
                dependency = EspressoDependency.CLICK;
                break;
            case LONG_CLICK:
                dependency = EspressoDependency.LONG_CLICK;
                break;
            case TYPE_TEXT:
            case TYPE_SPECIFIC_TEXT:
                dependency = EspressoDependency.TYPE_TEXT;
                break;
            case CLEAR_TEXT:
                dependency = CLEAR_TEXT;
                break;
            case SWIPE_LEFT:
                dependency = EspressoDependency.SWIPE_LEFT;
                break;
            case SWIPE_RIGHT:
                dependency = EspressoDependency.SWIPE_RIGHT;
                break;
            case SWIPE_DOWN:
                dependency = EspressoDependency.SWIPE_DOWN;
                break;
            case SWIPE_UP:
                dependency = EspressoDependency.SWIPE_UP;
                break;
            default:
                throw new UnsupportedOperationException("Action type " + actionType
                        + " not yet supported");
        }

        builder.append(dependency).append("(");

        // the text actions requires the text to insert as additional argument
        if (dependency == EspressoDependency.TYPE_TEXT) {
            builder.append(boxString(widget.getText()));
        }

        builder.append(")");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void buildViewMatcher() {

        final String resourceName = getShortResourceName();

        // the resource id is not necessarily present for all widgets
        if (resourceName != null) {
            buildWithResourceName(resourceName);
        } else {

            if (isNonEmptyTextView()) {
                // try first a view matcher solely based on the widget text
                buildAnyOf();
            }

            // use an 'allOf()' view matcher if there are multiple matchers applicable
            if (numberOfUsableViewMatchers > 1) {
                buildAllOf();
            }

            if (isNonEmptyTextView()) {
                // close the anyOf() matcher
                builder.append(")");
            }
        }
    }

    /**
     * Determines how many view matchers can be applied.
     *
     * @return Returns the number of applicable view matchers.
     */
    private int getNumberOfUsableViewMatchers() {
        int numberOfUsableViewMatchers = 1; // there is at least one matcher applicable

        if (getShortClassName() != null) {
            numberOfUsableViewMatchers++;
        }

        if (isNonEmptyTextView()) {
            numberOfUsableViewMatchers++;
        }

        return numberOfUsableViewMatchers;
    }

    /**
     * @param resourceName The non empty resource name.
     */
    private void buildWithResourceName(String resourceName) {
        builder.append(WITH_RESOURCE_NAME)
                .append("(").append(boxString(resourceName)).append(")");
    }

    /**
     * Builds a view matcher for a text view using the 'anyOf' matcher.
     */
    private void buildAnyOf() {
        builder.append(ANY_OF).append("(");
        buildTextMatcher(true);
    }

    /**
     * Builds a view matcher for a text view using the 'withText()/withContentDescription()' matcher.
     *
     * @param appendComma Whether to append a comma at the end.
     */
    private void buildTextMatcher(boolean appendComma) {
        boolean useContentDescription = !widget.getContentDesc().isEmpty();
        if (useContentDescription) {
            builder.append(WITH_CONTENT_DESCRIPTION)
                    .append("(").append(boxString(widget.getContentDesc())).append(")");
        } else {
            builder.append(WITH_TEXT)
                    .append("(").append(boxString(widget.getText())).append(")");
        }
        if (appendComma) {
            builder.append(", ");
        }
    }

    /**
     * Builds a view matcher using the 'allOf()' matcher, see the hamcrest API for more details.
     */
    private void buildAllOf() {
        builder.append(ALL_OF).append("(");
        // Coordinates matcher is disabled since it is not a default Espresso matcher.
        // buildCoordinatesMatcher();
        buildClassMatcher();
        if (isNonEmptyTextView()) {
            builder.append(", ");
            buildTextMatcher(false);
        }
        builder.append(")");
    }

    /**
     * Builds a view matcher based on the class name.
     */
    private void buildClassMatcher() {
        String className = getShortClassName();
        if (className != null) {
	    builder.append(String.format("%s(%s(%s))", WITH_CLASS_NAME, EQUAL_TO, boxString(className)));
        }
    }

    /**
     * Checks whether the widget represents a non-empty text view.
     *
     * @return Returns {@code true} if the widget is a non-empty text view.
     */
    private boolean isNonEmptyTextView() {
        return widget.isTextViewType()
                && ((widget.getText() != null && !widget.getText().isEmpty())
                || (!widget.getContentDesc().isEmpty()));
    }

    /**
     * Returns a short form of the original class name.
     *
     * @return Returns the short class name or {@code null} if the class name is not defined.
     */
    private String getShortClassName() {
        if (!widget.getClazz().isEmpty()) {
            // the class name looks as follows: android.widget.TextView
            String[] tokens = widget.getClazz().split("\\.");
            return tokens[tokens.length - 1];
        } else {
            return null;
        }
    }
}
