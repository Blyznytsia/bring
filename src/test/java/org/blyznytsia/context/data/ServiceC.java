package org.blyznytsia.context.data;

import org.blyznytsia.annotation.Autowired;
import org.blyznytsia.annotation.Component;

@Component
public class ServiceC {

  ServiceD serviceD;

  @Autowired
  public ServiceC(ServiceD serviceD) {
    this.serviceD = serviceD;
  }
}
