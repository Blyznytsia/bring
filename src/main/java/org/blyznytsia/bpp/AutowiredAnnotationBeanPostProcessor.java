package org.blyznytsia.bpp;

import lombok.extern.slf4j.Slf4j;
import org.blyznytsia.annotation.Autowired;
import org.blyznytsia.context.ApplicationContext;
import org.blyznytsia.exception.BeanConfigurationException;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

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
  public Object configure(Object bean, ApplicationContext context) {
    log.debug("Configuring bean of {} type", bean.getClass());
    try {
      for (var field : bean.getClass().getDeclaredFields()) {
        if (field.isAnnotationPresent(Autowired.class)) {
          field.setAccessible(true);
          field.set(bean, calculateValue(context, field));

          log.debug(
              "Successfully initialized {} field with the value of {} type",
              field.getName(),
              field.getType().getSimpleName());
        }
      }
    } catch (Exception e) {
      log.error("Exception during bean configuration: {}", e.getMessage());
      throw new BeanConfigurationException("Exception during bean configuration", e);
    }

    return bean;
  }

  private Object calculateValue(ApplicationContext context, Field field) {
    if (List.class.isAssignableFrom(field.getType()) && field.getGenericType() instanceof ParameterizedType) {
      var allBeans = context.getAllBeans(field.getType());
      return allBeans.values().stream().toList();
    } else {
      return context.getBean(field.getType());
    }
  }

}
