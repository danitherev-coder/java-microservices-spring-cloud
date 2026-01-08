package com.microservice.gateway.microservice_gateway.util;



import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;

@Service
public class JwtServiceImpl implements JwtService {

    public static final String SECRET = "w5Fhw7FpdG8gY29tbyBlc3RhcywgamhhbmVsbHkgcXVpZXJvIG1ldGVydGUgdG9kYSBsYSBwaW5nYSBwb3IgbGEgdmFnaW5hIHkgYW5hbG1lbnRlLCBpZ3VhbCBhIHRpIGFkcmlhbmEgcHJlY2lvc2E=";

    @Override
    public void validateToken(String token) {
        Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token);
    }


    private Key getSignKey(){
        byte[] keyByte = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyByte);
    }
}
