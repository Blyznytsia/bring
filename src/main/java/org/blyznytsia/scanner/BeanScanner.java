package org.blyznytsia.scanner;

import org.blyznytsia.model.BeanDefinition;

import java.util.List;

public interface BeanScanner {

    List<BeanDefinition<?>> scan(String packageName);

}
