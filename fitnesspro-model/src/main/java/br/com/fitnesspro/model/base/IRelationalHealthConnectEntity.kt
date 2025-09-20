package br.com.fitnesspro.model.base

/**
 * Interface utilizada nas entidades do Health Connect que possuem uma ligação com uma entidade
 * do modelo de domínio.
 */
interface IRelationalHealthConnectEntity {
    val relationId: String?
}