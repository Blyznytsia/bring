package org.blyznytsia.bpp;

import static org.assertj.core.api.Assertions.assertThat;

import org.blyznytsia.bpp.data.TestProxyMethod;
import org.blyznytsia.context.ApplicationContext;
import org.junit.Rule;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ProxyMethodBeanProcessorTest {

  @Mock ApplicationContext testContext;

  @Rule public final SystemOutRule systemOutRule = new SystemOutRule().enableLog();

  @Test
  void test() {
    var beanPostProcessor = new ProxyMethodBeanProcessor();
    var normalBean = new TestProxyMethod();
    TestProxyMethod proxyBean = beanPostProcessor.configure(normalBean);
    //        when(testContext.getBean(TestProxyMethod.class)).thenReturn(proxyBean);
    //        TestProxyMethod testProxyBean = testContext.getBean(TestProxyMethod.class);
    proxyBean.test();

    assertThat(normalBean.test()).isEqualTo("test");
    assertThat(proxyBean.test()).isEqualTo("test");
    assertThat(systemOutRule.getLog())
        .contains("Method proxy: Calling method test of the class TestProxyMethod");
    // now this is a cglib class
    assertThat(proxyBean.getClass()).isNotEqualTo(TestProxyMethod.class);
  }
}
