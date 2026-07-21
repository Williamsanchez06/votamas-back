package com.votamas.password;

import com.votamas.model.auth.gateways.PasswordHasher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
public class PasswordAdapter implements PasswordHasher {

    private final PasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public Mono<String> hash(String password) {
        return Mono.fromCallable(() -> encoder.encode(password))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Boolean> matches(String rawPassword, String hashedPassword) {
        return Mono.fromCallable(() -> encoder.matches(rawPassword, hashedPassword))
                .subscribeOn(Schedulers.boundedElastic());
    }
}
