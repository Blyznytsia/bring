package org.blyznytsia.context.data;

import java.util.List;
import org.blyznytsia.annotation.Autowired;
import org.blyznytsia.annotation.Component;
import org.blyznytsia.annotation.Value;
import org.blyznytsia.context.data.TestConfig.AnotherDependency;
import org.blyznytsia.context.data.TestConfig.Entity;

@Component
public class Controller2 {

  @Value("db.url")
  private String dbUrl;

  private final ServiceA serviceA;
  private final ServiceB serviceB;
  private final AnotherDependency anotherDependency;
  private final List<Convertor> convertors;

  @Autowired private ServiceE serviceE;
  // comes from @Configuration class
  @Autowired Entity entity;

  @Autowired
  public Controller2(
      ServiceA serviceA,
      ServiceB serviceB,
      List<Convertor> convertors,
      // comes from @Configuration class
      AnotherDependency anotherDependency) {
    this.serviceA = serviceA;
    this.serviceB = serviceB;
    this.convertors = convertors;
    this.anotherDependency = anotherDependency;
  }
}
