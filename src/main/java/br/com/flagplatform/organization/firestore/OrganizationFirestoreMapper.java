package br.com.flagplatform.organization.firestore;

import br.com.flagplatform.common.enums.DocumentType;
import br.com.flagplatform.common.enums.OrganizationStatus;
import br.com.flagplatform.common.enums.OrganizationType;
import br.com.flagplatform.organization.entity.OrganizationEntity;
import org.mapstruct.Mapper;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Mapeia {@link OrganizationEntity} (JPA) ↔ {@link OrganizationFirestoreDocument}
 * (espelho Firestore, ADR-006). Conversões próprias do domínio, em campos camelCase.
 *
 * <p>Enums são gravados pelo {@code code} (mesmo valor da coluna VARCHAR no Postgres,
 * ex.: {@code CLUB}, {@code ACTIVE}, {@code CNPJ}); UUIDs e {@link LocalDateTime}
 * viram {@code String} para manter o documento simples e JSON-friendly — ver
 * {@link OrganizationFirestoreDocument}.
 */
@Mapper(componentModel = "spring")
public interface OrganizationFirestoreMapper {

    OrganizationFirestoreDocument toDocument(OrganizationEntity entity);

    OrganizationEntity toEntity(OrganizationFirestoreDocument document);

    // entity → documento
    default String map(UUID value) {
        return value == null ? null : value.toString();
    }

    default String map(OrganizationType value) {
        return value == null ? null : value.getCode();
    }

    default String map(OrganizationStatus value) {
        return value == null ? null : value.getCode();
    }

    default String map(DocumentType value) {
        return value == null ? null : value.getCode();
    }

    default String map(LocalDateTime value) {
        return value == null ? null : value.toString();
    }

    // documento → entity
    default UUID mapUuid(String value) {
        return value == null ? null : UUID.fromString(value);
    }

    default OrganizationType mapOrganizationType(String value) {
        return value == null ? null : OrganizationType.valueOf(value);
    }

    default OrganizationStatus mapOrganizationStatus(String value) {
        return value == null ? null : OrganizationStatus.valueOf(value);
    }

    default DocumentType mapDocumentType(String value) {
        return value == null ? null : DocumentType.valueOf(value);
    }

    default LocalDateTime mapLocalDateTime(String value) {
        return value == null ? null : LocalDateTime.parse(value);
    }
}