package nl.t64.cot.screens.world.entity

import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import nl.t64.cot.Utils.brokerManager
import nl.t64.cot.Utils.gameData
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.components.loot.Loot
import nl.t64.cot.components.party.HeroItem
import nl.t64.cot.components.party.skills.SkillItemId
import nl.t64.cot.constants.Constant
import nl.t64.cot.screens.world.entity.events.Event
import nl.t64.cot.screens.world.entity.events.LoadEntityEvent
import nl.t64.cot.screens.world.entity.events.OnActionEvent
import nl.t64.cot.screens.world.entity.events.StateEvent


class PhysicsChest(private val chest: Loot) : PhysicsComponent() {

    private val stringBuilder: StringBuilder = StringBuilder()
    private var isSelected: Boolean = false

    override fun receive(event: Event) {
        if (event is LoadEntityEvent) {
            currentPosition = event.position
            setBoundingBox()
        }
        if (event is OnActionEvent) {
            if (event.playerDirection == Direction.NORTH
                && event.checkRect.overlaps(boundingBox)
            ) {
                isSelected = true
            }
        }
    }

    override fun update(entity: Entity, dt: Float) {
        if (isSelected) {
            isSelected = false
            tryToOpenChest(entity)
        }
    }

    override fun setBoundingBox() {
        boundingBox.set(currentPosition.x, currentPosition.y, Constant.TILE_SIZE, Constant.TILE_SIZE)
    }

    private fun tryToOpenChest(entity: Entity) {
        stringBuilder.setLength(0)
        if (isFailingOnTrap()) return
        if (isFailingOnLock()) return

        entity.send(StateEvent(EntityState.OPENED))
        showFindDialog()
    }

    private fun isFailingOnTrap(): Boolean {
        return chest.isTrapped() && isTrapToDifficult()
    }

    private fun isTrapToDifficult(): Boolean {
        val bestMechanic = gameData.party.getHeroWithHighestSkill(SkillItemId.MECHANIC)
        return if (canHandleTrapWith(bestMechanic)) {
            doHandleTrapWith(bestMechanic)
            false
        } else {
            dontHandleTrap()
            true
        }
    }

    private fun canHandleTrapWith(bestMechanic: HeroItem): Boolean {
        val bestMechanicLevel = bestMechanic.getCalculatedTotalSkillOf(SkillItemId.MECHANIC)
        return chest.canDisarmTrap(bestMechanicLevel)
    }

    private fun dontHandleTrap() {
        stringBuilder
            .append("There's a dangerous trap on this treasure chest.")
            .append(System.lineSeparator())
            .append("You need a level ${chest.trapLevel} Mechanic to disarm the trap.")
        brokerManager.componentObservers.notifyShowMessageDialog(stringBuilder.toString())
    }

    private fun doHandleTrapWith(bestMechanic: HeroItem) {
        stringBuilder.append("${bestMechanic.name} disarmed the trap")
        chest.disarmTrap()
    }

    private fun isFailingOnLock(): Boolean {
        return chest.isLocked() && isLockToDifficult()
    }

    private fun isLockToDifficult(): Boolean {
        val bestThief = gameData.party.getHeroWithHighestSkill(SkillItemId.THIEF)
        return if (canHandleLockWith(bestThief)) {
            doHandleLockWith(bestThief)
            false
        } else {
            dontHandleLock()
            true
        }
    }

    private fun canHandleLockWith(bestThief: HeroItem): Boolean {
        val bestThiefLevel = bestThief.getCalculatedTotalSkillOf(SkillItemId.THIEF)
        return chest.canPickLock(bestThiefLevel)
    }

    private fun dontHandleLock() {
        if (stringBuilder.toString().isBlank()) {
            stringBuilder
                .append("There's a lock on this treasure chest.")
                .append(System.lineSeparator())
        } else {
            stringBuilder
                .append(",")
                .append(System.lineSeparator())
                .append("but it seems the treasure chest is also locked.")
                .append(System.lineSeparator())
        }
        stringBuilder.append("You need a level ${chest.lockLevel} Thief to pick the lock.")
        brokerManager.componentObservers.notifyShowMessageDialog(stringBuilder.toString())
    }

    private fun doHandleLockWith(bestThief: HeroItem) {
        if (stringBuilder.toString().contains(bestThief.name)) {
            stringBuilder
                .append(System.lineSeparator())
                .append("and picked the lock")
        } else if (stringBuilder.toString().isBlank()) {
            stringBuilder
                .append("${bestThief.name} picked the lock")
        } else {
            stringBuilder
                .append(System.lineSeparator())
                .append(" and ${bestThief.name} picked the lock")
        }
        chest.pickLock()
    }

    private fun showFindDialog() {
        val message = finishStringBuilder()
        if (message.isBlank()) {
            brokerManager.componentObservers.notifyShowFindDialog(chest, AudioEvent.SE_CHEST)
        } else {
            brokerManager.componentObservers.notifyShowFindDialog(chest, AudioEvent.SE_CHEST, message)
        }
    }

    private fun finishStringBuilder(): String {
        if (stringBuilder.toString().isNotBlank()) {
            stringBuilder.append('.')
        }
        return stringBuilder.toString()
    }

    override fun debug(shapeRenderer: ShapeRenderer) {
        // empty
    }

}
