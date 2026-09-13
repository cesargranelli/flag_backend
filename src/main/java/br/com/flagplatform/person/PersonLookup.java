package br.com.flagplatform.person;

import java.util.UUID;

/**
 * API pública do módulo person para consulta de pessoas.
 * <p>
 * Sem tipos de subpacote (DTO/exception) na assinatura para não vazar
 * API interna e manter o isolamento do Spring Modulith.
 */
public interface PersonLookup {

    void assertExists(UUID id);

    boolean existsById(UUID id);

    PersonInfo findPersonInfoById(UUID id);

}
