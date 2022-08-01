package org.blyznytsia.context.data;

import lombok.Data;
import org.blyznytsia.annotation.Autowired;
import org.blyznytsia.annotation.Component;

@Component
@Data
public class Bean1 {

  @Autowired private Bean2 bean2;
  @Autowired private Bean6 bean6;
}
