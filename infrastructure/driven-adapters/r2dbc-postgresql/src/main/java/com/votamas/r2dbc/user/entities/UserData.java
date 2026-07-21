package com.votamas.r2dbc.user.entities;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import java.util.UUID;

@Builder (toBuilder = true)
@Table(name = "users")
public record UserData(
        @Id @Column("user_id") UUID id,
        @Column("name") String name,
        @Column("surname") String surname,
        @Column("email") String email,
        @Column("password") String password,
        @Column("active") Boolean active
) {
}
