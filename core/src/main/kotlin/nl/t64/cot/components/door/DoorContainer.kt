package nl.t64.cot.components.door

import nl.t64.cot.ConfigDataLoader


class DoorContainer {

    private val doors: Map<String, Door> = ConfigDataLoader.createDoors()

    fun getDoor(doorId: String): Door {
        return doors[doorId]!!
    }

}
