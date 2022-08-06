package org.blyznytsia.bpp.data;

import lombok.Getter;
import org.blyznytsia.annotation.Component;
import org.blyznytsia.annotation.Value;

@Component
@Getter
public class AccessPoint {

  @Value String hostVal;

  @Value("port")
  Integer port;

  @Value("id")
  Long id;
}
