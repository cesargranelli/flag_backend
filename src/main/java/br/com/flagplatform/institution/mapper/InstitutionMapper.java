package br.com.flagplatform.institution.mapper;

import br.com.flagplatform.institution.dto.response.InstitutionResponse;
import br.com.flagplatform.institution.entity.InstitutionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Arrays;
import java.util.List;

@Mapper(componentModel = "spring")
public interface InstitutionMapper {

    @Mapping(target = "colors", expression = "java(toList(entity.getColors()))")
    @Mapping(target = "organizations", ignore = true)
    InstitutionResponse toResponse(InstitutionEntity entity, List<java.util.UUID> organizations);

    default List<String> toList(String[] colors) {
        return colors == null ? List.of() : Arrays.asList(colors);
    }
}
