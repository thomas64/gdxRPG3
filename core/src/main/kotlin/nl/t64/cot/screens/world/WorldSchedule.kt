package nl.t64.cot.screens.world

import nl.t64.cot.screens.world.entity.Entity


class WorldSchedule {

    private lateinit var scheduledEntities: List<Entity>

    fun update(scheduledEntities: List<Entity>, dt: Float) {
        this.scheduledEntities = scheduledEntities


    }

}
