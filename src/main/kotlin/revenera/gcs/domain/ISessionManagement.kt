package revenera.gcs.domain

interface ISessionManagement {
    fun getSessions() : Collection<Session>

    fun locateSession(sid: String) : Session?
}