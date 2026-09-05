package br.com.flagplatform.conference.firestore;

import java.util.HashMap;
import java.util.Map;

/**
 * Documento próprio do domínio Conference no Firestore (ADR-006) — espelho da
 * entidade JPA {@code ConferenceEntity} em campos camelCase. NUNCA persistir a
 * entidade JPA diretamente: o espelho usa este mapa próprio, estável para os apps
 * (referee_app/public_app) que lerem a coleção {@code conferences} em realtime.
 *
 * <p>Modelagem <b>flat</b> (ADR-006): coleção própria no nível raiz, documento
 * identificado pelo {@code UUID.toString()} da entidade e o relacionamento expresso
 * por campo de referência ({@code competitionId}) — sem subcoleções aninhadas.
 *
 * <p>Tipos serializados (mapa simples, compatível com JSON):
 * <ul>
 *   <li>{@code id}/{@code competitionId}/{@code createdBy}/{@code updatedBy}: UUID como
 *       {@code String};</li>
 *   <li>{@code createdAt}/{@code updatedAt}: {@code LocalDateTime} como ISO-8601
 *       ({@code LocalDateTime.toString()});</li>
 *   <li>demais campos: {@code String}.</li>
 * </ul>
 *
 * <p>Campos {@code null} são omitidos do documento ({@link #toMap()}) — o Firestore
 * não armazena {@code null} e o {@code set(Map)} substitui o documento inteiro,
 * removendo campos que deixaram de existir. A omissão consistente de {@code null}
 * também garante a idempotência do backfill ({@code expected.equals(current)}).
 *
 * @param id             id UUID da conferência ({@code UUID.toString()})
 * @param competitionId  id do campeonato dono ({@code UUID.toString()})
 * @param name           nome da conferência
 * @param createdAt      data de criação (ISO-8601)
 * @param updatedAt      data da última atualização (ISO-8601)
 * @param createdBy      id do usuário que criou ({@code UUID.toString()})
 * @param updatedBy      id do usuário que atualizou ({@code UUID.toString()})
 */
public record ConferenceFirestoreDocument(
        String id,
        String competitionId,
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
    public static ConferenceFirestoreDocument fromMap(Map<String, Object> data) {
        if (data == null || data.isEmpty()) {
            return null;
        }
        return new ConferenceFirestoreDocument(
                (String) data.get("id"),
                (String) data.get("competitionId"),
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