package org.blyznytsia.scanner.data;

import org.blyznytsia.annotation.Autowired;
import org.blyznytsia.annotation.Component;

@Component("anotherNameService")
public class TestService1 {

    @Autowired
    private TestService3 service;
}
