package org.example.springcloudapigateway;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Component;

import java.util.Date;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

@Component
public class JwtTokenProvider {

//    @Value("${jwt.secret}")
    private String jwtSecret = "test";

//    @Value("${jwt.expiration}")
    private long jwtExpirationInMs = 3600000;
    public String generateToken(String msisdn,String code) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return JWT.create()
                .withIssuedAt(new Date()).withSubject(msisdn)
                .withClaim("code", code)
                .withExpiresAt(expiryDate)
                .sign(HMAC512(jwtSecret));
    }

    public boolean validateToken(String authToken) {
        Algorithm algorithm = Algorithm.HMAC512(jwtSecret);
        try {
            JWTVerifier verifier = JWT.require(algorithm)
                    .build();
            DecodedJWT jwt = verifier.verify(authToken);
            if (jwt != null) {
                return true;
            }
            return true;
        } catch (Exception ex) {
        }

        return false;
    }
}
