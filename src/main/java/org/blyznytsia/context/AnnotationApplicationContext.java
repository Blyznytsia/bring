package org.blyznytsia.context;

import org.blyznytsia.exception.NotImplementedException;
import org.blyznytsia.injector.Injector;
import org.blyznytsia.model.BeanDefinition;
import org.blyznytsia.scanner.BeanScanner;
import org.reflections.Reflections;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
 * @see org.blyznytsia.injector.Injector
 */

public class AnnotationApplicationContext implements ApplicationContext {

    private Injector injector;
    private List<BeanScanner> scanners;
    private Map<BeanDefinition, Object> container = new HashMap<>();
    private Reflections reflections;

    public AnnotationApplicationContext(String packageScan) {
    }

    @Override
    public <T> T getBean(Class<T> beanType) {
        throw new NotImplementedException("Need to implement this method");
    }

    @Override
    public <T> T getBean(String name, Class<T> beanType) {
        throw new NotImplementedException("Need to implement this method");
    }

    @Override
    public <T> Map<BeanDefinition, T> getAllBeans(Class<T> beanType) {
        throw new NotImplementedException("Need to implement this method");
    }
}
