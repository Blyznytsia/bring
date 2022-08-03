package org.blyznytsia.validator;

import java.util.List;
import org.blyznytsia.model.BeanDefinition;

public interface BeanValidator {

  void validate(List<BeanDefinition> beanDefinitions);
}
