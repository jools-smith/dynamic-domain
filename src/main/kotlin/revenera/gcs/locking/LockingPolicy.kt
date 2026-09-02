package revenera.gcs.locking

interface LockingPolicy {
    fun <T> execute(action: () -> T): T
}