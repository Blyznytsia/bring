package org.blyznytsia.scanner;

import org.blyznytsia.exception.NotImplementedException;
import org.blyznytsia.model.BeanDefinition;
import org.reflections.Reflections;

import java.util.List;

public class ComponentAnnotationScanner implements BeanScanner {

    @Override
    public List<BeanDefinition> scan(String packageName) {
        throw new NotImplementedException("Need to implement this method");
    }

}
