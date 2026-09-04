package br.com.flagplatform.person.service;

import br.com.flagplatform.common.model.Person;
import br.com.flagplatform.person.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service para pessoas (atletas, técnicos, árbitros).
 * 
 * Contém regras de negócio como validação de elegibilidade.
 */
@Service
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository repository;

    /**
     * Lista todas as pessoas.
     */
    public List<Person> list() {
        try {
            return repository.listAll();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao listar pessoas", e);
        }
    }

    /**
     * Lista pessoas por role.
     */
    public List<Person> listByRole(String role) {
        try {
            return repository.listByRole(role);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao listar pessoas por role", e);
        }
    }

    /**
     * Busca pessoa por ID.
     */
    public Person getById(String id) {
        try {
            return repository.getById(id);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar pessoa", e);
        }
    }

    /**
     * Busca pessoa por email.
     */
    public Person findByEmail(String email) {
        try {
            return repository.findByEmail(email);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar pessoa por email", e);
        }
    }

    /**
     * Cria uma nova pessoa.
     */
    public Person create(Person person) {
        try {
            // Calcula ageGroup se birthDate fornecido
            if (person.getBirthDate() != null) {
                person.setComputedAgeGroup(person.calculateAgeGroup());
            }
            return repository.create(person);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criar pessoa", e);
        }
    }

    /**
     * Atualiza uma pessoa existente.
     */
    public Person update(String id, java.util.Map<String, Object> data) {
        try {
            // Recalcula ageGroup se birthDate atualizado
            if (data.containsKey("birthDate")) {
                Person person = repository.getById(id);
                if (person != null) {
                    // Atualiza birthDate temporariamente para calcular
                    person.setBirthDate(data.get("birthDate") instanceof String 
                        ? java.time.LocalDate.parse((String) data.get("birthDate")) 
                        : null);
                    data.put("computedAgeGroup", person.calculateAgeGroup());
                }
            }
            return repository.update(id, data);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao atualizar pessoa", e);
        }
    }

    /**
     * Remove uma pessoa.
     */
    public void delete(String id) {
        try {
            repository.delete(id);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao remover pessoa", e);
        }
    }
}
