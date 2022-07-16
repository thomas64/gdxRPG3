package nl.t64.cot.screens.world.entity

import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import nl.t64.cot.Utils.brokerManager
import nl.t64.cot.Utils.gameData
import nl.t64.cot.audio.playSe
import nl.t64.cot.components.door.Door
import nl.t64.cot.constants.Constant
import nl.t64.cot.screens.world.entity.events.*


class PhysicsDoor(private val door: Door) : PhysicsComponent() {

    private val stringBuilder: StringBuilder = StringBuilder()
    private var isSelected: Boolean = false
    private var isSelectedByNpc: Boolean = false
    private var closingDoorTime: Float = -1f

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
        if (event is NpcActionEvent) {
            isSelectedByNpc = true
        }
    }

    override fun update(entity: Entity, dt: Float) {
        this.entity = entity
        handleAutoClosingDoor(dt)
        if (isSelected) {
            isSelected = false
            tryToOpenDoor()
        }
        if (isSelectedByNpc) {
            isSelectedByNpc = false
            openDoorByNpc()
        }
    }

    override fun setBoundingBox() {
        boundingBox.set(currentPosition.x, currentPosition.y, door.width, Constant.HALF_TILE_SIZE)
    }

    private fun tryToOpenDoor() {
        stringBuilder.setLength(0)
        if (isFailingOnLock()) return

        if (door.isClosed) {
            openDoor()
        } else {
            closeDoor()
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

    private fun openDoor() {
        playSe(door.audio)
        door.open()
        entity.send(StateEvent(EntityState.OPENED))
        brokerManager.blockObservers.removeObserver(entity)
    }

    private fun closeDoor() {
        playSe(door.audio)
        door.close()
        entity.send(StateEvent(EntityState.CLOSING))
        brokerManager.blockObservers.addObserver(entity)
    }

    private fun openDoorByNpc() {
        closingDoorTime = 2f
        brokerManager.blockObservers.addObserver(entity)
        brokerManager.actionObservers.removeObserver(entity)
        if (door.isClosed) {
            door.open()
            entity.send(StateEvent(EntityState.OPENED))
        }
    }

    private fun handleAutoClosingDoor(dt: Float) {
        if (closingDoorTime >= 0f) {
            closingDoorTime -= dt
            if (closingDoorTime < 0f) {
                closingDoorTime = -1f
                autoCloseDoor()
            }
        }
    }

    private fun autoCloseDoor() {
        door.close()
        entity.send(StateEvent(EntityState.CLOSING))
        brokerManager.actionObservers.addObserver(entity)
    }

    override fun debug(shapeRenderer: ShapeRenderer) {
        // empty
    }

}
