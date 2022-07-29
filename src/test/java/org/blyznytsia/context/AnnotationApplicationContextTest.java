package org.blyznytsia.context;

import lombok.extern.slf4j.Slf4j;
import org.blyznytsia.context.data.Bean1;
import org.blyznytsia.context.data.Bean2;
import org.blyznytsia.context.data.Bean3;
import org.blyznytsia.context.data.Bean4;
import org.junit.jupiter.api.Test;

@Slf4j
class AnnotationApplicationContextTest {

  private final ApplicationContext ctx = new AnnotationApplicationContext("org.blyznytsia");

  /*
   *      Bean1    Bean4
   *       /        /
   *    Bean2 <----/
   *     /
   *  Bean3
   */
  @Test
  void getBean_givenValidClasses_shouldPrintBeans() {
    System.out.println(ctx.getBean(Bean1.class));
    System.out.println(ctx.getBean(Bean2.class));
    System.out.println(ctx.getBean(Bean3.class));
    System.out.println(ctx.getBean(Bean4.class));
  }
}
