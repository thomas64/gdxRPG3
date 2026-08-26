package nl.t64.cot.components.door

import nl.t64.cot.resources.ConfigDataLoader


class DoorContainer {

    private val doors: Map<String, Door> = ConfigDataLoader.createDoors()

    fun getDoor(doorId: String): Door {
        return doors[doorId]!!
    }

    fun toProgress(): Map<String, DoorProgress> {
        return doors
            .mapValues { it.value.toProgress() }
            .filterValues { it.isChanged() }
    }

    fun applyProgress(progress: Map<String, DoorProgress>) {
        progress.forEach { (id, p) -> doors[id]!!.applyProgress(p) }
    }

}
