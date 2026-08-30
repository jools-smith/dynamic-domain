package revenera.gcs.dmdemo.controllers

interface LockingPolicy {
    fun <T> execute(action: () -> T): T
}