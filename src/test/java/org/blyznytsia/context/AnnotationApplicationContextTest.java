package org.blyznytsia.context;

import static org.assertj.core.api.Assertions.assertThat;

import org.blyznytsia.context.data.Controller;
import org.blyznytsia.context.data.Controller2;
import org.blyznytsia.context.data.ServiceA;
import org.blyznytsia.context.data.TestConfig.Entity;
import org.junit.jupiter.api.Test;

class AnnotationApplicationContextTest {

  @Test
  void testContextInitialized() {
    var context = new AnnotationApplicationContext("org.blyznytsia.context.data");

    Controller controller1 = context.getBean(Controller.class);
    Controller cachedController1 = context.getBean(Controller.class);
    Controller2 controller2 = context.getBean(Controller2.class);
    ServiceA serviceA = context.getBean("serviceA", ServiceA.class);

    Entity beanForEntity = context.getBean("beanForEntity", Entity.class);

    assertThat(controller1).isNotNull().isEqualTo(cachedController1);
    assertThat(controller2).isNotNull();
    assertThat(serviceA).isNotNull();
  }
}
