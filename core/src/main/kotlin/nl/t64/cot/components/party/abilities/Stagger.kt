package nl.t64.cot.components.party.abilities

import nl.t64.cot.Utils.screenManager
import nl.t64.cot.components.battle.Character
import nl.t64.cot.components.battle.TurnManager
import nl.t64.cot.constants.ScreenType
import nl.t64.cot.screens.battle.BattleScreen
import java.lang.reflect.Field
import kotlin.random.Random


class Stagger(
    abilityItem: AbilityItem,
    attacker: Character
) : BattleAbilityItem(
    abilityItem,
    attacker
) {

    override fun toString(): String {
        return "$name (${abilityItem.ap} AP, ${abilityItem.sp} SP)"
    }

    override fun createPreviewMessage(): String {
        return currentWeapon?.let {
            """
                $this

                Target: ${target.name}
                Weapon: ${it.name}
                Durability: ${it.durability}
                ${it.getRangeText()}
                ${possibleCreateEffectiveMessage()}
                Chance to hit: ${calculateHitPercentageCapped()}%
                Damage: ${calculateDamage()}
                Critical chance: ${calculateCriticalHitPercentage()}%
                Critical damage: ${calculateCriticalDamage()}

                Moves the target to the
                bottom of the turn order.
            """.trimIndent()
        } ?: """
            $this

            Target: ${target.name}

            No weapon equipped!
        """.trimIndent()
    }

    override fun handleSuccess(messages: ArrayDeque<String>) {
        val turnManager: TurnManager = getTurnManagerTheUglyWay()
        turnManager.stagger(target)

        val isCriticalHit: Boolean = calculateCriticalHitPercentage() > Random.nextInt(0, 100)
        val damage: Int = calculateDamage()
        val criticalDamage: Int = calculateCriticalDamage()
        val damageDone: Int = if (isCriticalHit) criticalDamage else damage

        target.takeDamage(damageDone)

        val critMessage: String = if (isCriticalHit) "A critical hit!  " else ""
        messages.add("""
            $critMessage$name successfully did $damageDone damage.
            ${target.name} went to the bottom of the turn order.
            """.trimIndent()
        )

        possibleAddEffectiveMessage(messages)
    }

    private fun getTurnManagerTheUglyWay(): TurnManager {
        val battleScreen = screenManager.getScreen(ScreenType.BATTLE) as BattleScreen
        val turnManager: Field = BattleScreen::class.java.getDeclaredField("turnManager")
        turnManager.isAccessible = true
        return turnManager.get(battleScreen) as TurnManager
    }

}
