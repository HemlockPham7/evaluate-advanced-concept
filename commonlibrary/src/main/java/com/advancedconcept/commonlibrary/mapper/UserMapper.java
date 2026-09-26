package com.advancedconcept.commonlibrary.mapper;

import com.advancedconcept.commonlibrary.dto.record.UserRequest;
import com.advancedconcept.commonlibrary.dto.record.UserResponse;
import com.advancedconcept.commonlibrary.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    UserResponse toResponse(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "name", target = "name")
    User toEntity(UserRequest request);
}
