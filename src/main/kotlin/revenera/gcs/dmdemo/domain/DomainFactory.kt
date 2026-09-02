package revenera.gcs.dmdemo.domain

import revenera.gcs.dmdemo.model.StringGenerator

abstract class DomainFactory(
    private val stringGenerator: StringGenerator
) {

    protected fun createIdentifier(
        type: DomainObjectType,
        name: String = ""
    ): DomainIdentifier = DomainIdentifier(
        type = type,
        id = stringGenerator.generateBase34Token(),
        name = name
    )
}