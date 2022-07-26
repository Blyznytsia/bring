package org.blyznytsia.validator;

import org.blyznytsia.exception.CircularDependencyException;
import org.blyznytsia.model.BeanDefinition;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * The main purpose of this class is to find the circular dependency between beans.
 * When problem occurs than message with information about chain of beans is printed.
 * If circular dependency doesn't exist then return empty set.
 */
public class DependencyValidator {

    /**
     * Map of beans. Key is a bean, and value is a list of beans in which the key bean depends.
     */
    private final Map<String, List<String>> beans;

    private DependencyValidator(Map<String, List<String>> beans) {
        this.beans = beans;
    }

    public static DependencyValidator buildDependencyGraph(List<BeanDefinition> beanDefinitions) {

        Map<String, List<String>> beanDeps = beanDefinitions
            .stream()
            .collect(Collectors.toMap(
                BeanDefinition::getName,
                BeanDefinition::getDependsOnBeans)
            );

        return new DependencyValidator(beanDeps);
    }

    /**
     * Calculate cycles between beans.
     *
     * @return empty set or {@link CircularDependencyException}
     */
    public Set<String> calculateCycles() {
        Set<String> visited = new HashSet<>();
        return beans.keySet().stream()
            .map(bean -> calculateCycles(bean, visited, Collections.emptyList()))
            .flatMap(Set::stream)
            .collect(Collectors.toSet());
    }


    /**
     * Recursive method to calculate circular dependency
     *
     * @param bean is a bean name.
     * @param visited is a set of beans that is checked and added to collection
     * @param path is a list of beans that is added to path
     * @return empty set or {@link CircularDependencyException}
     */
    private Set<String> calculateCycles(String bean, Set<String> visited, List<String> path) {

        List<String> newPath = new LinkedList<>(path);
        newPath.add(bean);

        if (path.contains(bean)) {
            List<String> beanNames = newPath.subList(newPath.indexOf(bean), newPath.size());

            String chainOfBeans = String.join("->", beanNames);

            throw new CircularDependencyException(chainOfBeans);
        }

        if (visited.contains(bean)) {
            return Collections.emptySet();
        }

        visited.add(bean);

        List<String> dependsOnBeans = beans.get(bean);

        return dependsOnBeans.stream()
            .map(dep -> calculateCycles(dep, visited, newPath))
            .flatMap(Set::stream)
            .collect(Collectors.toSet());
    }

}

