package org.blyznytsia.validator;

import org.blyznytsia.exception.CircularDependencyException;
import org.blyznytsia.model.BeanDefinition;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DependencyValidatorTest {

    static final String SERVICE_TWO = "serviceTwo";
    static final String SERVICE_TREE = "serviceTree";
    static final String SERVICE_ONE = "serviceOne";
    static final String EXCEPTION_MESSAGE = "The dependencies of some of the beans in the application context form a cycle:\n";
    static List<BeanDefinition> beanDefinitions;
    static DependencyValidator dependencyValidator;

    @Test
    void calculateCycles_shouldThrowExceptionWithMessage() {
        BeanDefinition beanOne = BeanDefinition.builder()
            .name(SERVICE_ONE)
            .dependsOnBeans(List.of(SERVICE_TWO))
            .type(ServiceOne.class)
            .build();


        BeanDefinition beanTwo = BeanDefinition.builder()
            .name(SERVICE_TWO)
            .dependsOnBeans(List.of(SERVICE_ONE))
            .type(ServiceTwo.class)
            .build();

        beanDefinitions = List.of(beanOne, beanTwo);

        dependencyValidator = DependencyValidator.buildDependencyGraph(beanDefinitions);

        CircularDependencyException exception = assertThrows(CircularDependencyException.class,
            () -> dependencyValidator.calculateCycles());

        String message = exception.getMessage();
        String expectedMessage = EXCEPTION_MESSAGE + "serviceOne->serviceTwo->serviceOne";

        assertEquals(expectedMessage, message);
    }

    @Test
    void calculateCycles_shouldThrowExceptionWithMessage2() {
        BeanDefinition beanOne = BeanDefinition.builder()
            .name(SERVICE_ONE)
            .dependsOnBeans(Arrays.asList(SERVICE_TWO, SERVICE_TREE))
            .type(ServiceOne.class)
            .build();


        BeanDefinition beanTwo = BeanDefinition.builder()
            .name(SERVICE_TWO)
            .dependsOnBeans(List.of(SERVICE_TREE))
            .type(ServiceTwo.class)
            .build();

        BeanDefinition beanThree = BeanDefinition.builder()
            .name(SERVICE_TREE)
            .dependsOnBeans(List.of(SERVICE_ONE))
            .type(ServiceThree.class)
            .build();

        beanDefinitions = List.of(beanOne, beanTwo, beanThree);

        dependencyValidator = DependencyValidator.buildDependencyGraph(beanDefinitions);

        CircularDependencyException exception = assertThrows(CircularDependencyException.class,
            () -> dependencyValidator.calculateCycles());

        String message = exception.getMessage();

        String expectedMessage = EXCEPTION_MESSAGE + "serviceOne->serviceTwo->serviceTree->serviceOne";

        assertEquals(expectedMessage, message);
    }


    @Test
    void calculateCycles_shouldReturnEmptySet() {

        BeanDefinition beanOne = BeanDefinition.builder()
            .name(SERVICE_ONE)
            .dependsOnBeans(Arrays.asList(SERVICE_TWO, SERVICE_TREE))
            .type(ServiceOne.class)
            .build();


        BeanDefinition beanTwo = BeanDefinition.builder()
            .name(SERVICE_TWO)
            .dependsOnBeans(List.of(SERVICE_TREE))
            .type(ServiceTwo.class)
            .build();

        BeanDefinition beanThree = BeanDefinition.builder()
            .name(SERVICE_TREE)
            .dependsOnBeans(List.of())
            .type(ServiceThree.class)
            .build();

        beanDefinitions = List.of(beanOne, beanTwo, beanThree);

        dependencyValidator = DependencyValidator.buildDependencyGraph(beanDefinitions);

        Set<String> circularDependencyBeans = dependencyValidator.calculateCycles();

        assertTrue(circularDependencyBeans.isEmpty());
    }
}
