package br.com.flagplatform.team.service;

import br.com.flagplatform.common.model.Team;
import br.com.flagplatform.team.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service para times.
 * 
 * Contém regras de negócio para gerenciamento de times.
 */
@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository repository;

    /**
     * Lista todos os times.
     */
    public List<Team> list() {
        try {
            return repository.listAll();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao listar times", e);
        }
    }

    /**
     * Lista times por organização.
     */
    public List<Team> listByOrganization(String organizationId) {
        try {
            return repository.listWhere("organizationId", organizationId, 100);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao listar times por organização", e);
        }
    }

    /**
     * Busca time por ID.
     */
    public Team getById(String id) {
        try {
            return repository.getById(id);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar time", e);
        }
    }

    /**
     * Cria um novo time.
     */
    public Team create(Team team) {
        try {
            return repository.create(team);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criar time", e);
        }
    }

    /**
     * Atualiza um time existente.
     */
    public Team update(String id, java.util.Map<String, Object> data) {
        try {
            return repository.update(id, data);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao atualizar time", e);
        }
    }

    /**
     * Remove um time.
     */
    public void delete(String id) {
        try {
            repository.delete(id);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao remover time", e);
        }
    }
}
