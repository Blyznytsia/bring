package org.blyznytsia.scanner.data.component_scanner;

import org.blyznytsia.annotation.Autowired;
import org.blyznytsia.annotation.Component;

@Component
public class TestService2 {

    @Autowired
    private TestService1 service1;

    @Autowired
    private TestService3 service2;
}
