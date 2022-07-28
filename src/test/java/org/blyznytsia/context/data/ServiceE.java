package org.blyznytsia.context.data;

import org.blyznytsia.annotation.Autowired;
import org.blyznytsia.annotation.Component;

@Component
public class ServiceE {

  @Autowired private ServiceF serviceF;
}
