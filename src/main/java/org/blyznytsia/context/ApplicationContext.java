package org.blyznytsia.context;

import java.util.Map;
import org.blyznytsia.exception.NoSuchBeanException;
import org.blyznytsia.exception.NoUniqueBeanException;
import org.blyznytsia.model.BeanDefinition;
import org.reflections.Reflections;

/**
 * Central interface to provide access and retrieve beans object. Similar as BeanFactory in Spring
 * framework
 */
public interface ApplicationContext {

  /**
   * Retrieves a bean by type from the context.
   *
   * @param beanType class of the target bean
   * @param <T> type of the target bean
   * @return bean of type T fetched from the context
   * @throws org.blyznytsia.exception.NoSuchBeanException if no bean found for the target type
   * @throws org.blyznytsia.exception.NoUniqueBeanException if more than one bean with provided type
   *     is found
   */
  <T> T getBean(Class<T> beanType) throws NoSuchBeanException, NoUniqueBeanException;

  /**
   * Retrieves a bean by its name and type.
   *
   * @param name name of the target bean
   * @param beanType type of the target bean
   * @param <T> type of the target bean
   * @return bean of type T fetched from the context
   * @throws org.blyznytsia.exception.NoSuchBeanException if no bean found for the target type
   */
  <T> T getBean(String name, Class<T> beanType) throws NoSuchBeanException;

  /**
   * Returns a {@link Map} with all the beans of the target type where bean's name is a key and
   * bean's instance is a value.
   *
   * @param <T> type of the target bean
   * @param beanType type of the target bean
   * @return a {@link Map} of all beans with the provided type. If no beans are found returns empty
   *     {@link Map}
   */
  <T> Map<BeanDefinition, T> getAllBeans(Class<T> beanType);

  Reflections getReflections();

  Map<BeanDefinition, Object> getContainer();
}
