package revenera.gcs.domain

import revenera.gcs.domain.entities.IEntity

interface IEntityManagement {
    fun injectEntity(entity: IEntity) : IEntity

    fun removeEntity(entity: IEntity) : IEntity?
}