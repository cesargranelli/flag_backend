package br.com.flagplatform.organization.service;

import br.com.flagplatform.common.model.Organization;
import br.com.flagplatform.organization.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service para organizações.
 * 
 * Contém regras de negócio para gerenciamento de organizações.
 */
@Service
@RequiredArgsConstructor
public class OrganizationService {

    private final OrganizationRepository repository;

    /**
     * Lista todas as organizações.
     */
    public List<Organization> list() {
        try {
            return repository.listAll();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao listar organizações", e);
        }
    }

    /**
     * Busca organização por ID.
     */
    public Organization getById(String id) {
        try {
            return repository.getById(id);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar organização", e);
        }
    }

    /**
     * Cria uma nova organização.
     */
    public Organization create(Organization organization) {
        try {
            return repository.create(organization);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criar organização", e);
        }
    }

    /**
     * Atualiza uma organização existente.
     */
    public Organization update(String id, java.util.Map<String, Object> data) {
        try {
            return repository.update(id, data);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao atualizar organização", e);
        }
    }

    /**
     * Remove uma organização.
     */
    public void delete(String id) {
        try {
            repository.delete(id);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao remover organização", e);
        }
    }
}
