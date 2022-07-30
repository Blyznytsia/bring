package org.blyznytsia.context.data;

import java.util.List;
import org.blyznytsia.annotation.Autowired;
import org.blyznytsia.annotation.Component;
import org.blyznytsia.annotation.Qualifier;
import org.blyznytsia.annotation.Value;
import org.blyznytsia.context.data.TestConfig.Entity;

@Component
public class Controller {

  @Autowired private ServiceE serviceE;

  @Value("db.url")
  private String dbUrl;

  @Autowired private Entity entity;
  @Autowired private List<Convertor> convertors;

  private final IServiceA serviceA;

  private final ServiceB serviceB;

  @Autowired
  public Controller(@Qualifier("impl1") IServiceA serviceA, ServiceB serviceB) {
    this.serviceA = serviceA;
    this.serviceB = serviceB;
  }
}
