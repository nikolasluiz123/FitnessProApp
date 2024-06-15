package br.com.fitnesspro.model

import br.com.fitnesspro.model.enums.EnumUserProfile

/**
 * Classe para representar um usuário contendo as informações relevantes para o móvel.
 */
data class User(
    var id: Long? = null,
    var firstName: String,
    var lastName: String,
    var username: String,
    var email: String,
    var password: String,
    var profile: EnumUserProfile
)
