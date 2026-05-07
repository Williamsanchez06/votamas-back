package com.votamas.r2dbc.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;

import java.util.UUID;

@Builder
@Table(schema = "vota_mas", name = "users")
public record UserData(
        @Id @Column(name = "user_id") UUID id,
        @Column(name = "name") String name,
        @Column(name = "surname") String surname,
        @Column(name = "email") String email
        ) {
}