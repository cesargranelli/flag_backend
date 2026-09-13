package br.com.flagplatform.person.mapper;

import br.com.flagplatform.person.dto.request.CreatePersonRequest;
import br.com.flagplatform.person.dto.request.UpdatePersonRequest;
import br.com.flagplatform.person.dto.response.PersonResponse;
import br.com.flagplatform.person.entity.PersonEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PersonMapper {

    PersonEntity toEntity(CreatePersonRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    PersonEntity updateEntity(
            @MappingTarget PersonEntity entity,
            UpdatePersonRequest request);

    PersonResponse toResponse(PersonEntity entity);

    List<PersonResponse> toResponseList(List<PersonEntity> entities);

}
