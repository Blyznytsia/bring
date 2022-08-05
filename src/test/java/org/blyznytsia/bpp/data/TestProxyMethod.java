package org.blyznytsia.bpp.data;

import org.blyznytsia.annotation.Component;
import org.blyznytsia.annotation.ProxyMethod;

@Component
public class TestProxyMethod {

  @ProxyMethod
  public String test() {
    return "test";
  }
}
