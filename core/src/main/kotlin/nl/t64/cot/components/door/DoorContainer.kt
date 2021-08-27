package nl.t64.cot.components.door

import com.badlogic.gdx.Gdx
import nl.t64.cot.Utils


private const val DOOR_CONFIGS = "configs/doors/"
private const val FILE_LIST = DOOR_CONFIGS + "_files.txt"

class DoorContainer {

    private val doors: Map<String, Door> = Gdx.files.internal(FILE_LIST).readString()
        .split(System.lineSeparator())
        .map { Gdx.files.internal(DOOR_CONFIGS + it).readString() }
        .map { Utils.readValue<Door>(it) }
        .onEach { setDependentVariables(it) }
        .flatMap { it.toList() }
        .toMap()

    fun getDoor(doorId: String): Door {
        return doors[doorId]!!
    }

    private fun setDependentVariables(newDoors: Map<String, Door>) {
        newDoors.values.forEach {
            it.audio = it.type.audioEvent
            it.width = it.type.width
            it.height = it.type.height
            it.isLocked = it.keyId != null
            it.isClosed = true
        }
    }

}
