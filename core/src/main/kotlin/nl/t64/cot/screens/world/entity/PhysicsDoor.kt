package nl.t64.cot.screens.world.entity

import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import nl.t64.cot.Utils.audioManager
import nl.t64.cot.Utils.brokerManager
import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.mapManager
import nl.t64.cot.audio.AudioCommand
import nl.t64.cot.components.door.Door
import nl.t64.cot.constants.Constant
import nl.t64.cot.screens.world.entity.events.Event
import nl.t64.cot.screens.world.entity.events.LoadEntityEvent
import nl.t64.cot.screens.world.entity.events.OnActionEvent
import nl.t64.cot.screens.world.entity.events.StateEvent


class PhysicsDoor(private val door: Door) : PhysicsComponent() {

    private val stringBuilder: StringBuilder = StringBuilder()
    private var isSelected: Boolean = false

    override fun receive(event: Event) {
        if (event is LoadEntityEvent) {
            currentPosition = event.position
            setBoundingBox()
        }
        if (event is OnActionEvent) {
            if ((event.playerDirection == Direction.NORTH
                        || event.playerDirection == Direction.SOUTH)
                && event.checkRect.overlaps(boundingBox)
            ) {
                isSelected = true
            }
        }
    }

    override fun update(entity: Entity, dt: Float) {
        if (isSelected) {
            isSelected = false
            tryToOpenDoor(entity)
        }
    }

    override fun setBoundingBox() {
        boundingBox.set(currentPosition.x, currentPosition.y, door.width, Constant.HALF_TILE_SIZE)
    }

    private fun tryToOpenDoor(entity: Entity) {
        stringBuilder.setLength(0)
        if (isFailingOnLock()) return

        if (door.isClosed) {
            openDoor(entity)
        }
    }

    private fun isFailingOnLock(): Boolean {
        return door.isLocked && isUnableToUnlockWithKey(door.keyId)
    }

    private fun isUnableToUnlockWithKey(keyId: String?): Boolean {
        val inventory = gameData.inventory
        return if (inventory.hasEnoughOfItem(keyId, 1)) {
            inventory.autoRemoveItem(keyId!!, 1)
            door.unlock()
            false
        } else {
            stringBuilder.append("This door is locked.")
            stringBuilder.append(System.lineSeparator())
            stringBuilder.append("You need a key to open the door.")
            brokerManager.componentObservers.notifyShowMessageDialog(stringBuilder.toString())
            true
        }
    }

    private fun openDoor(entity: Entity) {
        audioManager.handle(AudioCommand.SE_PLAY_ONCE, door.audio)
        door.open()
        entity.send(StateEvent(EntityState.OPENED))
        brokerManager.blockObservers.removeObserver(entity)
        mapManager.setTiledGraph()
    }

    override fun debug(shapeRenderer: ShapeRenderer) {
        // empty
    }

}
