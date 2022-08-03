package org.blyznytsia.processor;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.blyznytsia.context.AnnotationApplicationContext;
import org.blyznytsia.model.BeanDefinition;
import org.blyznytsia.processor.data.EmptyBean;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reflections.Reflections;

@ExtendWith(MockitoExtension.class)
class BeanFactoryProcessorTest {

  @Mock private AnnotationApplicationContext context;

  @Spy private Map<String, Object> container = new HashMap<>();

  @Test
  void initiateContext_givenValidList_shouldInitializeContext() {
    // given:
    var beanDefinitions =
        List.of(
            BeanDefinition.builder()
                .name("emptyBean")
                .type(EmptyBean.class)
                .dependsOnBeans(emptyList())
                .build());

    when(context.getContainer()).thenReturn(container);
    when(context.getReflections()).thenReturn(new Reflections());
    var beanFactoryProcessor = new BeanFactoryProcessor(context);

    // when:
    beanFactoryProcessor.initiateContext(beanDefinitions);
    var expected = new EmptyBean();
    var actual = container.get("emptyBean");

    // then:
    assertEquals(expected, actual);
    verify(context).getReflections();
    verify(context).getContainer();
    assertFalse(container.isEmpty());
  }
}
