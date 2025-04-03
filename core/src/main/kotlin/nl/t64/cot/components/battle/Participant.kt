package nl.t64.cot.components.battle

import nl.t64.cot.Utils.preferenceManager
import nl.t64.cot.components.party.HeroItem
import nl.t64.cot.components.party.abilities.*
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

    fun setNegativeTurnCounter() {
        turnCounter = 0 - (10 + character.getCalculatedTotalStatOf(StatItemId.SPEED))
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
        if (isDelayingTurn) {
            isDelayingTurn = false
            return
        }

        if (amountOfTurns == 0) {
            return
        }

        currentAP = maximumAP + currentAP.coerceAtMost(2)
    }

    fun getPriorityFor(currentEnemy: Participant): Float {
        // todo, weaker character types? weapon types, or missile and throw?

        val attackSkill: SkillItemId = currentEnemy.getCurrentWeapon()!!.skill!!
        val targetSkill: SkillItemId? = getCurrentWeapon()?.skill

        val attackerHasAdvantageScore: Float = if (attackSkill.hasAdvantageOver(targetSkill)) -5f else 0f
        val attackerHasDisadvantageScore: Float = if (attackSkill.hasDisadvantageFrom(targetSkill)) 5f else 0f
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
        return character.getAllAbilities().map { createBattleAbilityItemFrom(it) }
    }

    private fun createBattleAbilityItemFrom(abilityItem: AbilityItem): BattleAbilityItem {
        return when (abilityItem.id) {
            AbilityItemId.BITE_1,
            AbilityItemId.BITE_2,
            AbilityItemId.BITE_3,
            AbilityItemId.BITE_4,
            AbilityItemId.BITE_5,
            AbilityItemId.BODY_SLAM_1,
            AbilityItemId.BODY_SLAM_2,
            AbilityItemId.BODY_SLAM_3 -> Strike(abilityItem, character)
            AbilityItemId.STRIKE_1,
            AbilityItemId.STRIKE_2,
            AbilityItemId.STRIKE_3,
            AbilityItemId.STRIKE_4 -> Strike(abilityItem, character)
            AbilityItemId.STAGGER -> Stagger(abilityItem, character)
        }
    }

    private fun getCurrentWeapon(): InventoryItem? {
        return character.getInventoryItem(InventoryGroup.WEAPON)
    }

}
