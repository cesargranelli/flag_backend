package br.com.flagplatform.organization.firestore;

import java.util.HashMap;
import java.util.Map;

/**
 * Documento próprio do domínio Organization no Firestore (ADR-006) — espelho da
 * entidade JPA {@code OrganizationEntity} em campos camelCase. NUNCA persistir a
 * entidade JPA diretamente: o espelho usa este mapa próprio, estável para os apps
 * (referee_app/public_app) que lerem a coleção {@code organizations} em realtime.
 *
 * <p>Tipos serializados (mapa simples, compatível com JSON):
 * <ul>
 *   <li>{@code id}/{@code parentId}/{@code createdBy}/{@code updatedBy}: UUID como {@code String};</li>
 *   <li>{@code organizationType}/{@code documentType}/{@code status}: código do enum (mesmo valor
 *       da coluna VARCHAR no PostgreSQL);</li>
 *   <li>{@code createdAt}/{@code updatedAt}: {@code LocalDateTime} como ISO-8601
 *       ({@code LocalDateTime.toString()});</li>
 *   <li>demais campos: {@code String} ou ausentes quando {@code null}.</li>
 * </ul>
 *
 * <p>Campos {@code null} são omitidos do documento ({@link #toMap()}) — o Firestore
 * não armazena {@code null} e o {@code set(Map)} substitui o documento inteiro,
 * removendo campos que deixaram de existir (ex.: {@code parentId} após desassociar).
 *
 * @param id              id UUID da organização ({@code UUID.toString()})
 * @param legalName       razão social
 * @param tradeName       nome fantasia
 * @param abbreviation    sigla
 * @param organizationType tipo da organização (código)
 * @param document        CNPJ/CPF (somente dígitos)
 * @param documentType    tipo do documento (código)
 * @param presidentName   nome do presidente
 * @param presidentCpf    CPF do presidente
 * @param email           e-mail
 * @param phone           telefone
 * @param website         site
 * @param instagram       instagram
 * @param country         país (ISO 3166-1 alpha-2)
 * @param state           estado
 * @param city            cidade
 * @param logoUrl         URL do logo
 * @param primaryColor    cor primária (hex)
 * @param secondaryColor  cor secundária (hex)
 * @param tertiaryColor   cor terciária (hex)
 * @param quaternaryColor cor quaternária (hex)
 * @param timezone        fuso horário
 * @param locale          locale
 * @param status          status da organização (código)
 * @param parentId        id da organização mãe ({@code UUID.toString()})
 * @param createdAt       data de criação (ISO-8601)
 * @param updatedAt       data da última atualização (ISO-8601)
 * @param createdBy       id do usuário que criou ({@code UUID.toString()})
 * @param updatedBy       id do usuário que atualizou ({@code UUID.toString()})
 */
public record OrganizationFirestoreDocument(
        String id,
        String legalName,
        String tradeName,
        String abbreviation,
        String organizationType,
        String document,
        String documentType,
        String presidentName,
        String presidentCpf,
        String email,
        String phone,
        String website,
        String instagram,
        String country,
        String state,
        String city,
        String logoUrl,
        String primaryColor,
        String secondaryColor,
        String tertiaryColor,
        String quaternaryColor,
        String timezone,
        String locale,
        String status,
        String parentId,
        String createdAt,
        String updatedAt,
        String createdBy,
        String updatedBy
) {

    /**
     * Converte o snapshot lido do Firestore (mapa) de volta para o documento.
     * Campos ausentes viram {@code null}.
     */
    public static OrganizationFirestoreDocument fromMap(Map<String, Object> data) {
        if (data == null || data.isEmpty()) {
            return null;
        }
        return new OrganizationFirestoreDocument(
                (String) data.get("id"),
                (String) data.get("legalName"),
                (String) data.get("tradeName"),
                (String) data.get("abbreviation"),
                (String) data.get("organizationType"),
                (String) data.get("document"),
                (String) data.get("documentType"),
                (String) data.get("presidentName"),
                (String) data.get("presidentCpf"),
                (String) data.get("email"),
                (String) data.get("phone"),
                (String) data.get("website"),
                (String) data.get("instagram"),
                (String) data.get("country"),
                (String) data.get("state"),
                (String) data.get("city"),
                (String) data.get("logoUrl"),
                (String) data.get("primaryColor"),
                (String) data.get("secondaryColor"),
                (String) data.get("tertiaryColor"),
                (String) data.get("quaternaryColor"),
                (String) data.get("timezone"),
                (String) data.get("locale"),
                (String) data.get("status"),
                (String) data.get("parentId"),
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
        putIfNotNull(data, "legalName", legalName);
        putIfNotNull(data, "tradeName", tradeName);
        putIfNotNull(data, "abbreviation", abbreviation);
        putIfNotNull(data, "organizationType", organizationType);
        putIfNotNull(data, "document", document);
        putIfNotNull(data, "documentType", documentType);
        putIfNotNull(data, "presidentName", presidentName);
        putIfNotNull(data, "presidentCpf", presidentCpf);
        putIfNotNull(data, "email", email);
        putIfNotNull(data, "phone", phone);
        putIfNotNull(data, "website", website);
        putIfNotNull(data, "instagram", instagram);
        putIfNotNull(data, "country", country);
        putIfNotNull(data, "state", state);
        putIfNotNull(data, "city", city);
        putIfNotNull(data, "logoUrl", logoUrl);
        putIfNotNull(data, "primaryColor", primaryColor);
        putIfNotNull(data, "secondaryColor", secondaryColor);
        putIfNotNull(data, "tertiaryColor", tertiaryColor);
        putIfNotNull(data, "quaternaryColor", quaternaryColor);
        putIfNotNull(data, "timezone", timezone);
        putIfNotNull(data, "locale", locale);
        putIfNotNull(data, "status", status);
        putIfNotNull(data, "parentId", parentId);
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