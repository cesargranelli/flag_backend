package br.com.flagplatform.division.firestore;

import java.util.HashMap;
import java.util.Map;

/**
 * Documento próprio do domínio Division no Firestore (ADR-006) — espelho da
 * entidade JPA {@code DivisionEntity} em campos camelCase. NUNCA persistir a
 * entidade JPA diretamente: o espelho usa este mapa próprio, estável para os apps
 * (referee_app/public_app) que lerem a coleção {@code divisions} em realtime.
 *
 * <p>Modelagem <b>flat</b> (ADR-006): coleção própria no nível raiz, documento
 * identificado pelo {@code UUID.toString()} da entidade e os relacionamentos
 * expressos por campos de referência ({@code competitionId} e {@code conferenceId})
 * — sem subcoleções aninhadas.
 *
 * <p>Tipos serializados (mapa simples, compatível com JSON):
 * <ul>
 *   <li>{@code id}/{@code competitionId}/{@code conferenceId}/{@code createdBy}/{@code updatedBy}:
 *       UUID como {@code String};</li>
 *   <li>{@code createdAt}/{@code updatedAt}: {@code LocalDateTime} como ISO-8601
 *       ({@code LocalDateTime.toString()});</li>
 *   <li>demais campos: {@code String}.</li>
 * </ul>
 *
 * <p>Campos {@code null} são omitidos do documento ({@link #toMap()}) — o Firestore
 * não armazena {@code null} e o {@code set(Map)} substitui o documento inteiro,
 * removendo campos que deixaram de existir. A omissão consistente de {@code null}
 * (em especial {@code conferenceId} para divisões sem conferência) também garante a
 * idempotência do backfill ({@code expected.equals(current)}).
 *
 * @param id             id UUID da divisão ({@code UUID.toString()})
 * @param competitionId  id do campeonato dono ({@code UUID.toString()})
 * @param conferenceId   id da conferência vinculada, quando houver ({@code UUID.toString()})
 * @param name           nome da divisão
 * @param createdAt      data de criação (ISO-8601)
 * @param updatedAt      data da última atualização (ISO-8601)
 * @param createdBy      id do usuário que criou ({@code UUID.toString()})
 * @param updatedBy      id do usuário que atualizou ({@code UUID.toString()})
 */
public record DivisionFirestoreDocument(
        String id,
        String competitionId,
        String conferenceId,
        String name,
        String createdAt,
        String updatedAt,
        String createdBy,
        String updatedBy
) {

    /**
     * Converte o snapshot lido do Firestore (mapa) de volta para o documento.
     * Campos ausentes viram {@code null}.
     */
    public static DivisionFirestoreDocument fromMap(Map<String, Object> data) {
        if (data == null || data.isEmpty()) {
            return null;
        }
        return new DivisionFirestoreDocument(
                (String) data.get("id"),
                (String) data.get("competitionId"),
                (String) data.get("conferenceId"),
                (String) data.get("name"),
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
        putIfNotNull(data, "competitionId", competitionId);
        putIfNotNull(data, "conferenceId", conferenceId);
        putIfNotNull(data, "name", name);
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