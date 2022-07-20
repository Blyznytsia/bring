package org.blyznytsia.scanner;


import org.assertj.core.api.Assertions;
import org.blyznytsia.model.BeanDefinition;
import org.blyznytsia.scanner.data.TestService1;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

class ComponentAnnotationScannerTest {

    private static final String PACKAGE_NAME = "org.blyznytsia.scanner.data";
    private static final String SERVICE1_NAME = "anotherNameService";
    private static final String SERVICE2_NAME = "testService2";
    private static final int BEAN_DEFINITIONS_SIZE = 2;
    private static final int SERVICE2_DEPENDENCIES_SIZE = 1;
    private static final String SERVICE2_DEPENDENCY_NAME = TestService1.class.getName();

    @Test
    void scan_givenValidPackage_shouldReturnValidBeanDefinitionsList() {
        final var beanDefinitions =
                new ComponentAnnotationScanner().scan(PACKAGE_NAME);
        beanDefinitions.forEach(System.out::println);
        Assertions.assertThat(checkFullPresence(beanDefinitions, SERVICE2_NAME, SERVICE1_NAME)).isTrue();

        final var service1 = getBeanDefinitionByName(beanDefinitions, SERVICE1_NAME);
        final var service2 = getBeanDefinitionByName(beanDefinitions, SERVICE2_NAME);

        Assertions.assertThat(beanDefinitions).hasSize(BEAN_DEFINITIONS_SIZE);
        Assertions.assertThat(service1.getDependsOnBeans()).isEmpty();
        Assertions.assertThat(service2.getDependsOnBeans()).hasSize(SERVICE2_DEPENDENCIES_SIZE);
        Assertions.assertThat(service2.getDependsOnBeans().get(0)).isEqualTo(SERVICE2_DEPENDENCY_NAME);
    }

    private BeanDefinition getBeanDefinitionByName(final List<BeanDefinition> list, final String name) {
        return list.stream().filter(el -> el.getName().equals(name)).findFirst().orElseThrow();
    }

    private boolean checkFullPresence(final List<BeanDefinition> list, final String... names) {
        return Arrays.equals(list.stream().map(BeanDefinition::getName).toArray(), names);
    }
}
