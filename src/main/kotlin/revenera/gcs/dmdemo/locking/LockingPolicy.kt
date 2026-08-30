package revenera.gcs.dmdemo.locking

interface LockingPolicy {
    fun <T> execute(action: () -> T): T
}