package revenera.gcs.dmdemo.controllers

abstract class Lockable(
    private val lockingPolicy: LockingPolicy
) {
    protected fun <T> locked(action: () -> T): T =
        lockingPolicy.execute(action)
}