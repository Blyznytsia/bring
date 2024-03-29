package org.blyznytsia.scanner.data.configuration_scanner;

import org.blyznytsia.annotation.Bean;
import org.blyznytsia.annotation.Component;
import org.blyznytsia.annotation.Configuration;

@Configuration
public class TestConfig {
  @Bean(value = "beanForEntity", initMethod = "init")
  public Entity entity(Dependency dependency, AnotherDependency anotherDependency) {
    return new Entity();
  }

  @Bean
  public String dependency2() {
    return "dependency2";
  }

  public static class Entity {

    public void init() {
      // no op
    }
  }

  @Component
  public static class Dependency {}

  @Component
  public static class AnotherDependency {}
}
