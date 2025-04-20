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
    var staggerChance: Float = 65f
    var fleeChance: Int = 10

    private var isDelayingTurn: Boolean = false
    private var isStaggered: Boolean = false
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

    fun stagger() {
        isStaggered = true
    }

    fun refreshActionPoints() {
        if (isDelayingTurn || isStaggered) {
            isDelayingTurn = false
            return
        }

        if (amountOfTurns == 0) {
            return
        }

        currentAP = maximumAP + currentAP.coerceAtMost(2)
    }

    fun getPriorityFor(currentEnemy: Participant, isEnemyNextToHero: Boolean): Float {
        // todo, weaker character types? weapon types, or missile and throw?

        val attackSkill: SkillItemId = currentEnemy.getCurrentWeapon()!!.skill!!
        val targetSkill: SkillItemId? = getCurrentWeapon()?.skill

        val nextToTargetScore: Int = if (isEnemyNextToHero) -2 else 0
        val attackerHasAdvantageScore: Int = if (attackSkill.hasAdvantageOver(targetSkill)) -4 else 0
        val attackerHasDisadvantageScore: Int = if (attackSkill.hasDisadvantageFrom(targetSkill)) 4 else 0
        val stealthScore: Int = character.getCalculatedTotalSkillOf(SkillItemId.STEALTH)
        val protectionScore: Float = character.getCalculatedTotalProtection() / 5f

        if (preferenceManager.isInDebugMode) {
            println("A low score means a high priority in the queue!")
            println("${character.name} " +
                        "nextToTargetScore: $nextToTargetScore, " +
                        "attackerHasAdvantageScore: $attackerHasAdvantageScore, " +
                        "attackerHasDisadvantageScore: $attackerHasDisadvantageScore, " +
                        "stealthScore: $stealthScore, " +
                        "protectionScore: $protectionScore = " +
                        "total: ${stealthScore + protectionScore + attackerHasAdvantageScore + attackerHasDisadvantageScore + nextToTargetScore}")
        }
        return stealthScore + protectionScore + attackerHasAdvantageScore + attackerHasDisadvantageScore + nextToTargetScore
    }

    fun getWeaponRanges(): List<Int> {
        return getCurrentWeapon()?.getWeaponRange().orEmpty()
    }

    fun getBattleAbilities(): List<BattleAbilityItem> {
        return character.getAllAbilities().map { createBattleAbilityItemFrom(it) }
    }

    fun getEndingTurnMessage(): ArrayDeque<String> {
        if (isStaggered) {
            isStaggered = false
            return ArrayDeque(listOf("${character.name} is staggered."))
        }
        return ArrayDeque(listOf("${character.name} ended ${character.gender} turn."))
    }

    fun getCurrentWeapon(): InventoryItem? {
        return character.getInventoryItem(InventoryGroup.WEAPON)
    }

    private fun createBattleAbilityItemFrom(abilityItem: AbilityItem): BattleAbilityItem {
        return when (abilityItem.id) {
            AbilityItemId.BITE_3,
            AbilityItemId.BITE_4,
            AbilityItemId.BODY_SLAM_2 -> Strike(abilityItem, this)
            AbilityItemId.STRIKE_2,
            AbilityItemId.STRIKE_3,
            AbilityItemId.STRIKE_4 -> Strike(abilityItem, this)
            AbilityItemId.STAGGER -> Stagger(abilityItem, this)
            AbilityItemId.DOUBLE_THROW -> DoubleThrow(abilityItem, this)
        }
    }

}
