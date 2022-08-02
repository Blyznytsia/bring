package org.blyznytsia.dependency;

import java.util.List;
import org.blyznytsia.context.DependencyTreeOrder;
import org.blyznytsia.model.BeanDefinition;
import org.junit.jupiter.api.Test;

class DependencyTreeOrderTest {

  static final String SERVICE_1 = "service1";
  static final String SERVICE_2 = "service2";
  static final String SERVICE_3 = "service3";
  static final String SERVICE_4 = "service4";
  static final String SERVICE_5 = "service5";
  static final String EXCEPTION_MESSAGE =
      "There is a circular dependency between beans in the application context:\n";

  @Test
  void test1() {
    BeanDefinition bean1 =
        BeanDefinition.builder()
            .name(SERVICE_1)
            .dependsOnBeans(List.of(SERVICE_2))
            .type(Service1.class)
            .build();

    BeanDefinition bean2 =
        BeanDefinition.builder()
            .name(SERVICE_2)
            .dependsOnBeans(List.of(SERVICE_4))
            .type(Service2.class)
            .build();

    BeanDefinition bean4 =
        BeanDefinition.builder()
            .name(SERVICE_4)
            .dependsOnBeans(List.of(SERVICE_5))
            .type(Service4.class)
            .build();

    BeanDefinition bean5 =
        BeanDefinition.builder()
            .name(SERVICE_5)
            .dependsOnBeans(List.of())
            .type(Service5.class)
            .build();

    List<BeanDefinition> beanDefinitions = List.of(bean1, bean2, bean4, bean5);

    DependencyTreeOrder dependencyTreeOrder = new DependencyTreeOrder(beanDefinitions);

    List<String> orderBeanForCreation = dependencyTreeOrder.buildSortedBeans();

    System.out.println(orderBeanForCreation);
  }

  @Test
  void test2() {
    BeanDefinition bean1 =
        BeanDefinition.builder()
            .name(SERVICE_1)
            .dependsOnBeans(List.of(SERVICE_2, SERVICE_3))
            .type(Service1.class)
            .build();

    BeanDefinition bean2 =
        BeanDefinition.builder()
            .name(SERVICE_2)
            .dependsOnBeans(List.of(SERVICE_3))
            .type(Service2.class)
            .build();

    BeanDefinition bean3 =
        BeanDefinition.builder()
            .name(SERVICE_3)
            .dependsOnBeans(List.of(SERVICE_4, SERVICE_5))
            .type(Service3.class)
            .build();

    BeanDefinition bean4 =
        BeanDefinition.builder()
            .name(SERVICE_4)
            .dependsOnBeans(List.of(SERVICE_5))
            .type(Service4.class)
            .build();

    BeanDefinition bean5 =
        BeanDefinition.builder()
            .name(SERVICE_5)
            .dependsOnBeans(List.of())
            .type(Service5.class)
            .build();

    List<BeanDefinition> beanDefinitions = List.of(bean1, bean2, bean3, bean4, bean5);

    DependencyTreeOrder dependencyTreeOrder = new DependencyTreeOrder(beanDefinitions);

    List<String> orderBeanForCreation = dependencyTreeOrder.buildSortedBeans();

    System.out.println(orderBeanForCreation);
  }
}
