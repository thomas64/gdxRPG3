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
        possibleLockOrUnlockBySchedule()
        possibleAutoClose(dt)
        if (isSelected) {
            isSelected = false
            useDoor()
        }
        if (isSelectedByNpc) {
            isSelectedByNpc = false
            openDoorByNpc()
        }
    }

    override fun setBoundingBox() {
        boundingBox.set(currentPosition.x, currentPosition.y, door.width, Constant.HALF_TILE_SIZE)
    }

    private fun useDoor() {
        stringBuilder.setLength(0)
        if (isFailingOnLock()) return

        if (door.isClosed) {
            openWhenClosedAndRemoveBlocker(true)
        } else {
            closeWhenOpenAndAddBlocker(true)
        }
    }

    private fun isFailingOnLock(): Boolean {
        return door.isLocked && !isAbleToUnlockWithKey(door.keyId)
    }

    private fun isAbleToUnlockWithKey(keyId: String?): Boolean {
        val inventory = gameData.inventory
        return if (keyId == null) {
            stringBuilder.append("Open from ${door.openStartTime} to ${door.openEndTime}.")
            brokerManager.componentObservers.notifyShowMessageDialog(stringBuilder.toString())
            false
        } else if (inventory.hasEnoughOfItem(keyId, 1)) {
            inventory.autoRemoveItem(keyId, 1)
            door.unlock()
            true
        } else {
            stringBuilder.append("This door is locked.")
            stringBuilder.append(System.lineSeparator())
            stringBuilder.append("You need a key to open the door.")
            brokerManager.componentObservers.notifyShowMessageDialog(stringBuilder.toString())
            false
        }
    }

    private fun possibleLockOrUnlockBySchedule() {
        door.openStartTime?.let { openStartTime ->
            door.openEndTime?.let { openEndTime ->
                lockOrUnlockBySchedule(openStartTime, openEndTime)
            }
        }
    }

    private fun lockOrUnlockBySchedule(openStartTime: String, openEndTime: String) {
        if (gameData.clock.isCurrentTimeInBetween(openStartTime, openEndTime)) {
            door.unlock()
        } else {
            door.lock()
            closeWhenOpenAndAddBlocker(false)
        }
    }

    private fun openDoorByNpc() {
        closingDoorTime = 2f
        openWhenClosedAndRemoveBlocker(false)
        brokerManager.blockObservers.addObserver(entity)
        brokerManager.actionObservers.removeObserver(entity)
    }

    private fun possibleAutoClose(dt: Float) {
        if (closingDoorTime >= 0f) {
            closingDoorTime -= dt
            if (closingDoorTime < 0f) {
                autoClose()
            }
        }
    }

    private fun autoClose() {
        closingDoorTime = -1f
        closeWhenOpenAndAddBlocker(false)
        brokerManager.actionObservers.addObserver(entity)
    }

    private fun openWhenClosedAndRemoveBlocker(withSound: Boolean) {
        if (door.isClosed) {
            if (withSound) playSe(door.audio)
            door.open()
            entity.send(StateEvent(EntityState.OPENED))
            brokerManager.blockObservers.removeObserver(entity)
        }
    }

    private fun closeWhenOpenAndAddBlocker(withSound: Boolean) {
        if (door.isOpen) {
            if (withSound) playSe(door.audio)
            door.close()
            entity.send(StateEvent(EntityState.CLOSING))
            brokerManager.blockObservers.addObserver(entity)
        }
    }

    override fun debug(shapeRenderer: ShapeRenderer) {
        // empty
    }

}
