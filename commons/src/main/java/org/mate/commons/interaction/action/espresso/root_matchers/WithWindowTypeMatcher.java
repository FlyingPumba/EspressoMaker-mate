package org.mate.commons.interaction.action.espresso.root_matchers;

import static androidx.test.espresso.matcher.RootMatchers.hasWindowLayoutParams;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.test.espresso.Root;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.util.HashSet;
import java.util.Set;

/**
 * Implements a withWindowType Root matcher.
 */
public class WithWindowTypeMatcher extends EspressoRootMatcher implements Parcelable {

    private final int windowType;

    public WithWindowTypeMatcher(int windowType) {
        super(EspressoRootMatcherType.WITH_WINDOW_TYPE);
        this.windowType = windowType;
    }

    @Override
    public Matcher<Root> getRootMatcher() {
        return withWindowType(windowType);
    }

    @Override
    public String getCode() {
        return String.format("withWindowType(%d)", windowType);
    }

    @Override
    public Set<String> getNeededClassImports() {
        return new HashSet<>();
    }

    @Override
    public Set<String> getNeededStaticImports() {
        Set<String> imports = new HashSet<>();
        imports.add("org.mate.espresso.tests.TestUtils.withWindowType");
        return imports;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(windowType);
    }

    public WithWindowTypeMatcher(Parcel in) {
        this(in.readInt());
    }

    public static final Creator<WithWindowTypeMatcher> CREATOR = new Creator<WithWindowTypeMatcher>() {
        @Override
        public WithWindowTypeMatcher createFromParcel(Parcel source) {
            // We need to use the EspressoViewMatcher.CREATOR here, because we want to make sure
            // to remove the EspressoViewMatcher's type integer from the beginning of Parcel and
            // call the appropriate constructor for this action.
            // Otherwise, the first integer will be read as data for an instance variable.
            return (WithWindowTypeMatcher) EspressoRootMatcher.CREATOR.createFromParcel(source);
        }

        @Override
        public WithWindowTypeMatcher[] newArray(int size) {
            return new WithWindowTypeMatcher[size];
        }
    };

    public static Matcher<Root> withWindowType(int windowType) {
        return new WithWindowType(windowType);
    }

    static final class WithWindowType extends TypeSafeMatcher<Root> {

        private final int windowType;

        public WithWindowType(int windowType) {
            this.windowType = windowType;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("is dialog");
        }

        @Override
        public boolean matchesSafely(Root root) {
            if (!hasWindowLayoutParams().matches(root)) {
                return false;
            }

            return windowType == root.getWindowLayoutParams().get().type;
        }
    }
}
