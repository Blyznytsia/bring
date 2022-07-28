package org.blyznytsia.bpp;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.blyznytsia.annotation.Autowired;
import org.blyznytsia.exception.BeanConfigurationException;
import org.blyznytsia.model.BeanDefinition;

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
  public void configure(Object bean, Map<BeanDefinition, Object> cache) {
    log.debug("Configuring bean of {} type", bean.getClass());
    try {
      for (var field : bean.getClass().getDeclaredFields()) {
        if (field.isAnnotationPresent(Autowired.class)) {
          field.setAccessible(true);
          var value =
              cache.values().stream()
                  .filter(v -> field.getType().equals(v.getClass()))
                  .findFirst()
                  .get();
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
