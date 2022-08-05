package org.blyznytsia.bpp;

import lombok.extern.slf4j.Slf4j;
import org.blyznytsia.annotation.Value;
import org.blyznytsia.context.ApplicationContext;
import org.blyznytsia.util.TypeResolver;

import java.io.IOException;
import java.util.Properties;

/**
 * Implementation of {@link BeanPostProcessor} interface that injects values into fields annotated
 * with {@link Value}
 */
@Slf4j
public class ValueAnnotationBeanPostProcessor implements BeanPostProcessor {

    private final Properties properties;

    public ValueAnnotationBeanPostProcessor() {
        this.properties = loadProperties("application.properties");
    }
    @Override
    public Object configure(Object bean, ApplicationContext context) {
        for (var field : bean.getClass().getDeclaredFields()) {

            if (field.isAnnotationPresent(Value.class)) {
                var annotation = field.getAnnotation(Value.class);
                Object value =
                        annotation.value().isBlank()
                                ? properties.get(field.getName())
                                : properties.get(annotation.value());

                try {
                    field.setAccessible(true);
                    field.set(bean, TypeResolver.parseToType((String) value, field.getType()));
                } catch (IllegalAccessException | NumberFormatException | ClassCastException e) {
                    throw new RuntimeException(
                            String.format(
                                    "Can't set value \"%s\" from properties to field \"%s\" in class %s",
                                    value, field.getName(), bean.getClass().getName()),
                            e);
                }
            }
        }
        return bean;
    }

    private Properties loadProperties(String resourceName) {
        var inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream(resourceName);
        if (inputStream == null) {
            throw new RuntimeException(
                    "Can't find properties file in resources with name application.properties");
        }

        Properties properties = new Properties();
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return properties;
    }
}