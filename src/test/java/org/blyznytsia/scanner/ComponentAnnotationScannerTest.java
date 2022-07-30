package org.blyznytsia.scanner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.HashMap;
import java.util.List;
import org.blyznytsia.model.BeanDefinition;
import org.blyznytsia.scanner.data.component_scanner.TestService1;
import org.blyznytsia.scanner.data.component_scanner.TestService3;
import org.junit.jupiter.api.Test;

class ComponentAnnotationScannerTest {

  static final String PACKAGE_NAME = "org.blyznytsia.scanner.data.component_scanner";
  static final String SERVICE1_NAME = "anotherNameService";
  static final String SERVICE2_NAME = "testService2";
  static final String SERVICE1_DEPENDENCY_NAME = TestService3.class.getName();
  static final String[] SERVICE2_DEPENDENCY_NAMES = {
    TestService1.class.getName(), TestService3.class.getName()
  };
  ComponentAnnotationScanner scanner =
      new ComponentAnnotationScanner(new HashMap<>(), PACKAGE_NAME);

  @Test
  void shouldReturnValidBeanDefinitions() {
    var beanDefinitions = scanner.scan();

    assertThat(beanDefinitions)
        .map(BeanDefinition::getName)
        .containsOnly(SERVICE2_NAME, SERVICE1_NAME);

    var service1 = getBeanDefinitionByName(beanDefinitions, SERVICE1_NAME);
    var service2 = getBeanDefinitionByName(beanDefinitions, SERVICE2_NAME);

    //    assertThat(service1.getDependsOnBeans()).containsOnly(SERVICE1_DEPENDENCY_NAME);
    //    assertThat(service2.getDependsOnBeans()).containsOnly(SERVICE2_DEPENDENCY_NAMES);
  }

  @Test
  void shouldThrowNPEIfPackageIsNull() {
    assertThatExceptionOfType(NullPointerException.class)
        .isThrownBy(() -> scanner.scan())
        .withMessage("packageName is marked non-null but is null");
  }

  BeanDefinition getBeanDefinitionByName(List<BeanDefinition> list, String name) {
    return list.stream().filter(el -> el.getName().equals(name)).findFirst().orElseThrow();
  }
}
