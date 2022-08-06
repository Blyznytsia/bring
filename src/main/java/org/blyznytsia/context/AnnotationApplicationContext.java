package org.blyznytsia.context;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.blyznytsia.exception.NoSuchBeanException;
import org.blyznytsia.exception.NoUniqueBeanException;
import org.blyznytsia.model.BeanDefinition;
import org.blyznytsia.scanner.BeanScanner;
import org.blyznytsia.validator.BeanValidator;
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
@Getter
public class AnnotationApplicationContext implements ApplicationContext {

  private static final String DEFAULT_SCANNERS_PACKAGE = "org.blyznytsia.scanner";
  /** Beans container */
  private final Map<String, Object> container = new ConcurrentHashMap<>();

  /**
   * Creates context
   *
   * <pre>
   * <b>Steps:</b>
   * 1. Initialize and run all implementations of {@link BeanScanner}
   * 2. Initialize {@link ObjectFactory} that creates beans
   * and put them into {@link AnnotationApplicationContext#container}
   * </pre>
   *
   * @param packageName package to scan
   */
  public AnnotationApplicationContext(String packageName) {
    var beanDefinitions = initAndRunScanners(packageName);
    validate(beanDefinitions);
    new ObjectFactory(this).initiateContext(beanDefinitions);
  }

  /**
   * Validate set of bean definition
   *
   * @param beanDefinitions set of bean definition
   */
  @SneakyThrows
  private void validate(Set<BeanDefinition> beanDefinitions) {
    Set<Class<? extends BeanValidator>> validatorClasses =
        new Reflections("org.blyznytsia.validator").getSubTypesOf(BeanValidator.class);
    for (var validatorClass : validatorClasses) {
      BeanValidator beanValidator = validatorClass.getDeclaredConstructor().newInstance();
      beanValidator.validate(beanDefinitions);
    }
  }

  /**
   * Retrieves a bean by its type
   *
   * @param beanType class of the target bean
   * @return Bean of specific type
   * @throws NoSuchBeanException if no bean is present in {@link
   *     AnnotationApplicationContext#container}
   * @throws NoUniqueBeanException if more than one bean is present in {@link
   *     AnnotationApplicationContext#container}
   */
  @Override
  public <T> T getBean(Class<T> beanType) throws NoSuchBeanException, NoUniqueBeanException {
    var list = container.values().stream().filter(beanType::isInstance).toList();

    if (list.size() > 1) throw new NoUniqueBeanException(list.size());
    else if (list.isEmpty()) throw new NoSuchBeanException();

    return beanType.cast(list.get(0));
  }

  /**
   * Retrieves a bean by its name and type
   *
   * @param name name of the target bean
   * @param beanType type of the target bean
   * @return Bean of specific type
   * @throws NoSuchBeanException if no bean is present in {@link
   *     AnnotationApplicationContext#container}
   */
  @Override
  public <T> T getBean(String name, Class<T> beanType) throws NoSuchBeanException {
    return Optional.ofNullable(beanType.cast(container.get(name)))
        .orElseThrow(NoSuchBeanException::new);
  }

  /**
   * Retrieves a {@link Map} with all the beans of the target type where bean's name is a key and
   * bean's instance is a value
   *
   * @param beanType type of the target bean
   * @return {@link Map} of all beans with the provided type. If no beans are found returns empty
   */
  @Override
  public <T> Map<String, T> getAllBeans(Class<T> beanType) {
    return container.entrySet().stream()
        .filter(entry -> beanType.isAssignableFrom(entry.getValue().getClass()))
        .collect(Collectors.toMap(Map.Entry::getKey, entry -> beanType.cast(entry.getValue())));
  }

  /**
   * Initializes and runs all implementations of {@link BeanScanner}
   *
   * @param packageName package to scan
   * @return {@link Set<BeanDefinition>} common set of scanners results
   */
  @SneakyThrows
  private Set<BeanDefinition> initAndRunScanners(String packageName) {
    log.debug("Searching for scanners in {} package", packageName);
    var scannerClasses = new Reflections(DEFAULT_SCANNERS_PACKAGE).getSubTypesOf(BeanScanner.class);
    log.debug("Found {} scanners in {} package", scannerClasses, packageName);

    var beanDefinitions = new HashSet<BeanDefinition>();
    for (var scannerClass : scannerClasses) {
      var beanScanner = scannerClass.getDeclaredConstructor().newInstance();
      beanDefinitions.addAll(beanScanner.scan(packageName));
    }

    return beanDefinitions;
  }
}
