package org.blyznytsia.bpp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.blyznytsia.bpp.data.constructor.BikeBean;
import org.blyznytsia.bpp.data.constructor.CarBean;
import org.blyznytsia.bpp.data.constructor.ConstructorBean;
import org.blyznytsia.context.ApplicationContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AutowiredConstructorBeanPostProcessorTest {

  @Mock ApplicationContext context;

  @Test
  void configure_givenValidClass_shouldConfigureBean() {
    // given:
    when(context.getBean(BikeBean.class)).thenReturn(new BikeBean());
    when(context.getBean(CarBean.class)).thenReturn(new CarBean());

    var beanPostProcessor = new AutowiredConstructorBeanPostProcessor();
    var beanToConfigure = new ConstructorBean();
    beanPostProcessor.configure(beanToConfigure, context);

    assertThat(beanToConfigure.getBike()).isNotNull();
    assertThat(beanToConfigure.getCar()).isNotNull();

    verify(context, times(2)).getBean(any());
  }
}
