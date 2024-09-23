package com.abel.videochattingsystem.Config;


import com.abel.videochattingsystem.Exception.ApiExceptionHandler;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
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
import java.util.function.Function;

@Service
public class JwtService {

    private static final String SECRET_KEY = "72357538782F413F4428472B4B6250645367566B597033733676397924422645";
    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpiration;

    private final ApiExceptionHandler apiExceptionHandler;


    public JwtService(ApiExceptionHandler apiExceptionHandler) {
        this.apiExceptionHandler = apiExceptionHandler;
    }

    public String generateRefreshToken(
            UserDetails userDetails
    ) {
        return buildToken(new HashMap<>(), userDetails, refreshExpiration);
    }

    public String extractUsername(String jwt) {
        return extractClaim(jwt, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // generate token without parsing claims
    public String generateToken(UserDetails userDetails){
        return generateToken(new HashMap<>(), userDetails);
    }

    // generate token with claims and userDetails
    public String generateToken (Map<String, Object> extraClaims, UserDetails userDetails){
        // userDetails.getUsername() is the unique email for a user
        try {
            return Jwts.builder().
                    setClaims(extraClaims).
                    setSubject(userDetails.getUsername()).
                    setIssuedAt(new Date(System.currentTimeMillis())).
                    setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24)).
                    signWith(getSigningKey(), SignatureAlgorithm.HS256).
                    compact();
        }catch (ExpiredJwtException e){
            System.out.println("error = " + e.getMessage());
            return e.getMessage();
        }
    }

    public boolean isTokenValid (String token, UserDetails userDetails){
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }


    private Claims extractAllClaims(String token){
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
    }

    private Key getSigningKey() {
        byte[] keyBites = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBites);
    }

    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration
    ) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
}
