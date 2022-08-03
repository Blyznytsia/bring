package org.blyznytsia.bpp;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.blyznytsia.annotation.Autowired;
import org.blyznytsia.context.ApplicationContext;
import org.blyznytsia.exception.BeanConfigurationException;
import org.blyznytsia.util.Helper;

/**
 * Implementation of {@link BeanPostProcessor} interface that injects beans into fields annotated
 * with {@link Autowired}
 */
@Slf4j
public class AutowiredConstructorBeanPostProcessor implements BeanPostProcessor {

  /**
   * Injects beans into fields via constructor annotated with {@link Autowired}
   *
   * @param bean bean to be configured
   * @param context application context to fetch beans from for injections
   */
  @Override
  public Object configure(Object bean, ApplicationContext context) {
    log.debug("Configuring bean of {} type", bean.getClass());
    try {
      configureConstructor(bean, context);
    } catch (Exception e) {
      log.error("Exception during bean configuration: {}", e.getMessage());
      throw new BeanConfigurationException("Exception during bean configuration", e);
    }

    return bean;
  }

  private void configureConstructor(Object bean, ApplicationContext context)
      throws IllegalAccessException, NoSuchMethodException {
    var optionalConstructor = Helper.checkAndGetAutowiredConstructor(bean.getClass());
    if (optionalConstructor.isPresent()) {
      var fieldsMap =
          Arrays.stream(bean.getClass().getDeclaredFields())
              .collect(Collectors.toMap(el -> el.getType().getSimpleName(), Function.identity()));

      // go through the all arguments
      for (var argument : optionalConstructor.get().getParameterTypes()) {
        var name = argument.getSimpleName();
        var field = fieldsMap.get(name);
        field.setAccessible(true);
        var value = context.getBean(field.getType());
        field.set(bean, value);

        log.debug(
            "Successfully initialized {} field with the value of {} type",
            field.getName(),
            field.getType().getSimpleName());
      }
    }
  }
}
