package org.blyznytsia.bpp.data;

import lombok.Getter;
import org.blyznytsia.annotation.Component;

@Component
public class WeatherController {

  @Getter private WeatherService weatherService;
}
