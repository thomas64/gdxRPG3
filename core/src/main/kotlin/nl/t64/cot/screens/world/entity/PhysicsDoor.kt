package nl.t64.cot.screens.world.entity

import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import nl.t64.cot.Utils.brokerManager
import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.worldScreen
import nl.t64.cot.audio.playSe
import nl.t64.cot.components.condition.ConditionDatabase
import nl.t64.cot.components.door.Door
import nl.t64.cot.constants.Constant
import nl.t64.cot.screens.world.entity.events.*


private const val NPC_DOOR_OPEN_TIME = 3f
private const val DEFAULT_CLOSE_TIME = -1f

class PhysicsDoor(private val door: Door) : PhysicsComponent() {

    private val stringBuilder: StringBuilder = StringBuilder()
    private var isSelected: Boolean = false
    private var isSelectedByNpc: Boolean = false
    private var isOpenedByNpc: Boolean = false
    private var closingDoorTime: Float = DEFAULT_CLOSE_TIME

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
        if (isFailingOnCondition()) return
        if (isFailingOnLock()) return

        if (door.isClosed) {
            openWhenClosedAndRemoveBlocker(true)
            removeKeyFromInventoryIfDoorWasAlreadyUnlockedInThePast()
        } else {
            closeWhenOpenAndAddBlocker(true)
        }
    }

    private fun isFailingOnCondition(): Boolean {
        return !ConditionDatabase.isMeetingConditions(door.conditionIds)
    }

    private fun isFailingOnLock(): Boolean {
        return door.isLocked && !isAbleToUnlockWithKey()
    }

    private fun isAbleToUnlockWithKey(): Boolean {
        val inventory = gameData.inventory
        if (door.keyId == null) {
            stringBuilder.append(door.message!!)
            worldScreen.showMessageDialog(stringBuilder.toString())
            return false
        } else if (inventory.hasEnoughOfItem(door.keyId, 1)) {
            inventory.autoRemoveItem(door.keyId, 1)
            door.unlock()
            stringBuilder.append("You used the key to unlock the door.")
            worldScreen.showMessageDialog(stringBuilder.toString())
            return true
        } else {
            stringBuilder.append("This door is locked.")
            stringBuilder.append(System.lineSeparator())
            stringBuilder.append("You need a key to open the door.")
            worldScreen.showMessageDialog(stringBuilder.toString())
            return false
        }
    }

    private fun removeKeyFromInventoryIfDoorWasAlreadyUnlockedInThePast() {
        if (door.wasLockedOnce() && gameData.inventory.hasEnoughOfItem(door.keyId, 1)) {
            gameData.inventory.autoRemoveItem(door.keyId!!, 1)
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
            if (isOpenedByNpc) return
            closeWhenOpenAndAddBlocker(false)
        }
    }

    private fun openDoorByNpc() {
        isOpenedByNpc = true
        closingDoorTime = NPC_DOOR_OPEN_TIME
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
        closingDoorTime = DEFAULT_CLOSE_TIME
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
            isOpenedByNpc = false
        }
    }

    override fun debug(shapeRenderer: ShapeRenderer) {
        // empty
    }

}
