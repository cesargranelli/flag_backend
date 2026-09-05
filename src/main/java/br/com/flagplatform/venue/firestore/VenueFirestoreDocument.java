package br.com.flagplatform.venue.firestore;

import java.util.HashMap;
import java.util.Map;

/**
 * Documento próprio do domínio Venue no Firestore (ADR-006) — espelho da
 * entidade JPA {@code VenueEntity} em campos camelCase. NUNCA persistir a
 * entidade JPA diretamente: o espelho usa este mapa próprio, estável para os apps
 * (referee_app/public_app) que lerem a coleção {@code venues} em realtime.
 *
 * <p>Tipos serializados (mapa simples, compatível com JSON):
 * <ul>
 *   <li>{@code id}/{@code createdBy}/{@code updatedBy}: UUID como {@code String};</li>
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
 * @param id        id UUID do venue ({@code UUID.toString()})
 * @param name      nome do venue
 * @param address   endereço (opcional)
 * @param mapsUrl   URL do Google Maps (opcional)
 * @param createdAt data de criação (ISO-8601)
 * @param updatedAt data da última atualização (ISO-8601)
 * @param createdBy id do usuário que criou ({@code UUID.toString()})
 * @param updatedBy id do usuário que atualizou ({@code UUID.toString()})
 */
public record VenueFirestoreDocument(
        String id,
        String name,
        String address,
        String mapsUrl,
        String createdAt,
        String updatedAt,
        String createdBy,
        String updatedBy
) {

    /**
     * Converte o snapshot lido do Firestore (mapa) de volta para o documento.
     * Campos ausentes viram {@code null}.
     */
    public static VenueFirestoreDocument fromMap(Map<String, Object> data) {
        if (data == null || data.isEmpty()) {
            return null;
        }
        return new VenueFirestoreDocument(
                (String) data.get("id"),
                (String) data.get("name"),
                (String) data.get("address"),
                (String) data.get("mapsUrl"),
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
        putIfNotNull(data, "name", name);
        putIfNotNull(data, "address", address);
        putIfNotNull(data, "mapsUrl", mapsUrl);
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