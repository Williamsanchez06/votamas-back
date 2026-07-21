package com.votamas.api.auth.mappers;

import com.votamas.api.auth.dtos.LoginRequest;
import com.votamas.api.auth.dtos.LoginResponse;
import com.votamas.model.auth.Login;
import com.votamas.model.auth.Token;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface LoginMapper {

    @Mapping(source = "email", target = "email")
    @Mapping(source = "password", target = "password")
    Login toLogin(LoginRequest loginRequest);

    @Mapping(source = "accessToken", target = "token")
    LoginResponse toLoginDTO(Token token);

}
