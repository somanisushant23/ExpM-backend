package com.userservice.expmbff.configs;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

public class JwtUtils {

    @Value("${user.secret.key}")
    private String SECRET_KEY;

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    public boolean validateToken(String token, String headerEmail) {
        logger.info("Validating token");
        try{
            Jwts.parser().verifyWith(getSigningKey()).build().parseClaimsJws(token);
            //extract email from token
            String tokenEmail = getPayloadEmail(token);
            if(tokenEmail.equalsIgnoreCase(headerEmail)) {
                return true;
            } else return false;
        } catch (Exception ex) {
            logger.error("Error validating token: {} with error {}", token, ex.getMessage());
            return false;
        }
    }

    public String createToken(String email, Map<String, String> claims) {

        logger.info("Creating token for email {}", email);
        String jwt = Jwts.builder()
                .header().keyId(email)
                .and()
                .subject(email)
                .claims(claims)
                .issuedAt(new Date(System.currentTimeMillis()))
                .issuer("User Service")
                .encodePayload(true)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
        return jwt;
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY));
    }

    private String getPayloadEmail(String token) {
        return getClaimsSubject(token);
    }

    private String getClaimsSubject(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // New helper to parse all claims from a JWT
    public Claims parseClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // New helper to expose subject (email) publicly
    public String getSubject(String token) {
        return getClaimsSubject(token);
    }
}
