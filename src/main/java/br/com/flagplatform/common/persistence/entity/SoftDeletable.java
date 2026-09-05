package br.com.flagplatform.common.persistence.entity;

/**
 * Marker interface para entidades que suportam soft delete.
 * Entidades que implementam esta interface terão o campo deletedAt
 * que marca a exclusão lógica.
 */
public interface SoftDeletable {
}
