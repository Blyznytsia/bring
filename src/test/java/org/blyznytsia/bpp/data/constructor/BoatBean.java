package org.blyznytsia.bpp.data.constructor;

import lombok.NoArgsConstructor;
import org.blyznytsia.annotation.Component;

@Component
@NoArgsConstructor
public class BoatBean {

  private String name;

  public BoatBean(String name) {
    this.name = name;
  }
}
