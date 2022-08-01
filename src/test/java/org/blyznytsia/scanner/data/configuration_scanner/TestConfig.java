package org.blyznytsia.scanner.data.configuration_scanner;

import org.blyznytsia.annotation.Bean;
import org.blyznytsia.annotation.Configuration;

@Configuration
public class TestConfig {
  @Bean(value = "beanForEntity", initMethod = "init")
  public Entity
      entity() { // todo deleted because of conflict with bean processing in current implementation
    return new Entity();
  }
  //  public Entity entity(Dependency dependency, AnotherDependency anotherDependency) {
  //    return new Entity();
  //  }

  @Bean
  public String dependency2() {
    return "dependency2";
  }

  public static class Entity {

    public void init() {
      // no op
    }
  }

  public static class Dependency {}

  public static class AnotherDependency {}
}
