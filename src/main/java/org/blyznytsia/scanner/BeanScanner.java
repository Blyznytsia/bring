package org.blyznytsia.scanner;

import org.blyznytsia.model.BeanDefinition;

import java.util.List;

/**
 * A bean definition scanner that detects bean candidates in the package, registering corresponding bean definitions.
 * Candidate classes are detected through configurable type filters.
 * The default filters include classes that are annotated with Bring's
 * {@link org.blyznytsia.annotation.Component Component},
 * {@link org.blyznytsia.annotation.Configuration Configuration},
 * {@link org.blyznytsia.annotation.Bean Bean}
 *
 * @see org.blyznytsia.annotation.Component
 * @see org.blyznytsia.annotation.Configuration
 * @see org.blyznytsia.annotation.Bean
 */
public interface BeanScanner {

    List<BeanDefinition> scan(String packageName);

}
