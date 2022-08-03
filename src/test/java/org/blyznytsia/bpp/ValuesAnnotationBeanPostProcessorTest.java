package org.blyznytsia.bpp;

import org.blyznytsia.bpp.data.AccessPoint;
import org.blyznytsia.context.ApplicationContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class ValuesAnnotationBeanPostProcessorTest {

  @Mock ApplicationContext testContext;

  @Test
  void configure_givenFieldsAnnotatedWithValues_shouldInitializeItWithValue() {

    var beanPostProcessor1 = new ValueAnnotationBeanPostProcessor();
    var beanToConfigure1 = new AccessPoint();
    beanPostProcessor1.configure(beanToConfigure1, testContext);

    assertThat(beanToConfigure1.getHostVal()).isEqualTo("http://localhost");
    assertThat(beanToConfigure1.getPort()).isEqualTo("8082");

  }
}