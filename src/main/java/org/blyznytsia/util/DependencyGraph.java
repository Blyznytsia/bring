package org.blyznytsia.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.blyznytsia.model.BeanDefinition;

public class DependencyGraph {
  private final Map<BeanDefinition, List<BeanDefinition>> adjacencyMap;

  public DependencyGraph(List<BeanDefinition> definitions) {
    this.adjacencyMap = buildAdjacencyMap(definitions);
  }

  private Map<BeanDefinition, List<BeanDefinition>> buildAdjacencyMap(
      List<BeanDefinition> definitions) {
    Map<BeanDefinition, List<BeanDefinition>> map = new HashMap<>();

    for (BeanDefinition definition : definitions) {
      List<BeanDefinition> deps = new ArrayList<>(definition.getRequiredDependencies());
      deps.addAll(definition.getFieldDependencies());
      map.put(definition, deps);
    }

    return map;
  }

  /**
   * Runs topological sort on adjacency map
   *
   * @return bean definitions sorted according to dependencies they require to be created as
   *     prerequisite for their creation
   */
  public List<BeanDefinition> sort() {
    List<BeanDefinition> sorted = new ArrayList<>();
    Set<BeanDefinition> visited = new HashSet<>();

    while (sorted.size() != adjacencyMap.size()) {
      for (var entry : adjacencyMap.entrySet()) {
        BeanDefinition node = entry.getKey();
        List<BeanDefinition> children = entry.getValue();

        if (!visited.contains(node)) {
          visited.add(node);
          checkChildren(children, node, visited, sorted);
        }
      }
    }

    return sorted;
  }

  private void checkChildren(
      List<BeanDefinition> tree,
      BeanDefinition head,
      Set<BeanDefinition> visited,
      List<BeanDefinition> sorted) {

    visited.add(head);
    if (!sorted.contains(head)) {
      for (BeanDefinition node : tree) {
        if (adjacencyMap.containsKey(node)) {
          checkChildren(adjacencyMap.get(node), node, visited, sorted);
        }
      }
      sorted.add(head);
    }
  }
}
