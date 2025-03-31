package nl.t64.cot.components.battle

import nl.t64.cot.Utils.preferenceManager
import nl.t64.cot.components.party.HeroItem
import nl.t64.cot.components.party.abilities.BattleAbilityItem
import nl.t64.cot.components.party.inventory.InventoryGroup
import nl.t64.cot.components.party.inventory.InventoryItem
import nl.t64.cot.components.party.skills.SkillItemId
import nl.t64.cot.components.party.stats.StatItemId


class Participant(
    val character: Character
) {
    val isHero: Boolean get() = character is HeroItem
    var turnCounter: Int = 0

    val maximumAP: Int = character.getCalculatedActionPoints()
    var currentAP: Int = maximumAP

    private var isDelayingTurn: Boolean = false
    private var amountOfTurns: Int = 0

    fun updateTurnCounter() {
        turnCounter += 10 + character.getCalculatedTotalStatOf(StatItemId.SPEED)
    }

    fun isTurnCounterAtMax(): Boolean {
        return turnCounter >= 200
    }

    fun resetTurnCounter() {
        turnCounter -= 200
        amountOfTurns++
    }

    fun delayTurn() {
        isDelayingTurn = true
    }

    fun refreshActionPoints() {
        if (amountOfTurns == 0) {
            return
        }
        if (isDelayingTurn) {
            isDelayingTurn = false
            return
        }

        currentAP = maximumAP + currentAP.coerceAtMost(2)
    }

    fun getPriority(): Float {
        // todo, weaker character types? weapon types, wpn triangle, or missile and throw?

        val stealthScore: Int = character.getCalculatedTotalSkillOf(SkillItemId.STEALTH)
        val protectionScore: Float = character.getCalculatedTotalProtection() / 5f
        if (preferenceManager.isInDebugMode) {
            println("${character.name} stealthScore: $stealthScore, " +
                        "protectionScore: $protectionScore = total: ${stealthScore + protectionScore}")
        }
        return stealthScore + protectionScore
    }

    fun getWeaponRanges(): List<Int> {
        return getCurrentWeapon()?.getWeaponRange().orEmpty()
    }

    fun getBattleAbilities(): List<BattleAbilityItem> {
        val currentWeapon: InventoryItem? = getCurrentWeapon()
        return character.getAllAbilities().map { BattleAbilityItem(it, currentWeapon) }
    }

    private fun getCurrentWeapon(): InventoryItem? {
        return character.getInventoryItem(InventoryGroup.WEAPON)
    }

}
