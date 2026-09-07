package br.com.flagplatform.institution.repository;

import br.com.flagplatform.institution.entity.InstitutionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface InstitutionRepository extends JpaRepository<InstitutionEntity, UUID> {
}
