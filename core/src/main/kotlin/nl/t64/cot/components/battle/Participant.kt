package nl.t64.cot.components.battle

import nl.t64.cot.components.party.HeroItem
import nl.t64.cot.components.party.inventory.InventoryGroup
import nl.t64.cot.components.party.stats.StatItemId


class Participant(
    val character: Character
) {
    val isHero: Boolean get() = character is HeroItem
    var turnCounter: Int = 0

    val maximumAP: Int = character.getCalculatedActionPoints()
    var currentAP: Int = maximumAP


    fun updateTurnCounter() {
        turnCounter += 10 + character.getCalculatedTotalStatOf(StatItemId.SPEED)
    }

    fun isTurnCounterAtMax(): Boolean {
        return turnCounter >= 200
    }

    fun resetTurnCounter() {
        turnCounter -= 200
    }

    fun refreshActionPoints() {
        currentAP = maximumAP
    }

    fun getWeaponRanges(): List<Int> {
        return character.getInventoryItem(InventoryGroup.WEAPON)?.getWeaponRange().orEmpty()
    }

}
