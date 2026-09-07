package br.com.flagplatform.club.mapper;

import br.com.flagplatform.club.dto.request.CreateClubRequest;
import br.com.flagplatform.club.dto.request.UpdateClubRequest;
import br.com.flagplatform.club.dto.response.ClubResponse;
import br.com.flagplatform.club.entity.ClubEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ClubMapper {

    ClubEntity toEntity(CreateClubRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    ClubEntity updateEntity(
            @MappingTarget ClubEntity entity,
            UpdateClubRequest request);

    ClubResponse toResponse(ClubEntity entity);

    List<ClubResponse> toResponseList(List<ClubEntity> entities);

}
