package br.com.flagplatform.institution;

import java.util.UUID;

/**
 * API pública do módulo institution para consulta e asserção de existência.
 * Segue o padrão de isolamento entre módulos (ADR-001 / Spring Modulith).
 */
public interface InstitutionLookup {

    void assertExists(UUID id);

    String findTradeNameById(UUID id);

}