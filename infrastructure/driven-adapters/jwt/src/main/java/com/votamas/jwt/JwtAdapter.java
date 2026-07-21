package com.votamas.jwt;

import com.votamas.model.auth.UserPermission;
import com.votamas.model.auth.gateways.TokenProvider;
import com.votamas.model.user.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtAdapter implements TokenProvider {

    private final SecretKey secretKey;
    private final long expiration;
    private final String issuer;

    public JwtAdapter(@Value("${jwt.secret}") String secretKey,
                      @Value("${jwt.expiration}") long expiration,
                      @Value("${jwt.issuer}") String issuer) {
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.expiration = expiration;
        this.issuer = issuer;
    }

    @Override
    public String generateAccessToken(User user, List<UserPermission> userPermissions) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        Set<String> roles = userPermissions.stream()
                .map(UserPermission::role)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Set<String> authorities = userPermissions.stream()
                .map(UserPermission::permission)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(permission -> !permission.isBlank())
                .collect(Collectors.toSet());

        return Jwts.builder()
                .subject(user.email())
                .claim("userId", user.id().toString())
                .claim("roles", roles)
                .claim("authorities", authorities)
                .issuer(issuer)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }
}
