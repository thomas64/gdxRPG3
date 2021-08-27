package nl.t64.cot.screens.world

import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.math.Vector2
import nl.t64.cot.Utils.brokerManager
import nl.t64.cot.Utils.gameData
import nl.t64.cot.screens.world.entity.*
import nl.t64.cot.screens.world.entity.events.LoadEntityEvent


class DoorLoader(private val currentMap: GameMap) {

    private val doorList: MutableList<Entity> = ArrayList()

    fun createDoors(): List<Entity> {
        loadDoors()
        return ArrayList(doorList)
    }

    private fun loadDoors() {
        currentMap.doors.forEach { loadDoor(it) }
    }

    private fun loadDoor(gameMapDoor: RectangleMapObject) {
        val door = gameData.doors.getDoor(gameMapDoor.name)
        door.close()
        val entity = Entity(gameMapDoor.name, InputEmpty(), PhysicsDoor(door), GraphicsDoor(door))
        doorList.add(entity)
        brokerManager.actionObservers.addObserver(entity)
        brokerManager.blockObservers.addObserver(entity)
        val position = Vector2(gameMapDoor.rectangle.x, gameMapDoor.rectangle.y)
        entity.send(LoadEntityEvent(EntityState.IMMOBILE, position))
    }

}
