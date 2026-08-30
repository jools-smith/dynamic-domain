package revenera.gcs.dmdemo.locking

abstract class Lockable(
    private val lockingPolicy: LockingPolicy
) {
    protected fun <T> locked(action: () -> T): T =
        lockingPolicy.execute(action)
}