package org.blyznytsia.bpp;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.blyznytsia.annotation.Autowired;
import org.blyznytsia.annotation.Values;
import org.blyznytsia.context.ApplicationContext;
import org.blyznytsia.exception.BeanConfigurationException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

/**
 * Implementation of {@link BeanPostProcessor} interface that injects values into fields annotated
 * with {@link org.blyznytsia.annotation.Values}
 */
@Slf4j
public class ValueAnnotationBeanPostProcessor implements BeanPostProcessor  {

    Map<String, String> map = new HashMap<>();
    // comment add
    @SneakyThrows
   public ValueAnnotationBeanPostProcessor() {
        String path = ClassLoader.getSystemClassLoader().getResource("application.properties").getPath();
        Stream<String> lines = new BufferedReader(new FileReader(path)).lines();
        map = lines.map(line -> line.split("=")).collect(toMap(arr -> arr[0], arr -> arr[1]));
    }
    @Override
    public Object configure(Object bean, ApplicationContext context) {
        log.debug("Configuring bean of {} type", bean.getClass());
        try {
            for (Field field : bean.getClass().getDeclaredFields()) {
                Values annotation = field.getAnnotation(Values.class);
                if (annotation != null) {
                    String propertyName = annotation.value();
                    String propertyValue = map.get(propertyName);
                    field.setAccessible(true);
                    field.set(bean,propertyValue);
                }
            }
        } catch (Exception e) {
            log.error("Exception during bean configuration: {}", e.getMessage());
            throw new BeanConfigurationException("Exception during bean configuration", e);
        }

        return bean;
    }

}
