package org.blyznytsia.bpp;

import lombok.extern.slf4j.Slf4j;
import org.blyznytsia.annotation.Autowired;
import org.blyznytsia.context.AnnotationApplicationContext;
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
  public void configure(Object bean, AnnotationApplicationContext context) {
    try {
      for (var field : bean.getClass().getDeclaredFields()) {
        if (field.isAnnotationPresent(Autowired.class)) {
          log.debug("Configuring bean of {} type", bean.getClass());
          field.setAccessible(true);
          var value = context.getBean(field.getType());
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
}
