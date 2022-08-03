package org.blyznytsia.bpp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.blyznytsia.bpp.data.field.RegionRepository;
import org.blyznytsia.bpp.data.field.WeatherController;
import org.blyznytsia.bpp.data.field.WeatherRepository;
import org.blyznytsia.bpp.data.field.WeatherService;
import org.blyznytsia.context.ApplicationContext;
import org.blyznytsia.exception.BeanConfigurationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AutowiredAnnotationBeanPostProcessorTest {

  @Mock ApplicationContext testContext;

  @Test
  void configure_givenFieldsAnnotatedWithAutowired_shouldInitializeItWithValueFromContext() {
    when(testContext.getBean(WeatherRepository.class)).thenReturn(new WeatherRepository());
    when(testContext.getBean(RegionRepository.class)).thenReturn(new RegionRepository());

    var beanPostProcessor = new AutowiredAnnotationBeanPostProcessor();
    var beanToConfigure = new WeatherService();
    beanPostProcessor.configure(beanToConfigure, testContext);

    assertThat(beanToConfigure.getWeatherRepository()).isNotNull();
    assertThat(beanToConfigure.getRegionRepository()).isNotNull();

    verify(testContext, times(2)).getBean(any());
  }

  @Test
  void configure_givenExceptionWasThrown_shouldWrapItAndRethrowAsBeanConfigurationException() {
    var expectedCause = new RuntimeException("Exception during setting @Autowired field value");
    when(testContext.getBean(WeatherRepository.class)).thenThrow(expectedCause);

    var beanPostProcessor = new AutowiredAnnotationBeanPostProcessor();
    var beanToConfigure = new WeatherService();

    assertThatThrownBy(() -> beanPostProcessor.configure(beanToConfigure, testContext))
        .isInstanceOf(BeanConfigurationException.class)
        .hasMessageContaining("Exception during bean configuration")
        .hasCause(expectedCause);
  }

  @Test
  void configure_givenNoFieldsAnnotatedWithAutowired_shouldNotInitializeFields() {
    var beanPostProcessor = new AutowiredAnnotationBeanPostProcessor();
    var beanToConfigure = new WeatherController();

    beanPostProcessor.configure(beanToConfigure, testContext);

    verifyNoInteractions(testContext);
    assertThat(beanToConfigure.getWeatherService()).isNull();
  }
}
