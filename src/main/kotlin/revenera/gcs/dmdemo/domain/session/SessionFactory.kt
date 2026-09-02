package revenera.gcs.dmdemo.domain.session

import org.springframework.stereotype.Service
import revenera.gcs.dmdemo.domain.DomainFactory
import revenera.gcs.dmdemo.domain.DomainIdentifier
import revenera.gcs.dmdemo.domain.DomainObjectType
import revenera.gcs.dmdemo.model.StringGenerator
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

@Service
class SessionFactory(
    stringGenerator: StringGenerator
) : DomainFactory(stringGenerator) {

    fun create(
        userId: DomainIdentifier,
        lifetime: Duration = 1.hours,): Session = Session(
        id = createIdentifier(DomainObjectType.SESSION),
        lifetime = lifetime,
        userId = userId)
}