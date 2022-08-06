package org.blyznytsia.validator;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Set;
import org.blyznytsia.exception.CircularDependencyException;
import org.blyznytsia.model.BeanDefinition;
import org.junit.jupiter.api.Test;

class DependencyValidatorTest {

  static final String SERVICE_1 = "service1";
  static final String SERVICE_2 = "service2";
  static final String SERVICE_3 = "service3";
  static final String SERVICE_4 = "service4";
  static final String SERVICE_5 = "service5";
  static final String EXCEPTION_MESSAGE =
      "There is a circular dependency between beans in the application context:\n";

  @Test
  void validate_shouldThrowExceptionWithMessage() {
    BeanDefinition bean1 =
        BeanDefinition.builder()
            .name(SERVICE_1)
            .fieldDependencies(Set.of(SERVICE_2))
            .type(Service1.class)
            .build();

    BeanDefinition bean2 =
        BeanDefinition.builder()
            .name(SERVICE_2)
            .fieldDependencies(Set.of(SERVICE_1))
            .type(Service2.class)
            .build();

    BeanDefinition bean4 =
        BeanDefinition.builder()
            .name(SERVICE_4)
            .fieldDependencies(Set.of(SERVICE_5))
            .type(Service4.class)
            .build();

    BeanDefinition bean5 =
        BeanDefinition.builder()
            .name(SERVICE_5)
            .fieldDependencies(Set.of(SERVICE_4))
            .type(Service5.class)
            .build();

    Set<BeanDefinition> beanDefinitions = Set.of(bean1, bean2, bean4, bean5);

    DependencyValidator dependencyValidator = new DependencyValidator();

    assertThatThrownBy(() -> dependencyValidator.validate(beanDefinitions))
        .isInstanceOf(CircularDependencyException.class)
        .hasMessage(
            EXCEPTION_MESSAGE
                + "0: service5->service4->service5\n"
                + "1: service2->service1->service2");
  }

  @Test
  void validate_shouldThrowExceptionWithMessage2() {
    BeanDefinition bean1 =
        BeanDefinition.builder()
            .name(SERVICE_1)
            .fieldDependencies(Set.of(SERVICE_2))
            .type(Service1.class)
            .build();

    BeanDefinition bean2 =
        BeanDefinition.builder()
            .name(SERVICE_2)
            .fieldDependencies(Set.of(SERVICE_3))
            .type(Service2.class)
            .build();

    BeanDefinition bean3 =
        BeanDefinition.builder()
            .name(SERVICE_3)
            .fieldDependencies(Set.of(SERVICE_1))
            .type(Service3.class)
            .build();

    Set<BeanDefinition> beanDefinitions = Set.of(bean1, bean2, bean3);

    DependencyValidator dependencyValidator = new DependencyValidator();

    assertThatThrownBy(() -> dependencyValidator.validate(beanDefinitions))
        .isInstanceOf(CircularDependencyException.class)
        .hasMessage(EXCEPTION_MESSAGE + "0: service2->service3->service1->service2");
  }

  @Test
  void validate_validationPassed_noExceptionThrow() {

    BeanDefinition beanOne =
        BeanDefinition.builder()
            .name(SERVICE_1)
            .fieldDependencies(Set.of(SERVICE_2, SERVICE_3))
            .type(Service1.class)
            .build();

    BeanDefinition beanTwo =
        BeanDefinition.builder()
            .name(SERVICE_2)
            .fieldDependencies(Set.of(SERVICE_3))
            .type(Service2.class)
            .build();

    BeanDefinition beanThree =
        BeanDefinition.builder()
            .name(SERVICE_3)
            .fieldDependencies(Set.of())
            .type(Service3.class)
            .build();

    Set<BeanDefinition> beanDefinitions = Set.of(beanOne, beanTwo, beanThree);

    DependencyValidator dependencyValidator = new DependencyValidator();

    dependencyValidator.validate(beanDefinitions);
  }
}
