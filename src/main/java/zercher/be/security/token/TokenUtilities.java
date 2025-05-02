package zercher.be.security.token;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import static zercher.be.security.token.AuthenticationTokenFilter.HEADER_PREFIX;

@Slf4j
@Component
public class TokenUtilities {
    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${com.zercher.be.tokenSecret}")
    private String tokenSecret;

    @Value("${com.zercher.be.tokenExpirationMs}")
    private Long tokenExpirationMs;

    public String generateToken(UserDetails userDetails) {
        var issuedDate = new Date();
        var expirationDate = new Date(issuedDate.getTime() + tokenExpirationMs);
        var algorithm = getAlgorithm();

        return JWT.create()
                .withSubject(userDetails.getUsername())
                .withIssuedAt(issuedDate)
                .withExpiresAt(expirationDate)
                .sign(algorithm);
    }

    public String getUsernameFromToken(String token) {
        if (token.startsWith(HEADER_PREFIX)) {
            token = token.substring(HEADER_PREFIX.length());
        }

        var decodedToken = decodeToken(token);

        if (decodedToken != null) {
            return decodedToken.getSubject();
        }
        return null;
    }

    public boolean isTokenValid(String authToken) {
        var token = decodeToken(authToken);
        return (token != null);
    }

    private Algorithm getAlgorithm() {
        return Algorithm.HMAC512(tokenSecret);
    }

    private DecodedJWT decodeToken(String token) {
        try {
            var algorithm = getAlgorithm();
            var verifier = JWT.require(algorithm).withIssuer(applicationName).build();
            return verifier.verify(token);
        } catch (JWTVerificationException exception) {
            log.error(exception.getMessage());
            return null;
        }

    }
}
