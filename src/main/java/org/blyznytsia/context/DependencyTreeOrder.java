package org.blyznytsia.context;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.blyznytsia.model.BeanDefinition;

public class DependencyTreeOrder {

  LinkedList<BeanDefinition> beanDefinitions;

  public DependencyTreeOrder(List<BeanDefinition> beanDefinitions) {
    this.beanDefinitions = new LinkedList<>(beanDefinitions);
    this.beanDefinitions.sort(
        (bean1, bean2) -> {
          int dependsBeans1 = bean1.getDependsOnBeans().size();
          int dependsBeans2 = bean2.getDependsOnBeans().size();
          return Integer.compare(dependsBeans1, dependsBeans2);
        });
  }

  public List<String> buildSortedBeans() {

    List<String> readyToCreateBeans = new ArrayList<>();
    while (!beanDefinitions.isEmpty()) {
      BeanDefinition bean = beanDefinitions.pop();
      List<String> beans = bean.getDependsOnBeans();

      if (beans.isEmpty() || readyToCreateBeans.containsAll(beans)) {
        readyToCreateBeans.add(bean.getName());
      } else {
        beanDefinitions.add(bean);
      }
    }

    return readyToCreateBeans;
  }
}
