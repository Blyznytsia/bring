package org.blyznytsia.validator;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.blyznytsia.exception.CircularDependencyException;
import org.blyznytsia.model.BeanDefinition;

/** Validator that checks beans for circular dependencies */
public class DependencyValidator implements BeanValidator {

  /** Map of beans. Key is a bean, and value is a list of beans in which the key bean depends. */
  private Map<String, Set<String>> beans;

  public DependencyValidator() {}

  /**
   * Validate list of bean definitions
   *
   * @param beanDefinitions list of bean definitions
   * @throws CircularDependencyException if circular dependency happens
   */
  public void validate(Set<BeanDefinition> beanDefinitions) {
    this.beans =
        beanDefinitions.stream()
            .collect(
                Collectors.toMap(BeanDefinition::getName, BeanDefinition::getFieldDependencies));
    validateBeans();
  }

  /**
   * Calculate cycles between beans.
   *
   * <p>when find cycles then throw {@link CircularDependencyException} with beans
   */
  private void validateBeans() {
    Set<String> visited = new HashSet<>();

    List<String> cycles =
        beans.keySet().stream()
            .map(bean -> findCycles(bean, visited, Collections.emptyList()))
            .filter(cycle -> !cycle.isEmpty())
            .map(cycle -> String.join("->", cycle))
            .toList();

    if (!cycles.isEmpty()) {
      throwException(cycles);
    }
  }

  private void throwException(List<String> cycles) {
    String chainOfCycles = buildChainOfBeanCycles(cycles);
    throw new CircularDependencyException(chainOfCycles);
  }

  private String buildChainOfBeanCycles(List<String> cycles) {
    StringBuilder chainOfCycles = new StringBuilder();
    for (int i = 0; i < cycles.size(); i++) {

      String cycle = cycles.get(i);
      chainOfCycles.append(i).append(": ").append(cycle);

      if (i < cycles.size() - 1) {
        chainOfCycles.append("\n");
      }
    }
    return chainOfCycles.toString();
  }

  /**
   * Recursive method to calculate circular dependency
   *
   * @param bean is a bean name.
   * @param visited is a set of beans that is checked and added to collection
   * @param path is a list of beans that is added to path
   * @return empty set or {@link CircularDependencyException}
   */
  private List<String> findCycles(String bean, Set<String> visited, List<String> path) {

    List<String> newPath = new LinkedList<>(path);
    newPath.add(bean);

    if (path.contains(bean)) {
      return newPath;
    }

    if (visited.contains(bean)) {
      return Collections.emptyList();
    }

    visited.add(bean);

    Set<String> dependsOnBeans = beans.get(bean);

    return dependsOnBeans.stream()
        .map(dep -> findCycles(dep, visited, newPath))
        .flatMap(List::stream)
        .toList();
  }
}
