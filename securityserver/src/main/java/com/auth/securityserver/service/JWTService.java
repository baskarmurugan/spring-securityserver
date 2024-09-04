package com.auth.securityserver.service;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@Service
public class JWTService {

    @Value("$(jwt.secret)")
    private String secret;
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", userDetails.getAuthorities());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())  // Ensure the subject is set here.
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))  // Fixed expiration time.
                .signWith(getSignatureKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(Map<String, Object> extraClaims, UserDetails userDetails){

        return Jwts.builder().
                setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() *684800000))
                .signWith(getSignatureKey(), SignatureAlgorithm.HS256)
                .compact();
    }


    public String extractUserName(String token){
        return extractClaims(token, Claims::getSubject);
    }

    public String extractRole(String token){
        return extractClaims(token, claims -> claims.get("role")).toString();
    }
    private <T> T extractClaims(String token, Function<Claims , T> claimsResolver){
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token){
        System.out.println("tokentokentoken : "+token);
        return Jwts.parserBuilder().setSigningKey(getSignatureKey()).build().parseClaimsJws(token).getBody();
    }
//    private Key getSignatureKey(){
//
//        byte[] key = Decoders.BASE64.decode("98745825858789456123321456987458213695478541253697145841412258841421485411584S51441D784151D84454154");
//        return Keys.hmacShaKeyFor(key);
//    }

    private Key getSignatureKey() {
        String base64Secret = "98745825858789456123321456987458213695478541253697145841412258841421485411584S51441D784151D84454154";
        byte[] keyBytes;
        try {
            keyBytes = Decoders.BASE64.decode(base64Secret);
        } catch (Exception e) {
            throw new RuntimeException("Failed to decode Base64 secret", e);
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }


    public boolean isTokenValid(String token, UserDetails userDetails){
        final String userNAme = extractUserName(token);
        return (userNAme.equals(userDetails.getUsername()) &&  !isTokenExpired(token));
    }
    public boolean isTokenExpired(String token){
        return extractClaims(token, Claims::getExpiration).before(new Date());
    }
}
