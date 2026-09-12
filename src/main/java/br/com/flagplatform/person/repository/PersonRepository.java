package br.com.flagplatform.person.repository;

import br.com.flagplatform.person.entity.PersonEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PersonRepository extends JpaRepository<PersonEntity, UUID> {

    boolean existsByNameIgnoreCase(String name);

    boolean existsByCpf(String cpf);

    boolean existsByCpfAndIdNot(String cpf, UUID id);

}
