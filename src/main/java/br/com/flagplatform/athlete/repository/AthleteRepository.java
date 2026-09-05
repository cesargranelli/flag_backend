package br.com.flagplatform.athlete.repository;

import br.com.flagplatform.common.persistence.repository.SoftDeleteRepository;
import br.com.flagplatform.athlete.entity.AthleteEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AthleteRepository extends SoftDeleteRepository<AthleteEntity, UUID> {

    List<AthleteEntity> findAllByOrderByNameAsc();

    boolean existsByNameIgnoreCase(String name);

    List<AthleteEntity> findByNameIgnoreCase(String name);

    boolean existsByCpf(String cpf);

    boolean existsByCpfAndIdNot(String cpf, UUID id);

}
