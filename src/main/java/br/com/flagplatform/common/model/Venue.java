package br.com.flagplatform.common.model;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.ServerTimestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

/**
 * Campo de jogo (venue).
 * 
 * Mapeado para a collection 'venues' no Firestore.
 * O address é um map nested (objeto).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Venue {

    @DocumentId
    private String id;

    private String name;

    private String logoUrl;

    private Map<String, Object> address; // Objeto nested: street, number, city, etc.

    private String mapsUrl;

    @ServerTimestamp
    private Instant createdAt;

    @ServerTimestamp
    private Instant updatedAt;

    /**
     * Cria um address como map.
     */
    public static Map<String, Object> createAddress(
            String street,
            String number,
            String complement,
            String neighborhood,
            String city,
            String state,
            String country,
            String zipCode,
            Double lat,
            Double lng) {
        return Map.of(
            "street", street != null ? street : "",
            "number", number != null ? number : "",
            "complement", complement != null ? complement : "",
            "neighborhood", neighborhood != null ? neighborhood : "",
            "city", city != null ? city : "",
            "state", state != null ? state : "",
            "country", country != null ? country : "",
            "zipCode", zipCode != null ? zipCode : "",
            "lat", lat != null ? lat : 0.0,
            "lng", lng != null ? lng : 0.0
        );
    }
}
