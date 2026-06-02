package com.votamas.jwt;

import com.votamas.model.auth.UserPermission;
import com.votamas.model.auth.gateways.TokenRepository;
import com.votamas.model.user.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JwtAdapter implements TokenRepository {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expiration;

    @Override
    public String generateAccessToken(User user, List<UserPermission> userPermissions) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.id());
        claims.put("rolesPermissions", userPermissions);

        return Jwts.builder()
                .subject(user.email())
                .claims(claims)
                .issuedAt(new Date())
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

}
