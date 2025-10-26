package nl.t64.cot.components.party.abilities

import nl.t64.cot.components.battle.AttackData
import nl.t64.cot.components.battle.Participant


abstract class HealingAbilityItem(
    abilityItem: AbilityItem,
    attacker: Participant
) : BattleAbilityItem(
    abilityItem,
    attacker
) {

    override fun createCopyForPreview(): BattleAbilityItem {
        throw IllegalStateException("HealingAbilityItem does not support preview copies.")
    }

    override fun handleSuccess(attackData: AttackData) {
        throw IllegalStateException("HealingAbilityItem does not support handleSuccess.")
    }

    override fun handle(): List<AttackData> {
        val specialData = AttackData()
        specialData.attacker = this.attacker.character.name
        specialData.target = this.target.character.name

        val healPoints: Int = calculateHealPoints()

        specialData.isHeal = true
        specialData.castMessage = "$healPoints"
        this.target.character.currentHp += healPoints

        return listOf(specialData)
    }

    private fun calculateHealPoints(): Int {
        val currentHp: Int = target.character.currentHp
        val maximumHp: Int = target.character.maximumHp
        val maxHealPoints: Int = calculateMaxHealPoints()
        return if (currentHp + maxHealPoints > maximumHp) maximumHp - currentHp else maxHealPoints
    }

    protected abstract fun calculateMaxHealPoints(): Int

    protected fun List<String>.alignCenter(): String {
        val maxLength: Int = this.maxOfOrNull { it.length } ?: return ""
        return this.joinToString(System.lineSeparator()) { line ->
            val padding: Int = (maxLength - line.length) / 2
            " ".repeat(padding) + line
        }
    }

}
