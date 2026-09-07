package br.com.flagplatform.user.mapper;

import br.com.flagplatform.user.dto.response.UserResponse;
import br.com.flagplatform.user.entity.UserEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse toResponse(UserEntity entity);

    List<UserResponse> toResponseList(List<UserEntity> entities);

}
