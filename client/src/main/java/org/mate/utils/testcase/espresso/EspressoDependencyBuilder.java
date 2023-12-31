package org.mate.utils.testcase.espresso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Keeps track of the required espresso dependencies.
 */
public class EspressoDependencyBuilder {

    /**
     * The singleton instance.
     */
    private static final EspressoDependencyBuilder INSTANCE = new EspressoDependencyBuilder();

    /**
     * The set of required espresso dependencies.
     */
    private final Set<EspressoDependency> dependencies = new HashSet<>();

    /**
     * The singleton pattern implies a private constructor.
     */
    private EspressoDependencyBuilder() {
    }

    /**
     * Gets the singleton instance.
     *
     * @return Returns the singleton instance.
     */
    public static EspressoDependencyBuilder getInstance() {
        EspressoDependencyBuilder instance = INSTANCE;
        instance.register(EspressoDependency.SYSTEM_CLOCK_SLEEP);

        return instance;
    }

    /**
     * Registers an espresso dependency.
     *
     * @param dependency The dependency to be registered.
     */
    public void register(EspressoDependency dependency) {
        dependencies.add(dependency);
    }

    /**
     * Resets the set of dependencies.
     */
    public void reset() {
        dependencies.clear();
        register(EspressoDependency.SYSTEM_CLOCK_SLEEP);
    }

    /**
     * Retrieves the espresso dependencies in an ordered fashion. Inserts new lines ({@code null})
     * to visually group similar imports together.
     *
     * @return Returns the ordered espresso dependencies.
     */
    public List<EspressoDependency> getOrderedDependencies() {

        List<EspressoDependency> dependencies = new ArrayList<>(this.dependencies);
        Collections.sort(dependencies, new DependencyComparator());

        List<EspressoDependency> orderedDependencies = new ArrayList<>();

        String prevPrefix = null;
        boolean prevStatic = false;

        for (int i = 0; i < dependencies.size(); i++) {

            if (requiresEmptyLine(dependencies.get(i), prevPrefix, prevStatic)) {
                orderedDependencies.add(null);
            }

            orderedDependencies.add(dependencies.get(i));

            prevPrefix = dependencies.get(i).getFullQualifiedName().split("\\.")[0];
            prevStatic = dependencies.get(i).isStaticDependency();
        }

        return orderedDependencies;
    }

    /**
     * Determines whether an empty line should be inserted before the next dependency. This is
     * necessary when the next dependency doesn't start with the same prefix as the previous
     * dependency, or when the non-static imports start.
     *
     * @param dependency The next dependency.
     * @param prevPrefix The prefix of the previous dependency.
     * @param prevStatic Whether the previous dependency was static or not.
     * @return Returns {@code true} if an empty line should be inserted, otherwise {code false} is
     *         returned.
     */
    private boolean requiresEmptyLine(EspressoDependency dependency, String prevPrefix, boolean prevStatic) {
        return (prevPrefix != null
                // insert a line break if the previous dependency doesn't start with the same prefix
                && !prevPrefix.equals(dependency.getFullQualifiedName().split("\\.")[0]))
                // between a static and a non-static dependency a line break is necessary
                || (prevStatic && !dependency.isStaticDependency());
    }

    /**
     * A comparator for espresso dependencies. Defines the order the dependencies are listed in
     * the import statements.
     */
    private static class DependencyComparator implements Comparator<EspressoDependency> {

        /**
         * Ranks two espresso dependencies. A static dependency comes first. If both dependencies
         * are either static or non-static, the dependencies are ranked based on the lexical order.
         *
         * @param first The first espresso dependency.
         * @param second The second espresso dependency.
         * @return Returns the ranking of the two dependencies.
         */
        @Override
        public int compare(EspressoDependency first, EspressoDependency second) {

            // static dependencies are ranked first
            if (first.isStaticDependency() != second.isStaticDependency()) {
                return first.isStaticDependency() ? -1 : 1;
            }

            // otherwise use the lexical order
            return first.getFullQualifiedName().compareTo(second.getFullQualifiedName());
        }
    }
}

