package org.blyznytsia.validator;

import java.util.Set;
import org.blyznytsia.model.BeanDefinition;

public interface BeanValidator {

  void validate(Set<BeanDefinition> beanDefinitions);
}
