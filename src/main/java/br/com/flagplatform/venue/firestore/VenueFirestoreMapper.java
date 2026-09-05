package br.com.flagplatform.venue.firestore;

import br.com.flagplatform.venue.entity.VenueEntity;
import org.mapstruct.Mapper;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Mapeia {@link VenueEntity} (JPA) → {@link VenueFirestoreDocument}
 * (espelho Firestore, ADR-006). Conversões próprias do domínio, em campos camelCase.
 *
 * <p>UUIDs e {@link LocalDateTime} viram {@code String} para manter o documento
 * simples e JSON-friendly — ver {@link VenueFirestoreDocument}.
 */
@Mapper(componentModel = "spring")
public interface VenueFirestoreMapper {

    VenueFirestoreDocument toDocument(VenueEntity entity);

    default String map(UUID value) {
        return value == null ? null : value.toString();
    }

    default String map(LocalDateTime value) {
        return value == null ? null : value.toString();
    }
}