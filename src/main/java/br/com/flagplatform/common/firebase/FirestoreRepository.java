package br.com.flagplatform.common.firebase;

import java.util.List;
import java.util.Optional;

/**
 * Interface-base (porta) para repositórios Firestore por domínio — ADR-006.
 *
 * <p>Habilita a <b>persistência dual</b>: PostgreSQL/JPA continua sendo a escrita padrão
 * e o Firestore é adotado <b>domínio a domínio</b>, sem big bang. Cada domínio que migrar
 * deve seguir o padrão de porta de repositório:
 *
 * <pre>{@code
 * // 1. Interface do domínio (JPA, padrão atual) — NÃO muda:
 * public interface OrganizationRepository extends JpaRepository<OrganizationEntity, UUID> { ... }
 *
 * // 2. Implementação Firestore do domínio (nas próximas issues), ativada por flag:
 * @Component
 * @ConditionalOnProperty(name = "app.firestore.organization", havingValue = "true")
 * public class OrganizationFirestoreRepository
 *         implements OrganizationRepository, FirestoreRepository<OrganizationEntity> { ... }
 * }</pre>
 *
 * <p>A troca é por domínio: enquanto {@code app.firestore.<domínio>} estiver {@code false},
 * a implementação JPA (Spring Data) continua sendo o único bean do repositório; ao ligar a
 * flag, o bean Firestore entra com a mesma interface do domínio e passa a atender o service
 * sem alteração nas regras de negócio.
 *
 * <h2>Mapeamento de ids</h2>
 * <p>As entidades JPA usam {@link java.util.UUID} ({@code BaseEntity}); no Firestore o
 * documento é identificado por {@link String}. A implementação Firestore deve usar
 * {@code id.toString()} como document id e reconverter {@code UUID.fromString(...)} na leitura.
 *
 * <h2>Contratos</h2>
 * <ul>
 *   <li>Coleção de escrita: Java REST (Admin SDK) — o cliente web NUNCA escreve direto no Firestore.</li>
 *   <li>Leitura: Firestore (realtime) + regras de segurança com leitura pública/autenticada.</li>
 *   <li>Documentos armazenados em mapas próprios por domínio (dto/entity), nunca entidades JPA diretas.</li>
 * </ul>
 *
 * @param <T> tipo do documento/entidade persistido pelo repositório Firestore
 */
public interface FirestoreRepository<T> {

    /**
     * Busca um documento por id ({@code UUID.toString()} do {@code BaseEntity}).
     */
    Optional<T> findById(String id);

    /**
     * Lista todos os documentos da coleção do domínio.
     */
    List<T> findAll();

    /**
     * Grava (cria ou atualiza) um documento na coleção do domínio.
     *
     * @return a entidade persistida (com id resolvido, se gerado pelo Firestore)
     */
    T save(T entity);

    /**
     * Remove o documento com o id informado. Não lança erro se o documento não existir.
     */
    void delete(String id);
}