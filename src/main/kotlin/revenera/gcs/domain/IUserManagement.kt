package revenera.gcs.domain

import revenera.gcs.dmdemo.controllers.Credentials
import revenera.gcs.domain.entities.User

interface IUserManagement {
    fun getUsers() : Collection<User>

    fun locateUser(username: String) : User?

    fun locateUser(credentials: Credentials) : User?

    fun validateCredentials(credentials: Credentials) : User
}