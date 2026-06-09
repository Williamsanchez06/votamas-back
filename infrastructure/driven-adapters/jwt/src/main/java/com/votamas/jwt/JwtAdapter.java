package com.votamas.jwt;

import com.votamas.model.auth.AuthenticatedUser;
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

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expiration;

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
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
                .flatMap(up -> Arrays.stream(up.permissions().split(",")))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .collect(Collectors.toSet());

        return Jwts.builder()
                .subject(user.email())
                .claim("userId", user.id().toString())
                .claim("roles", roles)
                .claim("authorities", authorities)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getKey())
                .compact();
    }
}
