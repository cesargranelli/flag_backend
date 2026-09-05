package br.com.flagplatform.venue.repository;

import br.com.flagplatform.venue.entity.VenueEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

/**
 * Porta de persistência do domínio Venue (ADR-006). Isola o
 * {@code VenueService} da tecnologia de armazenamento e viabiliza a
 * <b>persistência dual</b>: com {@code app.firestore.venue=false} vigora a
 * implementação JPA/PostgreSQL ({@link JpaVenueStore}, padrão atual); com a
 * flag {@code true} entra {@link DualVenueStore}, que mantém o PostgreSQL
 * como escrita autoritativa e espelha toda escrita no Firestore.
 *
 * <p>As leituras sempre vêm do PostgreSQL (fonte de verdade); o Firestore é o
 * espelho/realtime para os apps. Regras de negócio e contrato REST ficam intactos
 * no service — ele conhece apenas esta porta.
 */
public interface VenueStore {

    VenueEntity save(VenueEntity entity);

    Optional<VenueEntity> findById(UUID id);

    boolean existsById(UUID id);

    Page<VenueEntity> findAll(Pageable pageable);

}