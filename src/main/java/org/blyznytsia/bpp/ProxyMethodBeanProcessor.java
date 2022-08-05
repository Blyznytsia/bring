package org.blyznytsia.bpp;

import org.blyznytsia.annotation.ProxyMethod;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;

/** Default implementation of {@link ProxyBeanProcessor ProxyBeanProcessor} */
public class ProxyMethodBeanProcessor implements ProxyBeanProcessor {

  @Override
  public <T> T configure(T t) {
    Class<?> type = t.getClass();
    Enhancer enhancer = new Enhancer();
    enhancer.setSuperclass(type);
    MethodInterceptor methodInterceptor =
        (o, method, objects, methodProxy) -> {
          if (method.isAnnotationPresent(ProxyMethod.class)) {
            System.out.printf(
                "Method proxy: Calling method %s of the class %s",
                method.getName(), type.getSimpleName());
          }

          return methodProxy.invokeSuper(o, objects);
        };
    enhancer.setCallback(methodInterceptor);
    return (T) enhancer.create();
  }
}
