package org.blyznytsia.context.data;

import org.blyznytsia.annotation.Autowired;
import org.blyznytsia.annotation.Component;
import org.blyznytsia.annotation.Value;

@Component
public class Controller {

  @Autowired private ServiceE serviceE;

  @Value("db.url")
  private String dbUrl;

  private ServiceA serviceA;
  private ServiceB serviceB;

  @Autowired
  public Controller(ServiceA serviceA, ServiceB serviceB) {
    this.serviceA = serviceA;
    this.serviceB = serviceB;
  }
}
