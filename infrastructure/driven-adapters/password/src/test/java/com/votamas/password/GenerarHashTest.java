package com.votamas.password;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GenerarHashTest {

    @Test
    void generarHash() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hash = encoder.encode("sinapi2");
        System.out.println("HASH GENERADO: " + hash);
    }
}
