package revenera.gcs.locking

import revenera.gcs.dmdemo.controllers.Credentials
import revenera.gcs.utils.Loggable

abstract class Lockable(
    private val lockingPolicy: LockingPolicy) : Loggable() {
    protected fun <T> locked(action: () -> T): T =
        lockingPolicy.execute(action)

}