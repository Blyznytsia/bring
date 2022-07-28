package org.blyznytsia.context.data;

import org.blyznytsia.annotation.Autowired;
import org.blyznytsia.annotation.Component;
import org.blyznytsia.annotation.Value;

@Component
public class Controller2 {

  @Autowired private ServiceE serviceE;

  @Value("db.url")
  private String dbUrl;

  private final ServiceA serviceA;
  private final ServiceB serviceB;

  @Autowired
  public Controller2(ServiceA serviceA, ServiceB serviceB) {
    this.serviceA = serviceA;
    this.serviceB = serviceB;
  }
}
