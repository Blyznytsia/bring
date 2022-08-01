package org.blyznytsia.context.data;

import lombok.Data;
import org.blyznytsia.annotation.Autowired;
import org.blyznytsia.annotation.Component;

@Component
@Data
public class Bean6 {
  @Autowired Bean3 bean3;
}
