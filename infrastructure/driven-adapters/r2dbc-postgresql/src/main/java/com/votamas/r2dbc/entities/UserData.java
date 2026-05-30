package com.votamas.r2dbc.entities;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import java.util.UUID;

@Builder
@Table(schema = "vota_mas", name = "users")
public record UserData(
        @Id @Column("user_id") UUID id,  // campo Java = "id", columna BD = "user_id"
        @Column("name") String name,
        @Column("surname") String surname,
        @Column("email") String email,
        @Column("password") String password
) {
}