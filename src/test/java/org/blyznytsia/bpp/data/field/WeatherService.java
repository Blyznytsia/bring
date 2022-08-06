package org.blyznytsia.bpp.data.field;

import lombok.Getter;
import org.blyznytsia.annotation.Autowired;
import org.blyznytsia.annotation.Component;

@Component
@Getter
public class WeatherService {

  @Autowired private WeatherRepository weatherRepository;
  @Autowired private RegionRepository regionRepository;
}
