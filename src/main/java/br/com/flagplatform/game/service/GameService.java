package br.com.flagplatform.game.service;

import br.com.flagplatform.common.model.Game;
import br.com.flagplatform.game.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Service para jogos.
 * 
 * Contém regras de negócio como transições de status e atualização de placar.
 */
@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository repository;

    /**
     * Lista todos os jogos.
     */
    public List<Game> list() {
        try {
            return repository.listAll();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao listar jogos", e);
        }
    }

    /**
     * Lista jogos por competição.
     */
    public List<Game> listByCompetition(String competitionId) {
        try {
            return repository.listWhere("competitionId", competitionId, 100);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao listar jogos por competição", e);
        }
    }

    /**
     * Lista jogos ativos (SCHEDULED ou IN_PROGRESS).
     */
    public List<Game> listActive() {
        try {
            // Nota: Firestore não suporta OR em whereEqualTo, 
            // então fazemos duas queries e combinamos
            List<Game> scheduled = repository.listWhere("status", "SCHEDULED", 50);
            List<Game> inProgress = repository.listWhere("status", "IN_PROGRESS", 50);
            scheduled.addAll(inProgress);
            return scheduled;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao listar jogos ativos", e);
        }
    }

    /**
     * Busca jogo por ID.
     */
    public Game getById(String id) {
        try {
            return repository.getById(id);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar jogo", e);
        }
    }

    /**
     * Cria um novo jogo.
     */
    public Game create(Game game) {
        try {
            return repository.create(game);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criar jogo", e);
        }
    }

    /**
     * Atualiza o placar do jogo.
     */
    public Game updateScore(String gameId, int homeScore, int awayScore) {
        try {
            Map<String, Object> data = Map.of(
                "homeScore", homeScore,
                "awayScore", awayScore
            );
            return repository.update(gameId, data);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao atualizar placar", e);
        }
    }

    /**
     * Atualiza o status do jogo com validação de transição.
     * 
     * Transições válidas:
     * - SCHEDULED → OPENING, CANCELLED
     * - OPENING → IN_PROGRESS, CANCELLED
     * - IN_PROGRESS → CONFERENCE, CANCELLED
     * - CONFERENCE → FINISHED
     * - FINISHED → (nenhuma)
     * - CANCELLED → SCHEDULED
     */
    public Game updateStatus(String gameId, String newStatus) {
        try {
            Game game = repository.getById(gameId);
            if (game == null) {
                throw new RuntimeException("Jogo não encontrado: " + gameId);
            }

            String currentStatus = game.getStatus();
            if (!isValidTransition(currentStatus, newStatus)) {
                throw new RuntimeException(
                    "Transição inválida: " + currentStatus + " → " + newStatus);
            }

            Map<String, Object> data = Map.of("status", newStatus);
            return repository.update(gameId, data);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao atualizar status", e);
        }
    }

    /**
     * Registra início do jogo.
     */
    public Game startGame(String gameId) {
        try {
            Map<String, Object> data = Map.of(
                "status", "IN_PROGRESS",
                "actualStartTime", Instant.now()
            );
            return repository.update(gameId, data);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao iniciar jogo", e);
        }
    }

    /**
     * Registra fim do jogo.
     */
    public Game finishGame(String gameId) {
        try {
            Map<String, Object> data = Map.of(
                "status", "FINISHED",
                "actualEndTime", Instant.now()
            );
            return repository.update(gameId, data);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao finalizar jogo", e);
        }
    }

    /**
     * Remove um jogo.
     */
    public void delete(String id) {
        try {
            repository.delete(id);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao remover jogo", e);
        }
    }

    /**
     * Valida se a transição de status é válida.
     */
    private boolean isValidTransition(String current, String next) {
        return switch (current) {
            case "SCHEDULED" -> "OPENING".equals(next) || "CANCELLED".equals(next);
            case "OPENING" -> "IN_PROGRESS".equals(next) || "CANCELLED".equals(next);
            case "IN_PROGRESS" -> "CONFERENCE".equals(next) || "CANCELLED".equals(next);
            case "CONFERENCE" -> "FINISHED".equals(next);
            case "FINISHED" -> false;
            case "CANCELLED" -> "SCHEDULED".equals(next);
            default -> false;
        };
    }
}
