package br.com.flagplatform.competition.firestore;

import br.com.flagplatform.common.enums.AgeGroup;
import br.com.flagplatform.common.enums.CompetitionStatus;
import br.com.flagplatform.common.enums.Gender;
import br.com.flagplatform.common.enums.GroupingType;
import br.com.flagplatform.common.enums.Modality;
import br.com.flagplatform.competition.entity.CompetitionEntity;
import org.mapstruct.Mapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Mapeia {@link CompetitionEntity} (JPA) ↔ {@link CompetitionFirestoreDocument}
 * (espelho Firestore, ADR-006). Conversões próprias do domínio, em campos camelCase.
 *
 * <p>Enums são gravados pelo {@code code} (mesmo valor da coluna VARCHAR no Postgres,
 * ex.: {@code DRAFT}, {@code FLAG_5X5}, {@code ADULT}); UUIDs, {@link LocalDateTime} e
 * {@link LocalDate} viram {@code String} para manter o documento simples e
 * JSON-friendly — ver {@link CompetitionFirestoreDocument}.
 */
@Mapper(componentModel = "spring")
public interface CompetitionFirestoreMapper {

    CompetitionFirestoreDocument toDocument(CompetitionEntity entity);

    CompetitionEntity toEntity(CompetitionFirestoreDocument document);

    // entity → documento
    default String map(UUID value) {
        return value == null ? null : value.toString();
    }

    default String map(Modality value) {
        return value == null ? null : value.getCode();
    }

    default String map(Gender value) {
        return value == null ? null : value.getCode();
    }

    default String map(AgeGroup value) {
        return value == null ? null : value.getCode();
    }

    default String map(CompetitionStatus value) {
        return value == null ? null : value.getCode();
    }

    default String map(GroupingType value) {
        return value == null ? null : value.getCode();
    }

    default String map(LocalDate value) {
        return value == null ? null : value.toString();
    }

    default String map(LocalDateTime value) {
        return value == null ? null : value.toString();
    }

    // documento → entity
    default UUID mapUuid(String value) {
        return value == null ? null : UUID.fromString(value);
    }

    default Modality mapModality(String value) {
        return value == null ? null : Modality.valueOf(value);
    }

    default Gender mapGender(String value) {
        return value == null ? null : Gender.valueOf(value);
    }

    default AgeGroup mapAgeGroup(String value) {
        return value == null ? null : AgeGroup.valueOf(value);
    }

    default CompetitionStatus mapCompetitionStatus(String value) {
        return value == null ? null : CompetitionStatus.valueOf(value);
    }

    default GroupingType mapGroupingType(String value) {
        return value == null ? null : GroupingType.valueOf(value);
    }

    default LocalDate mapLocalDate(String value) {
        return value == null ? null : LocalDate.parse(value);
    }

    default LocalDateTime mapLocalDateTime(String value) {
        return value == null ? null : LocalDateTime.parse(value);
    }
}