package br.com.flagplatform.common.converter;

import br.com.flagplatform.common.converter.abstration.PersistableEnumConverter;
import br.com.flagplatform.common.enums.OrganizationType;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Converter(autoApply = true)
public class OrganizationTypeConverter
        extends PersistableEnumConverter<OrganizationType> {

    public OrganizationTypeConverter() {
        super(OrganizationType.class);
    }

    @Override
    public OrganizationType convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }

        for (OrganizationType constant : OrganizationType.values()) {
            if (constant.getCode().equals(dbData)) {
                return constant;
            }
        }

        log.warn("Valor desconhecido ou legado '{}' encontrado para OrganizationType no banco de dados. Retornando null.", dbData);
        return null;
    }

}
