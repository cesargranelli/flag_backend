package br.com.flagplatform.venue.repository;

import br.com.flagplatform.venue.entity.VenueEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VenueRepository extends JpaRepository<VenueEntity, UUID> {

}
