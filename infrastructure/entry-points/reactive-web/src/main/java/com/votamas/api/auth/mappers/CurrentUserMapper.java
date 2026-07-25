package com.votamas.api.auth.mappers;

import com.votamas.api.auth.dtos.CurrentUserResponseDTO;
import com.votamas.api.auth.dtos.UserModuleResponseDTO;
import com.votamas.model.auth.CurrentUserProfile;
import com.votamas.model.auth.UserModule;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface CurrentUserMapper {
    CurrentUserResponseDTO toResponse(CurrentUserProfile profile);

    UserModuleResponseDTO toResponse(UserModule module);
}
