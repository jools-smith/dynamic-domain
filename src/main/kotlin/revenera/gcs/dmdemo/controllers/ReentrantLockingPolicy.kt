package revenera.gcs.dmdemo.controllers

import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class ReentrantLockingPolicy(
    fair: Boolean = false) : LockingPolicy {

    private val lock = ReentrantLock(fair)

    override fun <T> execute(action: () -> T): T =
        lock.withLock(action)
}