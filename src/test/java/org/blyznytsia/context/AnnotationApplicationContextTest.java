package org.blyznytsia.context;

import org.blyznytsia.context.data.Controller;
import org.junit.jupiter.api.Test;

class AnnotationApplicationContextTest {

  @Test
  void getBean() {
    ApplicationContext context = new AnnotationApplicationContext("org.blyznytsia.context.data");
    Controller bean = context.getBean(Controller.class);
    System.out.println(bean);
  }
}
