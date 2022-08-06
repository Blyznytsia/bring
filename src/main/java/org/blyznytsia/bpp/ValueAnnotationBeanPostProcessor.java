package org.blyznytsia.bpp;

import java.io.IOException;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;
import org.blyznytsia.annotation.Value;
import org.blyznytsia.context.ApplicationContext;
import org.blyznytsia.exception.BeanConfigurationException;
import org.blyznytsia.util.TypeResolver;

/**
 * Implementation of {@link BeanPostProcessor} interface that injects values into fields annotated
 * with {@link Value}
 */
@Slf4j
public class ValueAnnotationBeanPostProcessor implements BeanPostProcessor {

  public static final String DEFAULT_APPLICATION_PROPERTIES = "application.properties";
  private final Properties properties;

  public ValueAnnotationBeanPostProcessor() {
    this.properties = loadProperties(DEFAULT_APPLICATION_PROPERTIES);
  }

  @Override
  public Object configure(Object bean, ApplicationContext context) {
    for (var field : bean.getClass().getDeclaredFields()) {
      if (field.isAnnotationPresent(Value.class)) {
        log.debug("Initializing '{}' field", field);
        try {
          var annotation = field.getAnnotation(Value.class);
          Object value =
              annotation.value().isBlank()
                  ? properties.get(field.getName())
                  : properties.get(annotation.value());

          field.setAccessible(true);
          Object typedValue = TypeResolver.parseToType((String) value, field.getType());
          field.set(bean, typedValue);

          log.debug(
              "Successfully initialized '{}' field with '{}' value from {}",
              field,
              typedValue,
              DEFAULT_APPLICATION_PROPERTIES);

        } catch (Exception e) {
          log.error("Exception during bean configuration: {}", e.getMessage());
          throw new BeanConfigurationException("Failed to inject %s property from", e);
        }
      }
    }
    return bean;
  }

  private Properties loadProperties(String resourceName) {
    var inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream(resourceName);
    if (inputStream == null) {
      throw new RuntimeException(
          "Failed to find '%s' properties file in resources".formatted(resourceName));
    }

    var props = new Properties();
    try {
      props.load(inputStream);
    } catch (IOException e) {
      throw new RuntimeException("Failed to load properties from %s".formatted(resourceName), e);
    }

    return props;
  }
}
