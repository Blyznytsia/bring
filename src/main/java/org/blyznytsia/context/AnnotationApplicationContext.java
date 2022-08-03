package org.blyznytsia.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.blyznytsia.exception.NoSuchBeanException;
import org.blyznytsia.exception.NoUniqueBeanException;
import org.blyznytsia.model.BeanDefinition;
import org.blyznytsia.processor.BeanFactoryProcessor;
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

  /** Packages scanner */
  private final Reflections reflections;
  /** Beans container */
  private final Map<String, Object> container = new HashMap<>();

  /**
   * Creates context
   *
   * <pre>
   * <b>Steps:</b>
   * 1. Initialize {@link Reflections}
   * 2. Initialize and run all implementations of {@link BeanScanner}
   * 3. Initialize {@link BeanFactoryProcessor} that creates beans
   * and put them into {@link AnnotationApplicationContext#container}
   * </pre>
   *
   * @param packageName package to scan
   */
  public AnnotationApplicationContext(String packageName) {
    this.reflections = new Reflections(packageName);
    var beanDefinitions = initAndRunScanners(packageName);
    validate(beanDefinitions);
    new BeanFactoryProcessor(this).initiateContext(beanDefinitions);
  }

  /**
   * Validate list of bean definition
   *
   * @param beanDefinitions list of bean definition
   */
  @SneakyThrows
  private void validate(List<BeanDefinition> beanDefinitions) {
    Set<Class<? extends BeanValidator>> validatorClasses =
        reflections.getSubTypesOf(BeanValidator.class);
    for (Class<? extends BeanValidator> validatorClass : validatorClasses) {
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
   * @return {@link List<BeanDefinition>} common list of scanners results
   */
  @SneakyThrows
  private List<BeanDefinition> initAndRunScanners(String packageName) {
    log.debug("Searching for scanners in {} package", packageName);
    var scannerClasses = reflections.getSubTypesOf(BeanScanner.class);
    log.debug("Found {} scanners in {} package", scannerClasses.size(), packageName);

    var list = new ArrayList<BeanDefinition>();
    for (var scannerClass : scannerClasses) {
      var beanScanner = scannerClass.getDeclaredConstructor().newInstance();
      list.addAll(beanScanner.scan(packageName));
    }

    return list;
  }
}
