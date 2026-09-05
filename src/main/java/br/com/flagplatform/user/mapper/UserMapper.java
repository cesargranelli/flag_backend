package br.com.flagplatform.user.mapper;

import br.com.flagplatform.user.dto.request.RegisterRequest;
import br.com.flagplatform.user.dto.response.UserResponse;
import br.com.flagplatform.user.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "passwordHash", source = "password")
    @Mapping(target = "firebaseUid", ignore = true)
    @Mapping(target = "skills", ignore = true)
    @Mapping(target = "organizationId", ignore = true)
    @Mapping(target = "clubId", ignore = true)
    UserEntity toEntity(RegisterRequest request);

    @Mapping(target = "firebaseUid", source = "entity.firebaseUid")
    @Mapping(target = "skills", source = "entity.skills")
    @Mapping(target = "organizationId", source = "entity.organizationId")
    @Mapping(target = "clubId", source = "entity.clubId")
    UserResponse toResponse(UserEntity entity);

    List<UserResponse> toResponseList(List<UserEntity> entities);

}
