package br.com.flagplatform.common.model;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.ServerTimestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

/**
 * Pessoa (atleta, técnico, árbitro, organizador).
 * 
 * Entidade central que pode ter múltiplos papéis.
 * Mapeado para a collection 'persons' no Firestore.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Person {

    @DocumentId
    private String id;

    private String name;

    private String email;

    private String phone;

    private String photoUrl;

    private String gender; // MALE, FEMALE, MIXED

    private LocalDate birthDate;

    private String computedAgeGroup; // U6, U8, U10, U12, U14, U16, U18, U20, OPEN

    @Builder.Default
    private List<String> roles = List.of(); // ATHLETE, COACH, MANAGER, ORGANIZER, REFEREE, ADMIN

    @Builder.Default
    private String status = "ACTIVE"; // ACTIVE, INACTIVE

    @ServerTimestamp
    private Instant createdAt;

    @ServerTimestamp
    private Instant updatedAt;

    /**
     * Calcula ageGroup a partir de birthDate.
     */
    public String calculateAgeGroup() {
        if (birthDate == null) {
            return null;
        }
        
        LocalDate now = LocalDate.now();
        int age = now.getYear() - birthDate.getYear();
        
        if (now.getMonthValue() < birthDate.getMonthValue() ||
            (now.getMonthValue() == birthDate.getMonthValue() && now.getDayOfMonth() < birthDate.getDayOfMonth())) {
            age--;
        }

        if (age <= 6) return "U6";
        if (age <= 8) return "U8";
        if (age <= 10) return "U10";
        if (age <= 12) return "U12";
        if (age <= 14) return "U14";
        if (age <= 16) return "U16";
        if (age <= 18) return "U18";
        if (age <= 20) return "U20";
        return "OPEN";
    }
}
