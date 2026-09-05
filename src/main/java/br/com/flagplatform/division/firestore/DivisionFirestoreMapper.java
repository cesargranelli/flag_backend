package br.com.flagplatform.division.firestore;

import br.com.flagplatform.division.entity.DivisionEntity;
import org.mapstruct.Mapper;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Mapeia {@link DivisionEntity} (JPA) ↔ {@link DivisionFirestoreDocument}
 * (espelho Firestore, ADR-006). Conversões próprias do domínio, em campos camelCase.
 *
 * <p>UUIDs e {@link LocalDateTime} viram {@code String} para manter o documento
 * simples e JSON-friendly — ver {@link DivisionFirestoreDocument}.
 */
@Mapper(componentModel = "spring")
public interface DivisionFirestoreMapper {

    DivisionFirestoreDocument toDocument(DivisionEntity entity);

    DivisionEntity toEntity(DivisionFirestoreDocument document);

    // entity → documento
    default String map(UUID value) {
        return value == null ? null : value.toString();
    }

    default String map(LocalDateTime value) {
        return value == null ? null : value.toString();
    }

    // documento → entity
    default UUID mapUuid(String value) {
        return value == null ? null : UUID.fromString(value);
    }

    default LocalDateTime mapLocalDateTime(String value) {
        return value == null ? null : LocalDateTime.parse(value);
    }
}