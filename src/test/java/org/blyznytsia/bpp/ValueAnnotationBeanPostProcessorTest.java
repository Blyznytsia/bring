package org.blyznytsia.bpp;

import static org.assertj.core.api.Assertions.assertThat;

import org.blyznytsia.bpp.data.AccessPoint;
import org.junit.jupiter.api.Test;

class ValueAnnotationBeanPostProcessorTest {

  @Test
  void configure_givenFieldsAnnotatedWithValues_shouldInitializeItWithValue() {
    var beanPostProcessor = new ValueAnnotationBeanPostProcessor();
    var beanToConfigure = new AccessPoint();
    beanPostProcessor.configure(beanToConfigure, null);

    assertThat(beanToConfigure.getHostVal()).isEqualTo("http://localhost");
    assertThat(beanToConfigure.getPort()).isEqualTo(8082);
    assertThat(beanToConfigure.getId()).isEqualTo(345123L);
  }
}
