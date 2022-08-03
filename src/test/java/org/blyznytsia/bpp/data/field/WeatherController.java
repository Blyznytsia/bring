package org.blyznytsia.bpp.data.field;

import lombok.Getter;
import org.blyznytsia.annotation.Component;

@Component
public class WeatherController {

  @Getter private WeatherService weatherService;
}
