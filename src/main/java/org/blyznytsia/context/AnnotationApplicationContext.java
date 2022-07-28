package org.blyznytsia.context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.blyznytsia.exception.NoSuchBeanException;
import org.blyznytsia.exception.NoUniqueBeanException;
import org.blyznytsia.factory.ObjectFactory;
import org.blyznytsia.model.BeanDefinition;
import org.blyznytsia.scanner.BeanScanner;
import org.reflections.Reflections;

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
  private final Map<BeanDefinition, Object> cache;
  private final List<BeanScanner> scanners;

  public AnnotationApplicationContext(String... packages) {
    this.scanners = initScanners();
    var beanDefinitions = scan(packages);
    this.cache = new ObjectFactory().createBeans(beanDefinitions);
  }

  @Override
  public <T> T getBean(Class<T> beanType) throws NoSuchBeanException, NoUniqueBeanException {
    var list =
        cache.values().stream().filter(el -> beanType.isAssignableFrom(el.getClass())).toList();

    if (list.size() > 1) throw new NoUniqueBeanException(list.size());
    else if (list.isEmpty()) throw new NoSuchBeanException();

    return beanType.cast(list.get(0));
  }

  @Override
  public <T> T getBean(String name, Class<T> beanType) throws NoSuchBeanException {
    var key =
        cache.keySet().stream()
            .filter(el -> el.getName().equals(name))
            .findFirst()
            .orElseThrow(NoSuchBeanException::new);

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
    return scanners.stream().flatMap(scanner -> scanner.scan(packages).stream()).toList();
  }

  @SneakyThrows
  private List<BeanScanner> initScanners() {
    var reflections = new Reflections("org.blyznytsia.scanner");
    var scannerClasses = reflections.getSubTypesOf(BeanScanner.class);

    List<BeanScanner> scanners = new ArrayList<>();
    for (var scannerClass : scannerClasses) {
      BeanScanner scanner = scannerClass.getConstructor().newInstance();
      scanners.add(scanner);
    }

    return scanners;
  }
}
