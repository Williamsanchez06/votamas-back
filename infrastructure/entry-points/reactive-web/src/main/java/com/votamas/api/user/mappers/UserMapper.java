package com.votamas.api.user.mappers;

import com.votamas.api.user.dtos.UserCreateRequestDTO;
import com.votamas.api.user.dtos.UserResponseDTO;
import com.votamas.api.user.dtos.UserUpdateRequestDTO;
import com.votamas.model.user.User;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    User toUser(UserCreateRequestDTO request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "active", ignore = true)
    User toUser(UserUpdateRequestDTO request);

    UserResponseDTO toResponse(User user);

}
