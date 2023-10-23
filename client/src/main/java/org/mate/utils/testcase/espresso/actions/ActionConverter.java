package org.mate.utils.testcase.espresso.actions;

import org.mate.commons.interaction.action.Action;

import static org.mate.utils.testcase.espresso.EspressoDependency.ON_VIEW;

/**
 * Provides an abstract converter for an {@link Action}.
 */
public abstract class ActionConverter {

    /**
     * The action that should be converted.
     */
    protected final Action action;

    /**
     * The internal builder containing the final espresso action sequence.
     */
    protected final StringBuilder builder = new StringBuilder();

    /**
     * Constructs a converter for the given action.
     *
     * @param action The action that should be converted.
     */
    public ActionConverter(Action action) {
        this.action = action;
    }

    /**
     * Performs the conversion process.
     *
     * @return Returns the obtained espresso action.
     */
    public String convert() {
        openViewMatcher();
        buildViewMatcher();
        closeViewMatcher();
        buildPerform();
        buildComment();
        return builder.toString();
    }

    /**
     * Builds a comment.
     */
    protected void buildComment() {
        builder.append(" // ");
    }

    /**
     * Builds the view action.
     */
    protected abstract void buildPerform();

    /**
     * Closes the view matcher.
     */
    protected void closeViewMatcher() {
        builder.append(")");
    }

    /**
     * Builds the view matcher.
     */
    protected abstract void buildViewMatcher();

    /**
     * Opens the view matcher.
     */
    protected void openViewMatcher() {
        builder.append(ON_VIEW).append("(");
    }


    /**
     * Method to turn a normal String into its proper representation for using in a Java expression.
     * This includes escaping the appropriate characters and adding double quotes.
     * For example: "txt\n" => "\"txt\\n\""
     * @param str string to box
     * @return the boxed stirng
     */
    protected String boxString(String str) {
        return "\"" + escapeStringCharacters(str) + "\"";
    }

    /**
     * Escape special characters in string.
     * @param s the string to work on
     * @return the escaped string.
     */
    private String escapeStringCharacters(String s) {
        StringBuilder buffer = new StringBuilder();
        escapeStringCharacters(s.length(), s, buffer);
        return buffer.toString();
    }

    private void escapeStringCharacters(int length, String str, StringBuilder buffer) {
        escapeStringCharacters(length, str, "\"", buffer);
    }

    private StringBuilder escapeStringCharacters(int length,
                                                 String str,
                                                 String additionalChars,
                                                 StringBuilder buffer) {
        for (int idx = 0; idx < length; idx++) {
            char ch = str.charAt(idx);
            switch (ch) {
                case '\b':
                    buffer.append("\\b");
                    break;

                case '\t':
                    buffer.append("\\t");
                    break;

                case '\n':
                    buffer.append("\\n");
                    break;

                case '\f':
                    buffer.append("\\f");
                    break;

                case '\r':
                    buffer.append("\\r");
                    break;

                case '\\':
                    buffer.append("\\\\");
                    break;

                default:
                    if (additionalChars != null && additionalChars.indexOf(ch) > -1) {
                        buffer.append("\\").append(ch);
                    } else if (Character.isISOControl(ch)) {
                        String hexCode = Integer.toHexString(ch).toUpperCase();
                        buffer.append("\\u");
                        int paddingCount = 4 - hexCode.length();
                        while (paddingCount-- > 0) {
                            buffer.append(0);
                        }
                        buffer.append(hexCode);
                    } else {
                        buffer.append(ch);
                    }
            }
        }
        return buffer;
    }
}
