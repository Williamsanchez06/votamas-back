package com.votamas.api.mappers;

import com.votamas.api.dtos.LoginRequest;
import com.votamas.api.dtos.LoginResponse;
import com.votamas.model.auth.Login;
import com.votamas.model.auth.Token;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface LoginMapper {

    LoginMapper INSTANCE = Mappers.getMapper(LoginMapper.class);

    @Mapping(source = "email", target = "email")
    @Mapping(source = "password", target = "password")
    Login toLogin(LoginRequest loginRequest);

    @Mapping(source = "accessToken", target = "token")
    LoginResponse toLoginDTO (Token token);

}
