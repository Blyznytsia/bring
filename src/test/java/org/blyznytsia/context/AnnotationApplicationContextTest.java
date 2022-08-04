package org.blyznytsia.context;

import static org.assertj.core.api.Assertions.assertThat;

import org.blyznytsia.context.data.Bean1;
import org.blyznytsia.context.data.Bean2;
import org.blyznytsia.context.data.Bean3;
import org.blyznytsia.context.data.Bean4;
import org.blyznytsia.context.data.Bean5;
import org.blyznytsia.context.data.Bean6;
import org.blyznytsia.context.data.Bean7;
import org.blyznytsia.context.data.BeanInterface;
import org.junit.jupiter.api.Test;

class AnnotationApplicationContextTest {

  private final ApplicationContext ctx = new AnnotationApplicationContext("org.blyznytsia");

  /*
   *      Bean1          Bean4
   *       / \            / \
   *      /  Bean6       /   \
   *    Bean2 <---------/   Bean5
   *     /
   *  Bean3
   */
  @Test
  void getBean_givenValidClassStructure_shouldPrintBeans() {
    System.out.println(ctx.getBean(Bean1.class));
    System.out.println(ctx.getBean(Bean2.class));
    System.out.println(ctx.getBean(Bean3.class));
    System.out.println(ctx.getBean(Bean4.class));
    System.out.println(ctx.getBean(Bean5.class));
    System.out.println(ctx.getBean(Bean6.class));
  }

  @Test
  void getBean_givenValidType_shouldReturnBean() {
    var bean1 = ctx.getBean(Bean1.class);
    assertThat(bean1).isInstanceOf(Bean1.class);
  }

  @Test
  void getBean_givenValidNameAndType_shouldReturnBean() {
    var bean5 = ctx.getBean("bean5", Bean5.class);
    assertThat(bean5).isInstanceOf(Bean5.class);
  }

  @Test
  void getAllBeans_givenValidType_shouldReturnBeans() {
    var beans = ctx.getAllBeans(BeanInterface.class);
    assertThat(beans).hasSize(2);
    assertThat(beans.get("bean1").getClass()).isEqualTo(Bean1.class);
    assertThat(beans.get("bean2").getClass()).isEqualTo(Bean2.class);
  }

  @Test
  void getListBean_givenValidBeanNameAndTypeInList_shouldReturnBean() {
    var bean7 = ctx.getBean("bean7", Bean7.class);
    assertThat(bean7).isInstanceOf(Bean7.class);
  }

}
