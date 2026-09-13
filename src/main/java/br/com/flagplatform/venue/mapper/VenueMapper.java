package br.com.flagplatform.venue.mapper;

import br.com.flagplatform.venue.dto.request.CreateVenueRequest;
import br.com.flagplatform.venue.dto.request.UpdateVenueRequest;
import br.com.flagplatform.venue.dto.response.VenueResponse;
import br.com.flagplatform.venue.entity.VenueEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface VenueMapper {

    VenueEntity toEntity(CreateVenueRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    VenueEntity updateEntity(
            @MappingTarget VenueEntity entity,
            UpdateVenueRequest request);

    @Mapping(source = "organizationId", target = "organizationId")
    VenueResponse toResponse(VenueEntity entity);

    List<VenueResponse> toResponseList(List<VenueEntity> entities);

}
