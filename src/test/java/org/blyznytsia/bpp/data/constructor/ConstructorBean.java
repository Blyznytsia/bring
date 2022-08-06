package org.blyznytsia.bpp.data.constructor;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.blyznytsia.annotation.Autowired;
import org.blyznytsia.annotation.Component;

@Component
@Data
@NoArgsConstructor
public class ConstructorBean {

  @Autowired private BoatBean boat;
  private BikeBean bike;
  private CarBean car;
  private final String name = "CONSTRUCTOR";

  @Autowired
  public ConstructorBean(BikeBean bike, CarBean car) {
    this.bike = bike;
    this.car = car;
  }
}
