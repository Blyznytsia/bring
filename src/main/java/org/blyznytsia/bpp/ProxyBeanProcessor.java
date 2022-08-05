package org.blyznytsia.bpp;

/** Proxy interface to add additional logic to method */
public interface ProxyBeanProcessor {

  /** Configure created object with extra logic, put under a specific method or whole class */
  <T> T configure(T t);
}
