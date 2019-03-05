package com.loya.devi.security;

import com.loya.devi.service.Impl.security.component.UserDetailsImpl;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * generate a JWT token
 * validate a JWT token
 * parse username from JWT token
 *
 * @author ilya.korzhavin
 */
@Component
public class JwtProvider {

    @Autowired
    Environment environment;

    public static final String INVALID_JWT_SIGNATURE_MESSAGE = "Invalid JWT signature -> Message: {} ";
    public static final String INVALID_JWT_TOKEN_MESSAGE = "Invalid JWT token -> Message: {}";
    public static final String EXPIRED_JWT_TOKEN_MESSAGE = "Expired JWT token -> Message: {}";
    public static final String UNSUPPORTED_JWT_TOKEN_MESSAGE = "Unsupported JWT token -> Message: {}";
    public static final String JWT_CLAIMS_STRING_IS_EMPTY_MESSAGE = "JWT claims string is empty -> Message: {}";

    private final String JWT_SECRET = "jwtBrolySecretKey";
    private final Integer JWT_EXPIRATION = (1000*600);
    private final Logger LOGGER = LoggerFactory.getLogger(JwtProvider.class);

    @Autowired
    private JwtProvider tokenProvider;

    /**
     * method generate and return token
     *
     * @param authentication Spring Security hold the principal information of each authenticated user in a ThreadLocal
     *                       – represented as an Authentication object.
     * @return token
     */
    public String generateJwtToken(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION))
                .signWith(SignatureAlgorithm.HS512, JWT_SECRET)
                .compact();
    }

    /**
     * method generate refresh token
     *
     * @param token old access token
     * @return refresh token
     */
    public String generateRefreshToken(String token) {
        String username = tokenProvider.getUserNameFromJwtToken(token);
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + JWT_EXPIRATION))
                .signWith(SignatureAlgorithm.HS512, JWT_SECRET)
                .compact();
    }

    /**
     * method return username from token
     *
     * @param token object
     * @return username
     */
    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser()
                .setSigningKey(JWT_SECRET)
                .parseClaimsJws(token)
                .getBody().getSubject();
    }

    /**
     * method validate token
     *
     * @param authToken string obj
     * @return true if all OK
     */
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(JWT_SECRET).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            LOGGER.error(INVALID_JWT_SIGNATURE_MESSAGE, e);
        } catch (MalformedJwtException e) {
            LOGGER.error(INVALID_JWT_TOKEN_MESSAGE, e);
        } catch (ExpiredJwtException e) {
            LOGGER.error(EXPIRED_JWT_TOKEN_MESSAGE, e);
        } catch (UnsupportedJwtException e) {
            LOGGER.error(UNSUPPORTED_JWT_TOKEN_MESSAGE, e);
        } catch (IllegalArgumentException e) {
            LOGGER.error(JWT_CLAIMS_STRING_IS_EMPTY_MESSAGE, e);
        }
        return false;
    }
}