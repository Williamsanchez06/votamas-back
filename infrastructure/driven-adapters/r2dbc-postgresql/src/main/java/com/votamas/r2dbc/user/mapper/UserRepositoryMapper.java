package com.votamas.r2dbc.user.mapper;

import com.votamas.model.user.User;
import com.votamas.r2dbc.user.entities.UserData;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface UserRepositoryMapper {

    UserData toUserData(User user);

    User toUser(UserData userData);

}
