package revenera.gcs.domain

import revenera.gcs.dmdemo.controllers.Credentials

interface IUserManagement {
    fun getUsers() : Collection<Entity.User>

    fun locateUser(username: String) : Entity.User?

    fun locateUser(credentials: Credentials) : Entity.User?

    fun validateCredentials(credentials: Credentials) : Entity.User
}