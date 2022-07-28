package org.blyznytsia.context.data;

import org.blyznytsia.annotation.Autowired;
import org.blyznytsia.annotation.Component;
import org.blyznytsia.annotation.Value;

@Component
public class ServiceA {

  @Value private int timeout;

  private ServiceC serviceC;

  @Autowired
  public ServiceA(ServiceC serviceC) {
    this.serviceC = serviceC;
  }
}
