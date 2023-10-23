package org.mate.commons.interaction.action.espresso.view_matchers.base;

import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;

import android.os.Parcel;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.espresso.matcher.ViewMatchers.Visibility;

import org.hamcrest.Matcher;
import org.mate.commons.interaction.action.espresso.view_matchers.EspressoViewMatcher;
import org.mate.commons.interaction.action.espresso.view_matchers.EspressoViewMatcherType;

import java.util.HashSet;
import java.util.Set;

/**
 * Implements an Espresso Matcher for targeting the views that have certain visibility.
 */
public class WithEffectiveVisibilityMatcher extends EspressoViewMatcher {

    /**
     * The visibility to match against.
     */
    private final Visibility visibility;

    public WithEffectiveVisibilityMatcher(String visibilityString) {
        this(mapStringToVisibility(visibilityString));
    }

    @NonNull
    private static Visibility mapStringToVisibility(String visibilityString) {
        // first, check if the visibility string is an integer
        try {
            int visibilityInt = Integer.parseInt(visibilityString);
            return Visibility.forViewVisibility(visibilityInt);
        } catch (NumberFormatException e) {
            // do nothing
        }

        // if not, check it against the known visibility strings
        switch (visibilityString) {
            case "visible":
                return Visibility.VISIBLE;
            case "invisible":
                return Visibility.INVISIBLE;
            case "gone":
                return Visibility.GONE;
            default:
                throw new IllegalArgumentException("Unknown visibility string: " + visibilityString);
        }
    }

    public WithEffectiveVisibilityMatcher(Visibility visibility) {
        super(EspressoViewMatcherType.WITH_EFFECTIVE_VISIBILITY);
        this.visibility = visibility;
    }

    @Override
    public String getCode() {
        String visibilityCode = "";
        switch (visibility) {
            case VISIBLE:
                visibilityCode = "Visibility.VISIBLE";
                break;
            case INVISIBLE:
                visibilityCode = "Visibility.INVISIBLE";
                break;
            case GONE:
                visibilityCode = "Visibility.GONE";
                break;
        }

        return String.format("withEffectiveVisibility(%s)", visibilityCode);
    }

    @Override
    public Matcher<View> getViewMatcher() {
        return withEffectiveVisibility(visibility);
    }

    @Override
    public Set<String> getNeededClassImports() {
        HashSet<String> imports = new HashSet<>();
        imports.add("androidx.test.espresso.matcher.ViewMatchers.Visibility");
        return imports;
    }

    @Override
    public Set<String> getNeededStaticImports() {
        HashSet<String> imports = new HashSet<>();
        imports.add("androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility");
        return imports;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.visibility == null ? -1 : this.visibility.ordinal());
    }

    public WithEffectiveVisibilityMatcher(Parcel source) {
        this(ViewMatchers.Visibility.values()[source.readInt()]);
    }

    public static final Creator<WithEffectiveVisibilityMatcher> CREATOR = new Creator<WithEffectiveVisibilityMatcher>() {
        @Override
        public WithEffectiveVisibilityMatcher createFromParcel(Parcel source) {
            // We need to use the EspressoViewMatcher.CREATOR here, because we want to make sure
            // to remove the EspressoViewMatcher's type integer from the beginning of Parcel and
            // call the appropriate constructor for this action.
            // Otherwise, the first integer will be read as data for an instance variable.
            return (WithEffectiveVisibilityMatcher) EspressoViewMatcher.CREATOR.createFromParcel(source);
        }

        @Override
        public WithEffectiveVisibilityMatcher[] newArray(int size) {
            return new WithEffectiveVisibilityMatcher[size];
        }
    };
}
