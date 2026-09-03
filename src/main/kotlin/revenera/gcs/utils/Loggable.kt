package revenera.gcs.utils

import org.slf4j.LoggerFactory

abstract class Loggable {
    protected val logger = LoggerFactory.getLogger(this.javaClass)
}