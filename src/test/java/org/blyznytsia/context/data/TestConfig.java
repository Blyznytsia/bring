package org.blyznytsia.context.data;

import org.blyznytsia.annotation.Bean;
import org.blyznytsia.annotation.Configuration;

@Configuration
public class TestConfig {
  @Bean(value = "beanForEntity", initMethod = "init")
  public Entity entity(Dependency dependency, AnotherDependency anotherDependency) {
    return new Entity(dependency, anotherDependency);
  }

  @Bean
  public String dependency2() {
    return "dependency2";
  }

  @Bean
  public Dependency dependency() {
    return new Dependency();
  }

  @Bean
  public AnotherDependency anotherDependency() {
    return new AnotherDependency();
  }

  public static class Entity {

    private Dependency dependency;
    private AnotherDependency anotherDependency;

    public Entity(Dependency dependency, AnotherDependency anotherDependency) {
      this.dependency = dependency;
      this.anotherDependency = anotherDependency;
    }

    public void init() {
      System.out.println("Running INIT method");
    }
  }

  public static class Dependency {}

  public static class AnotherDependency {}
}
