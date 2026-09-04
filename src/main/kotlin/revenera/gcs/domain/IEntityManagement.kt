package revenera.gcs.domain

interface IEntityManagement {
    fun injectEntity(entity: Entity) : Entity

    fun removeEntity(entity: Entity) : Entity?
}