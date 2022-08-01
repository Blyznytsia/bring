package org.blyznytsia.processor;

import lombok.extern.slf4j.Slf4j;
import org.blyznytsia.annotation.Autowired;
import org.blyznytsia.context.ApplicationContext;
import org.blyznytsia.model.BeanDefinition;

@Slf4j
public class AutowiredAnnotationBeanPostProcessor implements BeanPostProcessor {

  @Override
  public Object configure(Object bean, BeanDefinition beanDefinition, ApplicationContext context) {
    log.debug("Configuring bean of {} type", beanDefinition.getType());
    try {
      for (var field : bean.getClass().getDeclaredFields()) {
        if (field.isAnnotationPresent(Autowired.class)) {
          field.setAccessible(true);
          var value = context.getBean(field.getType());
          field.set(bean, value);

          log.trace(
              "Successfully initialized {} field with the value of {} type",
              field.getName(),
              field.getType().getSimpleName());
        }
      }
    } catch (Exception e) {
      log.error("Exception during bean configuration: {}", e.getMessage());
    }

    return bean;
  }
}
