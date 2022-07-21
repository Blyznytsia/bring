package org.blyznytsia.scanner;


import lombok.extern.slf4j.Slf4j;
import org.blyznytsia.model.BeanDefinition;
import org.blyznytsia.scanner.data.TestService1;
import org.blyznytsia.scanner.data.TestService3;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@Slf4j
class ComponentAnnotationScannerTest {

    private static final String PACKAGE_NAME = "org.blyznytsia.scanner.data";
    private static final String SERVICE1_NAME = "anotherNameService";
    private static final String SERVICE2_NAME = "testService2";
    private static final String SERVICE1_DEPENDENCY_NAME = TestService3.class.getName();
    private static final String[] SERVICE2_DEPENDENCY_NAMES = {TestService1.class.getName(), TestService3.class.getName()};
    private static final String NPE_MSG = "packageName is marked non-null but is null";

    private final ComponentAnnotationScanner scanner = new ComponentAnnotationScanner();

    @Test
    void scan_givenValidPackage_shouldReturnValidBeanDefinitionsList() {
        var beanDefinitions = scanner.scan(PACKAGE_NAME);
        log.debug("List<BeanDefinitions> [{}]: {}", beanDefinitions.size(), beanDefinitions);

        assertThat(beanDefinitions).map(BeanDefinition::getName)
                .containsOnly(SERVICE2_NAME, SERVICE1_NAME);

        var service1 = getBeanDefinitionByName(beanDefinitions, SERVICE1_NAME);
        var service2 = getBeanDefinitionByName(beanDefinitions, SERVICE2_NAME);

        assertThat(service1.getDependsOnBeans())
                .containsOnly(SERVICE1_DEPENDENCY_NAME);
        assertThat(service2.getDependsOnBeans())
                .containsOnly(SERVICE2_DEPENDENCY_NAMES);
    }

    @Test
    void scan_givenNullPackage_shouldThrowNPEWithCertainMsg() {
        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> scanner.scan(null))
                .withMessage(NPE_MSG);
    }

    private BeanDefinition getBeanDefinitionByName(List<BeanDefinition> list, String name) {
        return list.stream().filter(el -> el.getName().equals(name)).findFirst().orElseThrow();
    }
}
