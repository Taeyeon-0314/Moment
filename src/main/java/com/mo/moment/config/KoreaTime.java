package com.mo.moment.config;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class KoreaTime {

    public ZonedDateTime koreaDateTime(){
        ZoneId seoulZone = ZoneId.of("Asia/Seoul");
        ZonedDateTime now = ZonedDateTime.now(seoulZone);
        return now;
    }
}
