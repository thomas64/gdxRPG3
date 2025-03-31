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

    fun getPriorityFor(currentEnemy: Participant): Float {
        // todo, weaker character types? weapon types, or missile and throw?

        val attackSkill: SkillItemId = currentEnemy.getCurrentWeapon()!!.skill!!
        val targetSkill: SkillItemId? = getCurrentWeapon()?.skill

        val attackerHasAdvantageScore: Float = if (attackSkill.hasAdvantageOver(targetSkill)) -2.5f else 0f
        val attackerHasDisadvantageScore: Float = if (attackSkill.hasDisadvantageFrom(targetSkill)) 2.5f else 0f
        val stealthScore: Int = character.getCalculatedTotalSkillOf(SkillItemId.STEALTH)
        val protectionScore: Float = character.getCalculatedTotalProtection() / 5f

        if (preferenceManager.isInDebugMode) {
            println("A low score means a high priority in the queue!")
            println("${character.name} " +
                        "attackerHasAdvantageScore: $attackerHasAdvantageScore, " +
                        "attackerHasDisadvantageScore: $attackerHasDisadvantageScore, " +
                        "stealthScore: $stealthScore, " +
                        "protectionScore: $protectionScore = " +
                        "total: ${stealthScore + protectionScore + attackerHasAdvantageScore + attackerHasDisadvantageScore}")
        }
        return stealthScore + protectionScore + attackerHasAdvantageScore + attackerHasDisadvantageScore
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
