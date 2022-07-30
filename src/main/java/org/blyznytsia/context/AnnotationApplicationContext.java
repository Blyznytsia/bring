package org.blyznytsia.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.blyznytsia.exception.NoSuchBeanException;
import org.blyznytsia.exception.NoUniqueBeanException;
import org.blyznytsia.factory.ObjectFactory;
import org.blyznytsia.model.BeanDefinition;
import org.blyznytsia.scanner.BeanScanner;
import org.blyznytsia.scanner.ComponentAnnotationScanner;
import org.blyznytsia.scanner.ConfigurationAnnotationScanner;
import org.blyznytsia.util.DependencyGraph;

/**
 * Implementation of ApplicationContext. The main responsibilities of this class is to delegate
 * scanners to find all beans with annotation. After scanner scanned this package creates a
 * Collection of BeanDefinition.
 *
 * <p>Example:
 *
 * <pre class=code>
 *
 *     public class Main {
 *
 *         public static void main(String[] args) {
 *             AnnotationApplicationContext context = new AnnotationApplicationContext("org.example");
 *             SomeService service = context.getBean(SomeService.class);
 *         }
 *
 *     }
 * </pre>
 *
 * @see org.blyznytsia.context.ApplicationContext
 * @see org.blyznytsia.model.BeanDefinition
 * @see org.blyznytsia.scanner.BeanScanner
 */
@Slf4j
public class AnnotationApplicationContext implements ApplicationContext {
  private final Map<BeanDefinition, Object> cache = new HashMap<>();

  public AnnotationApplicationContext(String... packages) {
    var beanDefinitions = scan(packages);
    initializeContext(beanDefinitions);
  }

  private void initializeContext(List<BeanDefinition> beanDefinitions) {
    DependencyGraph dependencyGraph = new DependencyGraph(beanDefinitions);
    ObjectFactory objectFactory = new ObjectFactory(this);
    List<BeanDefinition> sorted = dependencyGraph.sort();
    sorted.forEach(definition -> cache.put(definition, objectFactory.createBean(definition)));
  }

  @Override
  public <T> T getBean(Class<T> beanType) throws NoSuchBeanException, NoUniqueBeanException {
    var list =
        cache.values().stream().filter(el -> beanType.isAssignableFrom(el.getClass())).toList();

    if (list.size() > 1) throw new NoUniqueBeanException(list.size());
    else if (list.isEmpty()) {
      throw new NoSuchBeanException(
          "Required a single bean, but 0 were found of type %s"
              .formatted(beanType.getSimpleName()));
    }

    return beanType.cast(list.get(0));
  }

  @Override
  public <T> T getBean(String name, Class<T> beanType) throws NoSuchBeanException {
    var key =
        cache.keySet().stream()
            .filter(el -> el.getName().equals(name))
            .findFirst()
            .orElseThrow(
                () ->
                    new NoSuchBeanException(
                        "Required a single bean, but 0 were found of type %s"
                            .formatted(beanType.getSimpleName())));

    return beanType.cast(cache.get(key));
  }

  @Override
  public <T> Map<BeanDefinition, T> getAllBeans(Class<T> beanType) {
    return cache.entrySet().stream()
        .filter(entry -> beanType.isAssignableFrom(entry.getValue().getClass()))
        .collect(Collectors.toMap(Map.Entry::getKey, entry -> beanType.cast(entry.getValue())));
  }

  @SneakyThrows
  private List<BeanDefinition> scan(String[] packages) {
    BeanScanner configScanner = new ConfigurationAnnotationScanner(packages);
    List<BeanDefinition> configDefinitions = configScanner.scan();

    var configDefinitionMap =
        configDefinitions.stream()
            .collect(Collectors.toMap(BeanDefinition::getType, Function.identity()));

    BeanScanner componentAnnotationScanner =
        new ComponentAnnotationScanner(configDefinitionMap, packages);
    List<BeanDefinition> componentDefinitions = componentAnnotationScanner.scan();

    List<BeanDefinition> definitions = new ArrayList<>();
    definitions.addAll(configDefinitions);
    definitions.addAll(componentDefinitions);

    return definitions;
  }

  public boolean contains(BeanDefinition definition) {
    return cache.containsKey(definition);
  }
}
