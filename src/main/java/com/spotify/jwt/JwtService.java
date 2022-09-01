package com.spotify.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {
    @Value("${jwt.token.expiry.time}")
    private long JWT_EXPIRED_TIME;
    @Value("${jwt.token.secret-key}")
    private String SECRET_KEY;

    @Value("${jwt.token.issuer}")
    private String JWT_ISSUER;

    public String generateToken(UserDetails userDetails){
        return Jwts.builder().setSubject(userDetails.getUsername())
                .setIssuer(JWT_ISSUER)
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plusSeconds(JWT_EXPIRED_TIME)))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public String getUsernameToken(String token){
        Claims claims = getClaims(token);
        return claims.getSubject();
    }

    public Boolean isExpiredToken(String token){
        Claims claims = getClaims(token);
        return claims.getExpiration().before(new Date());
    }

    public Boolean isValidToken(String token){
        return (getUsernameToken(token)!=null && !isExpiredToken(token));
    }

    public Claims getClaims(String token){
        return Jwts.parser().setSigningKey(SECRET_KEY).
                parseClaimsJws(token)
                .getBody();
    }
}
