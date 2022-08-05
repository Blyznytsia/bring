package org.blyznytsia.scanner;

import static java.util.Collections.emptySet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.blyznytsia.model.Scope.SINGLETON;
import static org.blyznytsia.scanner.data.configuration_scanner.TestConfig.Entity;

import java.util.List;
import java.util.Set;
import org.blyznytsia.model.BeanDefinition;
import org.blyznytsia.scanner.data.configuration_scanner.TestConfig;
import org.blyznytsia.scanner.data.configuration_scanner.TestConfig.AnotherDependency;
import org.blyznytsia.scanner.data.configuration_scanner.TestConfig.Dependency;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ConfigurationAnnotationScannerTest {

  static final String TEST_PACKAGE = "org.blyznytsia.scanner.data.configuration_scanner";
  static final String DEFAULT_INIT_METHOD_NAME = "";
  BeanScanner scanner = new ConfigurationAnnotationScanner();

  @DisplayName(
      """
                        Scanner should find all @Configuration classes and create bean
                        definitions from methods annotated with @Bean and configuration itself
                    """)
  @Test
  void shouldScanAndBuildDefinitionsForBeansAndConfiguration() throws NoSuchMethodException {
    var actualDefinitions = scanner.scan(TEST_PACKAGE);

    var beanDefinition1 =
        BeanDefinition.builder()
            .name("beanForEntity")
            .type(Entity.class)
            .configClassDependency(true)
            .beanMethod(
                TestConfig.class.getMethod("entity", Dependency.class, AnotherDependency.class))
            .configClass(TestConfig.class)
            .requiredDependencies(Set.of("dependency", "anotherDependency"))
            .fieldDependencies(emptySet())
            .scope(SINGLETON)
            .initMethod("init")
            .build();

    var beanDefinition2 =
        BeanDefinition.builder()
            .name("dependency2")
            .type(String.class)
            .configClassDependency(true)
            .beanMethod(TestConfig.class.getMethod("dependency2"))
            .configClass(TestConfig.class)
            .fieldDependencies(emptySet())
            .requiredDependencies(emptySet())
            .initMethod(DEFAULT_INIT_METHOD_NAME)
            .scope(SINGLETON)
            .build();

    var expectedDefinitions = List.of(beanDefinition1, beanDefinition2);

    assertThat(actualDefinitions).containsExactlyInAnyOrderElementsOf(expectedDefinitions);
  }

  @DisplayName("@Bean('beanName') should be used for bean definition name if provided")
  @Test
  void shouldUseBeanNameIfSet() {
    var actualDefinitions = scanner.scan(TEST_PACKAGE);

    assertThat(actualDefinitions)
        .filteredOn(beanDefinition -> beanDefinition.getName().equals("beanForEntity"))
        .hasSize(1);
  }

  @DisplayName(
      "Method name should be used for bean definition name if no @Bean('beanName') is set ")
  @Test
  void shouldUseMethodNameForAsBeanName() {
    var actualDefinitions = scanner.scan(TEST_PACKAGE);

    assertThat(actualDefinitions)
        .filteredOn(beanDefinition -> beanDefinition.getName().equals("dependency2"))
        .hasSize(1);
  }

  @DisplayName("IllegalArgumentException should be thrown if package is blank")
  @ParameterizedTest
  @ValueSource(strings = {" ", ""})
  void shouldThrowExceptionIfEmptyPackageProvided(String packageName) {
    assertThatThrownBy(() -> scanner.scan(packageName))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Blank package isn't allowed");
  }

  @DisplayName("NPE should be thrown if package is null")
  @Test
  void shouldThrowNPEIfPackageIsNull() {
    assertThatThrownBy(() -> scanner.scan(null))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("packageName is marked non-null but is null");
  }
}
