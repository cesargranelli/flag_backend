package br.com.flagplatform.venue.service;

import br.com.flagplatform.common.model.Venue;
import br.com.flagplatform.venue.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service para venues (campos de jogo).
 */
@Service
@RequiredArgsConstructor
public class VenueService {

    private final VenueRepository repository;

    /**
     * Lista todos os venues.
     */
    public List<Venue> list() {
        try {
            return repository.listAll();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao listar venues", e);
        }
    }

    /**
     * Busca venue por ID.
     */
    public Venue getById(String id) {
        try {
            return repository.getById(id);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar venue", e);
        }
    }

    /**
     * Cria um novo venue.
     */
    public Venue create(Venue venue) {
        try {
            return repository.create(venue);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criar venue", e);
        }
    }

    /**
     * Atualiza um venue existente.
     */
    public Venue update(String id, java.util.Map<String, Object> data) {
        try {
            return repository.update(id, data);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao atualizar venue", e);
        }
    }

    /**
     * Remove um venue.
     */
    public void delete(String id) {
        try {
            repository.delete(id);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao remover venue", e);
        }
    }
}
