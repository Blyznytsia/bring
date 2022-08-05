package org.blyznytsia.bpp.data;

import lombok.Getter;
import org.blyznytsia.annotation.Component;
import org.blyznytsia.annotation.Values;

@Component
@Getter
public class AccessPoint {

    @Values("host")
    String hostVal;

    @Values("port")
    Integer port;

    @Values("id")
    Long id;
}
