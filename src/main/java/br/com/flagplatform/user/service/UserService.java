package br.com.flagplatform.user.service;

import br.com.flagplatform.common.model.User;
import br.com.flagplatform.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service para usuários.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;

    /**
     * Lista todos os usuários.
     */
    public List<User> list() {
        try {
            return repository.listAll();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao listar usuários", e);
        }
    }

    /**
     * Busca usuário por ID.
     */
    public User getById(String id) {
        try {
            return repository.getById(id);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar usuário", e);
        }
    }

    /**
     * Cria um novo usuário.
     */
    public User create(User user) {
        try {
            return repository.create(user);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criar usuário", e);
        }
    }

    /**
     * Atualiza um usuário existente.
     */
    public User update(String id, java.util.Map<String, Object> data) {
        try {
            return repository.update(id, data);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao atualizar usuário", e);
        }
    }

    /**
     * Remove um usuário.
     */
    public void delete(String id) {
        try {
            repository.delete(id);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao remover usuário", e);
        }
    }
}
