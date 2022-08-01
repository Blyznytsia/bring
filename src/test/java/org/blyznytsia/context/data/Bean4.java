package org.blyznytsia.context.data;

import lombok.Data;
import org.blyznytsia.annotation.Autowired;
import org.blyznytsia.annotation.Component;

@Component
@Data
public class Bean4 {

  @Autowired private Bean2 bean2;
  @Autowired private Bean5 bean5;

  private String name = "BEAN4";
}
