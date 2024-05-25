package com.unity.potato.config.jwt;

import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

public class JwtFactory {
    private String subject = "test@email.com";
    private Date issuedAt = new Date();
    private Date expiration = new Date(new Date().getTime() + Duration.ofDays(14).toMillis());
    private Map<String, Object> claims = Collections.emptyMap();


}
