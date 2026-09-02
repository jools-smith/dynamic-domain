package revenera.gcs.locking

import revenera.gcs.dmdemo.controllers.Credentials

abstract class Lockable(
    private val lockingPolicy: LockingPolicy
) {
    protected fun <T> locked(action: () -> T): T =
        lockingPolicy.execute(action)

}