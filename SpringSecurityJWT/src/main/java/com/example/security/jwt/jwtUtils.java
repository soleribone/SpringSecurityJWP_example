package com.example.security.jwt;

import org.springframework.stereotype.Component;

@Component //va a ser un componente administrado por spring
public class jwtUtils {

    private String secretKey;
    private String timeExpiration;
}
