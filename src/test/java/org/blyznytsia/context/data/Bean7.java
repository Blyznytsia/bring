package org.blyznytsia.context.data;

import lombok.Data;
import org.blyznytsia.annotation.Autowired;
import org.blyznytsia.annotation.Component;

import java.util.List;

@Component
@Data
public class Bean7 {
  @Autowired
  List<Bean3> bean3List;
}