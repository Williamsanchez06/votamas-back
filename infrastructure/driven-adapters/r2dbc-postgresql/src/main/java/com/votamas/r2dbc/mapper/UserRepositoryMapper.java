package com.votamas.r2dbc.mapper;

import com.votamas.model.user.User;
import com.votamas.r2dbc.entities.UserData;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface UserRepositoryMapper {

    UserData toUserData(User user);
    User toUser(UserData userData);

}
