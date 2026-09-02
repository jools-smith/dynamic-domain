package revenera.gcs.dmdemo.domain.user

import org.springframework.stereotype.Service
import revenera.gcs.dmdemo.domain.DomainFactory
import revenera.gcs.dmdemo.domain.DomainObjectType
import revenera.gcs.dmdemo.model.Credentials
import revenera.gcs.dmdemo.model.StringGenerator

@Service
class UserFactory(
    stringGenerator: StringGenerator
) : DomainFactory(stringGenerator) {

    fun create(credentials: Credentials): User = User(
        id = createIdentifier(DomainObjectType.USER),
        credentials =credentials)
}