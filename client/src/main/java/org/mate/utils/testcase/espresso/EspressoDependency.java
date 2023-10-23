package org.mate.utils.testcase.espresso;

import androidx.annotation.NonNull;

/**
 * Describes the supported espresso dependencies.
 */
public enum EspressoDependency {

    /**
     * On view espresso dependency.
     */
    ON_VIEW(true, "androidx.test.espresso.Espresso", "onView"),

    /**
     * On data espresso dependency.
     */
    ON_DATA(true, "androidx.test.espresso.Espresso", "onData"),

    /**
     * Click espresso dependency.
     */
    CLICK(true, "androidx.test.espresso.action.ViewActions", "click"),

    /**
     * Long click espresso dependency.
     */
    LONG_CLICK(true, "androidx.test.espresso.action.ViewActions", "longClick"),

    /**
     * Swipe up espresso dependency.
     */
    SWIPE_UP(true, "androidx.test.espresso.action.ViewActions", "swipeUp"),

    /**
     * Swipe down espresso dependency.
     */
    SWIPE_DOWN(true, "androidx.test.espresso.action.ViewActions", "swipeDown"),

    /**
     * Swipe left espresso dependency.
     */
    SWIPE_LEFT(true, "androidx.test.espresso.action.ViewActions", "swipeLeft"),

    /**
     * Swipe right espresso dependency.
     */
    SWIPE_RIGHT(true, "androidx.test.espresso.action.ViewActions", "swipeRight"),

    /**
     * Clear text espresso dependency.
     */
    CLEAR_TEXT(true, "androidx.test.espresso.action.ViewActions", "clearText"),

    /**
     * Close soft keyboard espresso dependency.
     */
    CLOSE_SOFT_KEYBOARD(true, "androidx.test.espresso.action.ViewActions", "closeSoftKeyboard"),

    /**
     * Press back espresso dependency.
     */
    PRESS_BACK(true, "androidx.test.espresso.action.ViewActions", "pressBackUnconditionally"),

    /**
     * Press menu espresso dependency.
     */
    PRESS_MENU(true,"androidx.test.espresso.action.ViewActions",  "pressMenuKey"),

    /**
     * Press key espresso dependency.
     */
    PRESS_KEY(true, "androidx.test.espresso.action.ViewActions", "pressKey"),

    /**
     * Scroll to espresso dependency.
     */
    SCROLL_TO(true, "androidx.test.espresso.action.ViewActions", "scrollTo"),

    /**
     * Type text espresso dependency.
     */
    TYPE_TEXT(true, "androidx.test.espresso.action.ViewActions", "typeText"),

    /**
     * The 'is root' view matcher.
     */
    IS_ROOT(true, "androidx.test.espresso.matcher.ViewMatchers", "isRoot"),

    /**
     * The 'with text' view matcher.
     */
    WITH_TEXT(true, "androidx.test.espresso.matcher.ViewMatchers", "withText"),

    /**
     * The 'with content description' view matcher.
     */
    WITH_CONTENT_DESCRIPTION(true, "androidx.test.espresso.matcher.ViewMatchers", "withContentDescription"),

    /**
     * The 'with id' view matcher.
     */
    WITH_ID(true, "androidx.test.espresso.matcher.ViewMatchers", "withId"),

    WITH_CLASS_NAME(true, "androidx.test.espresso.matcher.ViewMatchers", "withClassName"),

    WITH_RESOURCE_NAME(true, "androidx.test.espresso.matcher.ViewMatchers", "withResourceName"),

    /**
     * The 'all of' view matcher.
     */
    ALL_OF(true, "org.hamcrest.CoreMatchers", "allOf"),

    /**
     * The 'any of' view matcher.
     */
    ANY_OF(true, "org.hamcrest.CoreMatchers", "anyOf"),

    /**
     * The 'contains string' view matcher.
     */
    CONTAINS_STRING(true, "org.hamcrest.CoreMatchers", "containsString"),

    EQUAL_TO(true, "org.hamcrest.CoreMatchers", "equalTo"),

    /**
     * '@LargeTest' annotation.
     */
    LARGE_TEST(false, "androidx.test.filters", "LargeTest"),

    /**
     * Activity test rule espresso dependency.
     */
    ACTIVITY_TEST_RULE(false, "androidx.test.rule", "ActivityTestRule"),

    /**
     * Android junit 4 espresso dependency.
     */
    ANDROID_JUNIT_4(false, "androidx.test.runner", "AndroidJUnit4"),

    /**
     * Key event espresso dependency.
     */
    KEY_EVENT(false, "android.view", "KeyEvent"),

    /**
     * Rule espresso dependency.
     */
    RULE(false, "org.junit", "Rule"),

    /**
     * '@Test' annotation.
     */
    TEST(false, "org.junit", "Test"),

    /**
     * '@RunWith(TestRunner)' annotation.
     */
    RUN_WITH(false, "org.junit.runner", "RunWith"),

    UI_DEVICE(false, "androidx.test.uiautomator", "UiDevice"),

    GET_INSTRUMENTATION(true, "androidx.test.platform.app.InstrumentationRegistry", "getInstrumentation"),

    SYSTEM_CLOCK_SLEEP(false, "android.os", "SystemClock");

    /**
     * Whether the dependency is a static dependency.
     */
    private final boolean staticDependency;

    /**
     * The package name of the dependency.
     */
    private final String packageName;

    /**
     * The class name of the dependency.
     */
    private final String className;

    /**
     * This builder keeps track of all used espresso dependencies. Whenever {@link #toString()} is
     * called, the builder records the dependency.
     */
    private static final EspressoDependencyBuilder builder = EspressoDependencyBuilder.getInstance();

    /**
     * Creates a new espresso dependency with the given name.
     *
     * @param staticDependency Whether the dependency is static or not.
     * @param packageName The package name of the dependency.
     * @param className The class name of the dependency.
     */
    EspressoDependency(boolean staticDependency, String packageName, String className) {
        this.staticDependency = staticDependency;
        this.packageName = packageName;
        this.className = className;
    }

    /**
     * Returns the full-qualified name (FQN) consisting of package and class name.
     *
     * @return Returns the FQN of the dependency.
     */
    public String getFullQualifiedName() {
        return packageName + "." + className;
    }

    /**
     * Returns a textual representation of the dependency in the form of the class name.
     * Note that calling this method registers the dependency.
     *
     * @return Returns the string representation of the espresso dependency.
     */
    @NonNull
    @Override
    public String toString() {
        builder.register(this);
        return className;
    }

    /**
     * Whether this dependency is a static dependency or not.
     *
     * @return Returns {@code true} if the dependency is a static dependency, otherwise
     *         {@code false} is returned.
     */
    public boolean isStaticDependency() {
        return staticDependency;
    }
}
