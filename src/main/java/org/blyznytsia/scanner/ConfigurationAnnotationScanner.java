package org.blyznytsia.scanner;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.blyznytsia.annotation.Bean;
import org.blyznytsia.annotation.Configuration;
import org.blyznytsia.model.BeanDefinition;
import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Classpath scanner responsible for finding Java configuration classes with beans declared in these classes
 * and building {@link BeanDefinition} out of them.
 */
@Slf4j
public class ConfigurationAnnotationScanner implements BeanScanner {

    /**
     * Scans configuration classes annotated with {@link org.blyznytsia.annotation.Component},
     * identifies methods annotated with {@link Bean} within those classes and builds {@link BeanDefinition} out of them.
     *
     * @param packageName package to be scanned
     * @return a list of created {@link BeanDefinition}
     */
    @Override
    public List<BeanDefinition> scan(@NonNull String packageName) {
        if (packageName.isBlank()) {
            throw new IllegalArgumentException("Package cannot be blank");
        }

        log.info("Scanning '{}' package for classes annotated wth @Configuration", packageName);
        var configurationsClasses = new Reflections(packageName).getTypesAnnotatedWith(Configuration.class);
        log.debug("Found configurations classes: {}", configurationsClasses);

        List<BeanDefinition> allDefinitions = new ArrayList<>();

        for (var configClass : configurationsClasses) {
            var configDefinition = BeanDefinition.builder()
                    .name(resolveBeanName(configClass))
                    .type(configClass)
                    .build();
            var declaredBeansDefinitions = findDefinitionsForDeclaredBeans(configClass);

            allDefinitions.add(configDefinition);
            allDefinitions.addAll(declaredBeansDefinitions);
        }
        log.debug("Scanning is finished. Found bean definitions: {}", allDefinitions);

        return allDefinitions;
    }

    private List<BeanDefinition> findDefinitionsForDeclaredBeans(Class<?> configClass) {
        return Arrays.stream(configClass.getDeclaredMethods())
                .filter(m -> m.isAnnotationPresent(Bean.class))
                .map(m -> createDefinition(configClass, m))
                .toList();
    }

    private BeanDefinition createDefinition(Class<?> configClass, Method method) {
        return BeanDefinition.builder()
                .configClass(configClass)
                .configClassDependency(true)
                .beanMethod(method)
                .name(resolveBeanNameFromMethod(method))
                .type(method.getReturnType())
                .initMethod(method.getAnnotation(Bean.class).initMethod())
                .dependsOnBeans(findDependencies(method))
                .build();

    }

    //TODO: move methods for extracting information for BeanDefinition fields to BeanDefinition or helper class.
    // TODO: revisit type for List<String> dependencies ... should be map
    private List<String> findDependencies(Method method) {
        return Arrays.stream(method.getParameterTypes()).map(Class::getName).toList();
    }

    private String resolveBeanNameFromMethod(Method beanMethod) {
        Bean annotation = beanMethod.getAnnotation(Bean.class);
        String beanName = annotation.value();
        return beanName.isEmpty() ? beanMethod.getName() : beanName;
    }

    private String resolveBeanName(Class<?> type) {
        String className = type.getSimpleName();
        return Character.toLowerCase(className.charAt(0)) + className.substring(1);
    }
}
