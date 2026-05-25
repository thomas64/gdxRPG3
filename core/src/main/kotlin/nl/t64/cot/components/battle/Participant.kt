package nl.t64.cot.components.battle

import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.preferenceManager
import nl.t64.cot.components.party.HeroItem
import nl.t64.cot.components.party.abilities.*
import nl.t64.cot.components.party.inventory.InventoryGroup
import nl.t64.cot.components.party.inventory.InventoryItem
import nl.t64.cot.components.party.skills.SkillItemId
import nl.t64.cot.components.party.stats.StatItemId
import kotlin.math.roundToInt


private const val PENALTY_AP: Int = 4

class Participant(
    val character: Character
) {
    val isHero: Boolean get() = character is HeroItem
    var turnCounter: Int = 0

    val maximumAP: Int = character.getCalculatedActionPoints()
    var currentAP: Int = maximumAP
    var staggerChance: Float = 65f
    var fleeChance: Int = 70

    private var isDelayingTurn: Boolean = false
    private var isStaggered: Boolean = false
    private var amountOfTurns: Int = 0

    var performingType: AbilityItemId? = null
    val isPerforming: Boolean = performingType != null


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
        // todo, weaker character types? weapon types, or bow and throw?

        val attackSkill: SkillItemId = currentEnemy.getCurrentWeapon()!!.skill!!
        val targetSkill: SkillItemId? = getCurrentWeapon()?.skill

        val nextToTargetScore: Int = if (isEnemyNextToHero) -2 else 0
        val attackerHasAdvantageScore: Int = if (attackSkill.hasAdvantageOver(targetSkill)) -4 else 0
        val attackerHasDisadvantageScore: Int = if (attackSkill.hasDisadvantageFrom(targetSkill)) 4 else 0
        val stealthScore: Int = character.getCalculatedTotalSkillOf(SkillItemId.STEALTH)
        val protectionScore: Float = character.getCalculatedTotalProtection() / 5f // todo, magic attackers moeten letten op magic protection

        if (preferenceManager.isDebugModeOn) {
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

    fun getPenaltyAp(): Int {
        return (PENALTY_AP - (character.getCalculatedTotalSkillOf(SkillItemId.STEALTH) / 3f)).toInt().coerceAtLeast(0)
    }

    fun getWeaponRanges(): List<Int> {
        return getCurrentWeapon()?.getWeaponRange().orEmpty()
    }

    fun getBattleAbilities(): List<BattleAbilityItem> {
        return character.getAllAbilities().map { createBattleAbilityItemFrom(it) }
    }

    fun handlePossibleStagger(): String? {
        if (isStaggered) {
            isStaggered = false
            return "${character.name} is staggered."
        }
        return null
    }

    fun getCurrentWeapon(): InventoryItem? {
        return character.getInventoryItem(InventoryGroup.WEAPON)
    }

    fun resetAllTemporaryBattleEffects() {
        character.bonus.reset()
        dequipDisallowedWeapon()
        stopPerforming()
    }

    fun startPerforming(abilityItemId: AbilityItemId) {
        performingType = abilityItemId
        currentAP = 0
    }

    fun stopPerforming() {
        performingType = null
    }

    fun calculatePerformBonus(troubadourRank: Int): Int {
        val baseHit: Int = character.getCalculatedTotalHit()
        return (0.35f * (100 - baseHit) * (troubadourRank / 10f)).roundToInt()
    }

    fun calculatePerformPenalty(troubadourRank: Int): Int {
        val baseHit: Int = character.getCalculatedTotalHit()
        return (0.2f * baseHit * (troubadourRank / 10f)).roundToInt()
    }

    private fun dequipDisallowedWeapon() {
        val hero = character as HeroItem
        hero.getInventoryItem(InventoryGroup.WEAPON)?.let { weapon ->
            hero.createMessageIfHeroHasNotEnoughFor(weapon)?.let { _ ->
                hero.clearInventoryItemFor(InventoryGroup.WEAPON)
                gameData.inventory.autoSetItem(weapon)
            }
        }
    }

    private fun createBattleAbilityItemFrom(abilityItem: AbilityItem): BattleAbilityItem {
        return when (abilityItem.id) {
            AbilityItemId.BITE_3,
            AbilityItemId.BITE_4,
            AbilityItemId.BODY_SLAM_2,
            AbilityItemId.SCRATCH_2 -> Strike(abilityItem, this)
            AbilityItemId.STRIKE_2,
            AbilityItemId.STRIKE_3,
            AbilityItemId.STRIKE_3F,
            AbilityItemId.STRIKE_4 -> Strike(abilityItem, this)
            AbilityItemId.STAGGER -> Stagger(abilityItem, this)
            AbilityItemId.SNIPER_ARROW -> TODO()
            AbilityItemId.BRUTE_FORCE -> TODO()
            AbilityItemId.SHIELD_BASH -> TODO()
            AbilityItemId.DOUBLE_THROW -> DoubleThrow(abilityItem, this)
            AbilityItemId.GHOST_TOUCH -> TODO()
            AbilityItemId.BACKSTAB -> TODO()

            AbilityItemId.LAY_ON_HANDS -> LayOnHands(abilityItem, this)
            AbilityItemId.HEAL_WITH_HERBS -> HealWithHerbs(abilityItem, this)
            AbilityItemId.PERFORM_BEAUTY -> PerformBeauty(abilityItem, this)
            AbilityItemId.PERFORM_CHAOS -> PerformChaos(abilityItem, this)

            AbilityItemId.FIRE,
            AbilityItemId.ELFIRE,
            AbilityItemId.ARCFIRE,
            AbilityItemId.REXFIRE,
            AbilityItemId.WIND,
            AbilityItemId.ELWIND,
            AbilityItemId.ARCWIND,
            AbilityItemId.REXWIND,
            AbilityItemId.THUNDER,
            AbilityItemId.ELTHUNDER,
            AbilityItemId.ARCTHUNDER,
            AbilityItemId.REXTHUNDER -> ElementalAttack(abilityItem, this)
            AbilityItemId.MAGIC_SHIELD -> MagicShield(abilityItem, this)
            AbilityItemId.RESISTANCE -> TODO()
            AbilityItemId.TELEPORTATION -> TODO()
            AbilityItemId.BRILLIANCE -> Brilliance(abilityItem, this)
            AbilityItemId.STUPIDITY -> Stupidity(abilityItem, this)
            AbilityItemId.FINESSE -> Finesse(abilityItem, this)
            AbilityItemId.CLUMSINESS -> Clumsiness(abilityItem, this)
            AbilityItemId.MIGHT -> Might(abilityItem, this)
            AbilityItemId.DEBILITATION -> Debilitation(abilityItem, this)
            AbilityItemId.HASTE -> Haste(abilityItem, this)
            AbilityItemId.SLUGGISHNESS -> Sluggishness(abilityItem, this)
        }
    }

}
