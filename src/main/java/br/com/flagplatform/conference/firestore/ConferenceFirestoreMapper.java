package br.com.flagplatform.conference.firestore;

import br.com.flagplatform.conference.entity.ConferenceEntity;
import org.mapstruct.Mapper;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Mapeia {@link ConferenceEntity} (JPA) ↔ {@link ConferenceFirestoreDocument}
 * (espelho Firestore, ADR-006). Conversões próprias do domínio, em campos camelCase.
 *
 * <p>UUIDs e {@link LocalDateTime} viram {@code String} para manter o documento
 * simples e JSON-friendly — ver {@link ConferenceFirestoreDocument}.
 */
@Mapper(componentModel = "spring")
public interface ConferenceFirestoreMapper {

    ConferenceFirestoreDocument toDocument(ConferenceEntity entity);

    ConferenceEntity toEntity(ConferenceFirestoreDocument document);

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