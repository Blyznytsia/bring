package org.blyznytsia.context.data;

import org.blyznytsia.annotation.Autowired;
import org.blyznytsia.annotation.Component;
import org.blyznytsia.annotation.Qualifier;
import org.blyznytsia.annotation.Value;

@Qualifier("impl2")
@Component
public class ServiceA1 implements IServiceA {

  @Value private int timeout;

  private ServiceC serviceC;

  @Autowired
  public ServiceA1(ServiceC serviceC) {
    this.serviceC = serviceC;
  }
}
