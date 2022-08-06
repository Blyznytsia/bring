package org.blyznytsia.context;

import static java.util.Collections.emptySet;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import lombok.SneakyThrows;
import org.blyznytsia.context.data.EmptyBean;
import org.blyznytsia.model.BeanDefinition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ObjectFactoryTest {

  @Mock private AnnotationApplicationContext context;

  @Spy private Map<String, Object> container = new HashMap<>();

  @SneakyThrows
  @Test
  void initiateContext_givenValidList_shouldInitializeContext() {
    // given:
    var beanDefinitions =
        Set.of(
            BeanDefinition.builder()
                .name("emptyBean")
                .type(EmptyBean.class)
                .fieldDependencies(emptySet())
                .requiredDependencies(emptySet())
                .constructor(EmptyBean.class.getConstructor())
                .build());

    when(context.getContainer()).thenReturn(container);
    var objectFactory = new ObjectFactory(context);

    // when:
    objectFactory.initiateContext(beanDefinitions);
    var expected = new EmptyBean();
    var actual = container.get("emptyBean");

    // then:
    assertEquals(expected, actual);
    verify(context).getContainer();
    assertFalse(container.isEmpty());
  }
}
