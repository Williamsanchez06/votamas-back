package com.votamas.api.user.mappers;

import com.votamas.api.user.dtos.UserRequestDTO;
import com.votamas.api.user.dtos.UserResponseDTO;
import com.votamas.model.user.User;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    User toUser(UserRequestDTO request);

    UserResponseDTO toResponse(User user);

}
