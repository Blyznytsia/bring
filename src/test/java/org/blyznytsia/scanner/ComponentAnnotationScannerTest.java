package org.blyznytsia.scanner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.Set;
import org.blyznytsia.model.BeanDefinition;
import org.junit.jupiter.api.Test;

class ComponentAnnotationScannerTest {

  private static final String PACKAGE_NAME = "org.blyznytsia.scanner.data.component_scanner";
  private static final String SERVICE1_NAME = "anotherNameService";
  private static final String SERVICE2_NAME = "testService2";
  private static final String SERVICE3_NAME = "testService3";
  private static final String SERVICE1_DEPENDENCY_NAME = "testService3";
  private static final String[] SERVICE2_DEPENDENCY_NAMES = {"anotherNameService", "testService3"};
  private final ComponentAnnotationScanner scanner = new ComponentAnnotationScanner();

  @Test
  void scan_givenValidPackage_shouldReturnValidBeanDefinitions() {
    var beanDefinitions = scanner.scan(PACKAGE_NAME);

    assertThat(beanDefinitions)
        .map(BeanDefinition::getName)
        .containsOnly(SERVICE2_NAME, SERVICE1_NAME, SERVICE3_NAME);

    var service1 = getBeanDefinitionByName(beanDefinitions, SERVICE1_NAME);
    var service2 = getBeanDefinitionByName(beanDefinitions, SERVICE2_NAME);
    var service3 = getBeanDefinitionByName(beanDefinitions, SERVICE3_NAME);

    assertThat(service1.getDependsOnBeans()).containsOnly(SERVICE1_DEPENDENCY_NAME);
    assertThat(service2.getDependsOnBeans()).containsOnly(SERVICE2_DEPENDENCY_NAMES);
    assertThat(service3.getDependsOnBeans()).isEmpty();
  }

  @Test
  void scan_givenNullPackage_shouldThrowNPE() {
    assertThatExceptionOfType(NullPointerException.class)
        .isThrownBy(() -> scanner.scan(null))
        .withMessage("packageName is marked non-null but is null");
  }

  private BeanDefinition getBeanDefinitionByName(Set<BeanDefinition> set, String name) {
    return set.stream().filter(el -> el.getName().equals(name)).findFirst().orElseThrow();
  }
}
