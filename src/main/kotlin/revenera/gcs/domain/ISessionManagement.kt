package revenera.gcs.domain

import revenera.gcs.domain.entities.Session

interface ISessionManagement {
    fun getSessions() : Collection<Session>

    fun locateSession(sid: String) : Session?

    fun housekeeping()
}