package com.example.bankcards.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.bankcards.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Date;

@Component
public class JWTUtil {

    @Value("${jwt_secret}")
    private String secretWord;
    @Value("${token_issuer}")
    private String issuer;
    @Value("${jwt_days_until_expiration}")
    private int expirationDays;
    private final String subject = "User details";

    public String generateToken(User user){
        Date expirationDate = Date.from(ZonedDateTime.now().plusDays(expirationDays).toInstant());

        return JWT.create()
                .withSubject(subject)
                .withClaim("login", user.getLogin())
                .withClaim("role", user.getRole().getRole())
                .withIssuer(issuer)
                .withExpiresAt(expirationDate)
                .sign(Algorithm.HMAC256(secretWord));
    }

    public String validateTokenAndRetrieveClaim(String token) throws JWTVerificationException {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secretWord))
                .withSubject(subject)
                .withIssuer(issuer)
                .build();
        DecodedJWT jwt = verifier.verify(token);

        return jwt.getClaim("login").asString();
    }
}
