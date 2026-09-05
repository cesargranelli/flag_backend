package br.com.flagplatform.competition.firestore;

import java.util.HashMap;
import java.util.Map;

/**
 * Documento próprio do domínio Competition no Firestore (ADR-006) — espelho da
 * entidade JPA {@code CompetitionEntity} em campos camelCase. NUNCA persistir a
 * entidade JPA diretamente: o espelho usa este mapa próprio, estável para os apps
 * (referee_app/public_app) que lerem a coleção {@code competitions} em realtime.
 *
 * <p>Tipos serializados (mapa simples, compatível com JSON):
 * <ul>
 *   <li>{@code id}/{@code organizationId}/{@code createdBy}/{@code updatedBy}: UUID como
 *       {@code String};</li>
 *   <li>{@code modality}/{@code gender}/{@code ageGroup}/{@code status}/{@code groupingType}:
 *       código do enum (mesmo valor da coluna VARCHAR no PostgreSQL);</li>
 *   <li>{@code startDate}/{@code endDate}: {@code LocalDate} como ISO-8601
 *       ({@code yyyy-MM-dd});</li>
 *   <li>{@code createdAt}/{@code updatedAt}: {@code LocalDateTime} como ISO-8601
 *       ({@code LocalDateTime.toString()});</li>
 *   <li>demais campos: {@code String} ou ausentes quando {@code null}.</li>
 * </ul>
 *
 * <p>Campos {@code null} são omitidos do documento ({@link #toMap()}) — o Firestore
 * não armazena {@code null} e o {@code set(Map)} substitui o documento inteiro,
 * removendo campos que deixaram de existir. A omissão consistente de {@code null}
 * também garante a idempotência do backfill ({@code expected.equals(current)}).
 *
 * @param id             id UUID do campeonato ({@code UUID.toString()})
 * @param organizationId id da organização dona ({@code UUID.toString()})
 * @param modality       modalidade (código)
 * @param gender         gênero (código)
 * @param ageGroup       faixa etária (código)
 * @param name           nome do campeonato
 * @param description    descrição (opcional)
 * @param startDate      data de início (ISO-8601)
 * @param endDate        data de fim (ISO-8601)
 * @param status         status do campeonato (código)
 * @param groupingType   rótulo do agrupamento (código, opcional)
 * @param season         temporada
 * @param createdAt      data de criação (ISO-8601)
 * @param updatedAt      data da última atualização (ISO-8601)
 * @param createdBy      id do usuário que criou ({@code UUID.toString()})
 * @param updatedBy      id do usuário que atualizou ({@code UUID.toString()})
 */
public record CompetitionFirestoreDocument(
        String id,
        String organizationId,
        String modality,
        String gender,
        String ageGroup,
        String name,
        String description,
        String startDate,
        String endDate,
        String status,
        String groupingType,
        String season,
        String createdAt,
        String updatedAt,
        String createdBy,
        String updatedBy
) {

    /**
     * Converte o snapshot lido do Firestore (mapa) de volta para o documento.
     * Campos ausentes viram {@code null}.
     */
    public static CompetitionFirestoreDocument fromMap(Map<String, Object> data) {
        if (data == null || data.isEmpty()) {
            return null;
        }
        return new CompetitionFirestoreDocument(
                (String) data.get("id"),
                (String) data.get("organizationId"),
                (String) data.get("modality"),
                (String) data.get("gender"),
                (String) data.get("ageGroup"),
                (String) data.get("name"),
                (String) data.get("description"),
                (String) data.get("startDate"),
                (String) data.get("endDate"),
                (String) data.get("status"),
                (String) data.get("groupingType"),
                (String) data.get("season"),
                (String) data.get("createdAt"),
                (String) data.get("updatedAt"),
                (String) data.get("createdBy"),
                (String) data.get("updatedBy"));
    }

    /**
     * Converte o documento para o mapa gravado no Firestore, omitindo campos
     * {@code null} (o Firestore não armazena null e o {@code set(Map)} sobrescreve
     * o documento inteiro, removendo campos que deixaram de existir).
     */
    public Map<String, Object> toMap() {
        Map<String, Object> data = new HashMap<>();
        putIfNotNull(data, "id", id);
        putIfNotNull(data, "organizationId", organizationId);
        putIfNotNull(data, "modality", modality);
        putIfNotNull(data, "gender", gender);
        putIfNotNull(data, "ageGroup", ageGroup);
        putIfNotNull(data, "name", name);
        putIfNotNull(data, "description", description);
        putIfNotNull(data, "startDate", startDate);
        putIfNotNull(data, "endDate", endDate);
        putIfNotNull(data, "status", status);
        putIfNotNull(data, "groupingType", groupingType);
        putIfNotNull(data, "season", season);
        putIfNotNull(data, "createdAt", createdAt);
        putIfNotNull(data, "updatedAt", updatedAt);
        putIfNotNull(data, "createdBy", createdBy);
        putIfNotNull(data, "updatedBy", updatedBy);
        return data;
    }

    private static void putIfNotNull(Map<String, Object> data, String key, String value) {
        if (value != null) {
            data.put(key, value);
        }
    }
}