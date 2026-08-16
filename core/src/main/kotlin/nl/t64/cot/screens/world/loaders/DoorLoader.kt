package nl.t64.cot.screens.world.loaders

import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.math.Vector2
import nl.t64.cot.Utils.brokerManager
import nl.t64.cot.Utils.gameData
import nl.t64.cot.components.door.Door
import nl.t64.cot.screens.world.entity.*
import nl.t64.cot.screens.world.entity.events.LoadEntityEvent
import nl.t64.cot.screens.world.map.GameMap
import nl.t64.cot.screens.world.schedule.DoorScheduleDatabase


class DoorLoader(
    private val currentMap: GameMap
) {
    private val doorList: MutableList<Entity> = ArrayList()

    fun createDoors(): List<Entity> {
        loadDoors()
        return ArrayList(doorList)
    }

    private fun loadDoors() {
        currentMap.doors.forEach { loadDoor(it) }
    }

    private fun loadDoor(gameMapDoor: RectangleMapObject) {
        val door: Door = gameData.doors.getDoor(gameMapDoor.name)
        val entity = Entity(gameMapDoor.name, InputEmpty(), PhysicsDoor(door), GraphicsDoor(door))
        val position = Vector2(gameMapDoor.rectangle.x, gameMapDoor.rectangle.y)
        doorList.add(entity)
        brokerManager.blockObservers.addObserver(entity)

        if (DoorScheduleDatabase.shouldBeOpen(gameMapDoor.name)) {
            door.open()
            entity.send(LoadEntityEvent(EntityState.OPENED, position))
        } else {
            door.close()
            brokerManager.actionObservers.addObserver(entity)
            entity.send(LoadEntityEvent(EntityState.IMMOBILE, position))
        }
    }

}
