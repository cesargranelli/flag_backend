package br.com.flagplatform.competition.service;

import br.com.flagplatform.common.model.Competition;
import br.com.flagplatform.common.model.Person;
import br.com.flagplatform.competition.repository.CompetitionRepository;
import br.com.flagplatform.person.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Service para competições.
 * 
 * Contém regras de negócio como validação de elegibilidade.
 */
@Service
@RequiredArgsConstructor
public class CompetitionService {

    private final CompetitionRepository repository;
    private final PersonService personService;

    /**
     * Lista todas as competições.
     */
    public List<Competition> list() {
        try {
            return repository.listAll();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao listar competições", e);
        }
    }

    /**
     * Lista competições por organização.
     */
    public List<Competition> listByOrganization(String organizationId) {
        try {
            return repository.listWhere("organizationId", organizationId, 100);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao listar competições por organização", e);
        }
    }

    /**
     * Lista competições por season.
     */
    public List<Competition> listBySeason(String seasonId) {
        try {
            return repository.listWhere("seasonId", seasonId, 100);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao listar competições por season", e);
        }
    }

    /**
     * Busca competição por ID.
     */
    public Competition getById(String id) {
        try {
            return repository.getById(id);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar competição", e);
        }
    }

    /**
     * Cria uma nova competição.
     */
    public Competition create(Competition competition) {
        try {
            return repository.create(competition);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criar competição", e);
        }
    }

    /**
     * Atualiza uma competição existente.
     */
    public Competition update(String id, Map<String, Object> data) {
        try {
            return repository.update(id, data);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao atualizar competição", e);
        }
    }

    /**
     * Remove uma competição.
     */
    public void delete(String id) {
        try {
            repository.delete(id);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao remover competição", e);
        }
    }

    /**
     * Valida elegibilidade de um atleta para uma competição.
     * 
     * Regras:
     * 1. Verificar gênero (se competition.gender definido)
     * 2. Verificar idade (se eligibilityRules.minAge/maxAge definidos)
     */
    public boolean validateEligibility(Person person, Competition competition) {
        if (competition.getEligibilityRules() == null) {
            return true; // Sem regras = todos elegíveis
        }

        Map<String, Object> rules = competition.getEligibilityRules();

        // Verificar gênero
        List<String> allowedGenders = (List<String>) rules.get("allowedGenders");
        if (allowedGenders != null && !allowedGenders.isEmpty() && person.getGender() != null) {
            if (!allowedGenders.contains(person.getGender())) {
                return false;
            }
        }

        // Verificar idade
        if (person.getBirthDate() != null) {
            LocalDate now = LocalDate.now();
            int age = now.getYear() - person.getBirthDate().getYear();
            if (now.getMonthValue() < person.getBirthDate().getMonthValue() ||
                (now.getMonthValue() == person.getBirthDate().getMonthValue() && 
                 now.getDayOfMonth() < person.getBirthDate().getDayOfMonth())) {
                age--;
            }

            Number minAge = (Number) rules.get("minAge");
            Number maxAge = (Number) rules.get("maxAge");

            if (minAge != null && age < minAge.intValue()) {
                return false;
            }
            if (maxAge != null && age > maxAge.intValue()) {
                return false;
            }
        }

        return true;
    }
}
