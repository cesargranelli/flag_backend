package br.com.flagplatform.common.persistence.repository;

import br.com.flagplatform.common.persistence.entity.BaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository base para entidades com soft delete (ADR-006).
 *
 * Exige que o tipo da entidade herde de BaseEntity (que possui deletedAt).
 *
 * Comportamento padrão:
 * - findAll() e findById() retornam SOMENTE registros ativos (deletedAt IS NULL).
 * - Use findAllIncludingDeleted() / findByIdIncludingDeleted() quando precisar
 *   consultar registros deletados (ex: recuperação, auditoria).
 * - softDelete() marca o registro como excluído definindo deletedAt.
 */
@NoRepositoryBean
public interface SoftDeleteRepository<T extends BaseEntity, ID> extends JpaRepository<T, ID> {

    // ───findAll & findById COM soft-delete filter (comportamento padrão) ───

    @Override
    @Query("SELECT e FROM #{#entityName} e WHERE e.deletedAt IS NULL")
    List<T> findAll();

    @Override
    @Query("SELECT e FROM #{#entityName} e WHERE e.deletedAt IS NULL")
    Page<T> findAll(Pageable pageable);

    @Override
    @Query("SELECT e FROM #{#entityName} e WHERE e.deletedAt IS NULL AND e.id = :id")
    Optional<T> findById(@Param("id") ID id);

    @Override
    @Query("SELECT COUNT(e) FROM #{#entityName} e WHERE e.deletedAt IS NULL")
    long count();

    // ─── Consultas que incluem registros deletados ────────────────────────

    @Query("SELECT e FROM #{#entityName} e")
    List<T> findAllIncludingDeleted();

    @Query("SELECT e FROM #{#entityName} e WHERE e.id = :id")
    Optional<T> findByIdIncludingDeleted(@Param("id") ID id);

    // ─── Helpers de soft delete ───────────────────────────────────────────

    /**
     * Soft delete: marca o registro como excluído definindo deletedAt.
     * Retorna 1 se o registro existia e estava ativo; 0 caso contrário.
     */
    @Modifying
    @Query("UPDATE #{#entityName} e SET e.deletedAt = :now, e.updatedAt = :now WHERE e.id = :id AND e.deletedAt IS NULL")
    int softDelete(@Param("id") ID id, @Param("now") LocalDateTime now);

    default boolean softDeleteById(ID id) {
        return softDelete(id, LocalDateTime.now()) > 0;
    }

    /**
     * Verifica se existe um registro ativo com o ID informado.
     */
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM #{#entityName} e WHERE e.id = :id AND e.deletedAt IS NULL")
    boolean existsByIdActive(@Param("id") ID id);
}
