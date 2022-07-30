package org.blyznytsia.bpp;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.blyznytsia.annotation.Autowired;
import org.blyznytsia.context.ApplicationContext;
import org.blyznytsia.exception.BeanConfigurationException;

/**
 * Implementation of {@link BeanPostProcessor} interface that injects beans into fields annotated
 * with {@link org.blyznytsia.annotation.Autowired}
 */
@Slf4j
public class AutowiredAnnotationBeanPostProcessor implements BeanPostProcessor {

  /**
   * Injects beans into fields annotated with {@link org.blyznytsia.annotation.Autowired}
   *
   * @param bean bean to be configured
   * @param context application context to fetch beans from for injections
   */
  @Override
  public void configure(Object bean, ApplicationContext context) {
    try {
      for (var field : bean.getClass().getDeclaredFields()) {
        if (field.isAnnotationPresent(Autowired.class)) {
          log.debug("Configuring bean of {} type", bean.getClass());
          field.setAccessible(true);
          Object value = resolveValue(context, field);
          field.set(bean, value);

          log.trace(
              "Successfully initialized {} field with the value of {} type",
              field.getName(),
              value.getClass().getSimpleName());
        }
      }
    } catch (Exception e) {
      log.error("Exception during bean configuration: {}", e.getMessage());
      throw new BeanConfigurationException("Exception during bean configuration", e);
    }
  }

  private Object resolveValue(ApplicationContext context, Field field) {
    Class<?> fieldType = field.getType();

    if (List.class.isAssignableFrom(fieldType)) {
      return handleParameterizedListType(field, context);
    } else {
      return context.getBean(fieldType);
    }
  }

  private List<?> handleParameterizedListType(Field field, ApplicationContext context) {
    if (field.getGenericType() instanceof ParameterizedType parameterizedType) {
      var actualTypeArgument = (Class<?>) parameterizedType.getActualTypeArguments()[0];
      return context.getAllBeans(actualTypeArgument).values().stream().toList();
    }
    return Collections.emptyList();
  }
}
