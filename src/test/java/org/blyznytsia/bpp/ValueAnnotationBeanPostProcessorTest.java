package org.blyznytsia.bpp;

import static org.assertj.core.api.Assertions.assertThat;

import org.blyznytsia.bpp.data.AccessPoint;
import org.blyznytsia.context.ApplicationContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ValueAnnotationBeanPostProcessorTest {

  @Mock ApplicationContext testContext;

  @Test
  void configure_givenFieldsAnnotatedWithValues_shouldInitializeItWithValue() {

    var beanPostProcessor = new ValueAnnotationBeanPostProcessor();
    var beanToConfigure = new AccessPoint();
    beanPostProcessor.configure(beanToConfigure, testContext);

    assertThat(beanToConfigure.getHostVal()).isEqualTo("http://localhost");
    assertThat(beanToConfigure.getPort()).isEqualTo(8082);
    assertThat(beanToConfigure.getId()).isEqualTo(345123L);
  }
}
