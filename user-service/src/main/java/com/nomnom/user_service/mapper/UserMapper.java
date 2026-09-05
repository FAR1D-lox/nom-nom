package com.nomnom.user_service.mapper;

import com.nomnom.user_service.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "role", target = "role")
    ResponseUserProfileDto toUserProfileDto(UserEntity userEntity);

}
