package br.com.flagplatform.venue.repository;

import br.com.flagplatform.common.persistence.repository.SoftDeleteRepository;
import br.com.flagplatform.venue.entity.VenueEntity;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VenueRepository extends SoftDeleteRepository<VenueEntity, UUID> {

}
