package org.blyznytsia.context;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.blyznytsia.exception.NoSuchBeanException;
import org.blyznytsia.exception.NoUniqueBeanException;
import org.blyznytsia.factory.ObjectFactory;
import org.blyznytsia.model.BeanDefinition;
import org.blyznytsia.scanner.BeanScanner;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementation of ApplicationContext. The main responsibilities of this class is
 * to delegate scanners to find all beans with annotation. After scanner scanned this
 * package creates a Collection of BeanDefinition.
 *
 * <p>Example:</p>
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

    @Getter
    private final Reflections reflections;
    private final Map<BeanDefinition, Object> container = new HashMap();

    public AnnotationApplicationContext(String packageName) {
        this.reflections = new Reflections(packageName);
        var beanDefinitions = initAndRunScanners(packageName);
        new ObjectFactory(reflections, container)
                .createObjects(beanDefinitions);
    }

    @Override
    public <T> T getBean(Class<T> beanType) throws NoSuchBeanException, NoUniqueBeanException {
        var list = container.values().stream()
                .filter(el -> beanType.isAssignableFrom(el.getClass()))
                .toList();

        if (list.size() > 1) throw new NoUniqueBeanException(list.size());
        else if (list.isEmpty()) throw new NoSuchBeanException();

        return beanType.cast(list.get(0));
    }

    @Override
    public <T> T getBean(String name, Class<T> beanType) throws NoSuchBeanException {
        var key = container.keySet().stream()
                .filter(el -> el.getName().equals(name))
                .findFirst()
                .orElseThrow(NoSuchBeanException::new);

        return beanType.cast(container.get(key));
    }

    @Override
    public <T> Map<BeanDefinition, T> getAllBeans(Class<T> beanType) {
        return container.entrySet().stream()
                .filter(entry -> beanType.isAssignableFrom(entry.getValue().getClass()))
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> beanType.cast(entry.getValue())));
    }

    @SneakyThrows
    private List<BeanDefinition> initAndRunScanners(String packageName) {
        var scannerClasses = reflections.getSubTypesOf(BeanScanner.class);
        var list = new ArrayList<BeanDefinition>();

        for (var scannerClass : scannerClasses) {
            var beanScanner = scannerClass.getDeclaredConstructor().newInstance();
            list.addAll(beanScanner.scan(packageName));
        }

        return list;
    }
}
