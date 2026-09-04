package revenera.gcs.domain

import kotlinx.serialization.Serializable
import revenera.gcs.utils.StringGenerator
import revenera.gcs.utils.md5

@Serializable
data class User(
    override val id: String,
    val username: String,
    val password: String,
    val hash: String) : Entity {
    override val type: Type = Type.USER

    companion object {
        fun create(
            generator: StringGenerator,
            username: String,
            password: String) = User(
            generator.generate(),
            username,
            password,
                password.md5()
        )
    }
}


