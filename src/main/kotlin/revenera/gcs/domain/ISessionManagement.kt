package revenera.gcs.domain

interface ISessionManagement {
    fun getSessions() : Collection<Entity.Session>

    fun locateSession(sid: String) : Entity.Session?
}