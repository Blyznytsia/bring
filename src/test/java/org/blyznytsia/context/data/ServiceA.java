package org.blyznytsia.context.data;

import org.blyznytsia.annotation.Autowired;
import org.blyznytsia.annotation.Component;
import org.blyznytsia.annotation.Qualifier;
import org.blyznytsia.annotation.Value;

@Qualifier("impl1")
@Component
public class ServiceA implements IServiceA {

  @Value private int timeout;

  private ServiceC serviceC;

  @Autowired
  public ServiceA(ServiceC serviceC) {
    this.serviceC = serviceC;
  }
}
