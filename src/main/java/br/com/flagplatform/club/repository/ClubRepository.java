package br.com.flagplatform.club.repository;

import br.com.flagplatform.club.entity.ClubEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ClubRepository extends JpaRepository<ClubEntity, UUID> {

    List<ClubEntity> findAllByOrganizationIdOrderByNameAsc(UUID organizationId);

    Page<ClubEntity> findAllByOrganizationId(UUID organizationId, Pageable pageable);

}
