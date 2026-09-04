package br.com.flagplatform.season.service;

import br.com.flagplatform.common.model.Season;
import br.com.flagplatform.season.repository.SeasonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service para temporadas.
 */
@Service
@RequiredArgsConstructor
public class SeasonService {

    private final SeasonRepository repository;

    /**
     * Lista todas as temporadas.
     */
    public List<Season> list() {
        try {
            return repository.listAll();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao listar temporadas", e);
        }
    }

    /**
     * Lista temporadas por organização.
     */
    public List<Season> listByOrganization(String organizationId) {
        try {
            return repository.listWhere("organizationId", organizationId, 100);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao listar temporadas por organização", e);
        }
    }

    /**
     * Busca temporada por ID.
     */
    public Season getById(String id) {
        try {
            return repository.getById(id);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar temporada", e);
        }
    }

    /**
     * Cria uma nova temporada.
     */
    public Season create(Season season) {
        try {
            return repository.create(season);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criar temporada", e);
        }
    }

    /**
     * Atualiza uma temporada existente.
     */
    public Season update(String id, java.util.Map<String, Object> data) {
        try {
            return repository.update(id, data);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao atualizar temporada", e);
        }
    }

    /**
     * Remove uma temporada.
     */
    public void delete(String id) {
        try {
            repository.delete(id);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao remover temporada", e);
        }
    }
}
