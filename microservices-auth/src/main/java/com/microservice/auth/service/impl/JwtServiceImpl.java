package com.microservice.auth.service.impl;

import com.microservice.auth.exceptions.ApiErrors;
import com.microservice.auth.service.JwtService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtServiceImpl implements JwtService {

    public static final String SECRET = "w5Fhw7FpdG8gY29tbyBlc3RhcywgamhhbmVsbHkgcXVpZXJvIG1ldGVydGUgdG9kYSBsYSBwaW5nYSBwb3IgbGEgdmFnaW5hIHkgYW5hbG1lbnRlLCBpZ3VhbCBhIHRpIGFkcmlhbmEgcHJlY2lvc2E=";

    @Override
    public void validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(token);
        } catch (ExpiredJwtException ex) {
            throw new ApiErrors(HttpStatus.UNAUTHORIZED, "jwt expiro");
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException ex) {
            throw new ApiErrors(HttpStatus.FORBIDDEN, "error den jwt");
        }
    }




    @Override
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }

    private String createToken(Map<String, Object> claims, String username){
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+1000*60*30))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSignKey(){
        byte[] keyByte = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyByte);
    }
}
