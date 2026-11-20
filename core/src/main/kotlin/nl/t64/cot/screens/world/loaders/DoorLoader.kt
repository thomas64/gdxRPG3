package nl.t64.cot.screens.world.loaders

import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.math.Vector2
import nl.t64.cot.Utils.brokerManager
import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.resourceManager
import nl.t64.cot.components.door.Door
import nl.t64.cot.screens.world.atmosphere.AmbientLight
import nl.t64.cot.screens.world.atmosphere.LitSchedule
import nl.t64.cot.screens.world.entity.*
import nl.t64.cot.screens.world.entity.events.LoadEntityEvent
import nl.t64.cot.screens.world.map.GameMap
import nl.t64.cot.screens.world.schedule.DoorScheduleDatabase


private const val DEFAULT_DOOR_LIT_FROM = "19:00"

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
        val litSchedule: LitSchedule? = createLitSchedule(gameMapDoor, door)
        val entity = Entity(gameMapDoor.name, InputEmpty(), PhysicsDoor(door), GraphicsDoor(door, litSchedule))
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

    private fun createLitSchedule(gameMapDoor: RectangleMapObject, door: Door): LitSchedule? {
        if (!hasLitSprite(door)) return null
        return LitSchedule(gameMapDoor.properties, getDefaultLitFrom())
    }

    private fun hasLitSprite(door: Door): Boolean {
        return resourceManager.getSpriteConfig(door.litSpriteId) != null
    }

    private fun getDefaultLitFrom(): String {
        return if (currentMap.ambientLight == AmbientLight.DAY_CYCLE) DEFAULT_DOOR_LIT_FROM else LitSchedule.NEVER
    }

}
