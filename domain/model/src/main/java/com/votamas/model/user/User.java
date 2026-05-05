package com.votamas.model.user;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {

    private UUID id;
    private String name;
    private String surname;
    private String email;
    private String password;

}
