package org.blyznytsia.scanner.data.configuration_scanner;

import org.blyznytsia.annotation.Bean;

public class NotAnnotatedConfig {
    @Bean
    public String dependency3() {
        return "dependency3";
    }

    @Bean
    public String dependency4() {
        return "dependency4";
    }
}