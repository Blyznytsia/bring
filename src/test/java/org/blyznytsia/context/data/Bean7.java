package org.blyznytsia.context.data;

import java.util.List;
import lombok.Data;
import org.blyznytsia.annotation.Autowired;
import org.blyznytsia.annotation.Component;

@Component
@Data
public class Bean7 {
  @Autowired List<Bean3> bean3List;
}
