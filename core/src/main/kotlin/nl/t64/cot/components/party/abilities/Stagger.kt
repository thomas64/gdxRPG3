package nl.t64.cot.components.party.abilities

import nl.t64.cot.Utils.screenManager
import nl.t64.cot.components.battle.AttackData
import nl.t64.cot.components.battle.Participant
import nl.t64.cot.components.battle.TurnManager
import nl.t64.cot.components.party.skills.SkillItemId
import nl.t64.cot.constants.ScreenType
import nl.t64.cot.screens.battle.BattleScreen
import java.lang.reflect.Field
import kotlin.math.roundToInt
import kotlin.random.Random


class Stagger(
    abilityItem: AbilityItem,
    attacker: Participant
) : BattleAbilityItem(
    abilityItem,
    attacker
) {

    override fun createCopyForPreview(): BattleAbilityItem {
        return Stagger(abilityItem.copy(isPreview = true), attacker)
    }

    override fun createPreviewMessage(): String {
        return currentWeapon?.let {
            """
                $name
                ${it.name} ${it.getDurabilityText()}
                ${createEffectiveMessage()}
                Mod hit: ${String.format("%3d", calculateHitPercentageForVisual())} %
                Stagger: ${String.format("%3d", calculateStaggerPercentage())} %
                Damage:  ${String.format("%3d", calculateDamageForVisual())}
                Crit:    ${String.format("%3d", calculateCriticalHitPercentage())} %
            """.trimIndent().trimMargin()
        } ?: createNoWeaponMessage()
    }

    override fun handleSuccess(attackData: AttackData) {
        val isCriticalHit: Boolean = calculateCriticalHitPercentage() > Random.nextInt(0, 100)
        val damageDone: Int = if (isCriticalHit) calculateCriticalDamage() else calculateDamage()
        val isStaggered: Boolean = calculateStaggerPercentage() > Random.nextInt(0, 100)

        if (isStaggered) getTurnManagerTheUglyWay().stagger(target)
        target.character.takeDamage(damageDone)

        attackData.hasAdvantage = hasWeaponTriangleAdvantage()
        attackData.hasDisadvantage = hasWeaponTriangleDisadvantage()
        attackData.isCriticalHit = isCriticalHit
        attackData.damage = damageDone
        attackData.isStaggered = isStaggered
    }

    private fun calculateStaggerPercentage(): Int {
        val attackerWarriorRank: Int = attacker.character.getCalculatedTotalSkillOf(SkillItemId.WARRIOR)
        if (attackerWarriorRank == 0) return 0
        val warriorChance: Float = (target.staggerChance / 100f) * (5f * attackerWarriorRank)
        return (target.staggerChance + warriorChance).roundToInt().coerceAtMost(100)
    }

    private fun getTurnManagerTheUglyWay(): TurnManager {
        val battleScreen = screenManager.getScreen(ScreenType.BATTLE) as BattleScreen
        val turnManager: Field = BattleScreen::class.java.getDeclaredField("turnManager")
        turnManager.isAccessible = true
        return turnManager.get(battleScreen) as TurnManager
    }

}
