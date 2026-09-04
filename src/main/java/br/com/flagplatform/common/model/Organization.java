package br.com.flagplatform.common.model;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.ServerTimestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Organização (federação, liga, clube, universidade).
 * 
 * Mapeado para a collection 'organizations' no Firestore.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Organization {

    @DocumentId
    private String id;

    private String name;

    private String tradeName;

    private String parentId; // Referência a outra organização (hierarquia)

    private String type; // FEDERATION, LEAGUE, ASSOCIATION, UNIVERSITY, CLUB, OTHER

    private String document; // CNPJ/CPF

    private String logoUrl;

    private String primaryColor;

    private String secondaryColor;

    private String tertiaryColor;

    private String quaternaryColor;

    private String email;

    private String phone;

    private String website;

    private String instagram;

    private String country;

    private String state;

    private String city;

    private String timezone;

    private String locale;

    @Builder.Default
    private String status = "ACTIVE"; // ACTIVE, INACTIVE

    @ServerTimestamp
    private Instant createdAt;

    @ServerTimestamp
    private Instant updatedAt;
}
