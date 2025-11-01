package nl.t64.cot.components.party.abilities

import nl.t64.cot.components.battle.AttackData
import nl.t64.cot.components.battle.Participant
import nl.t64.cot.components.party.skills.SkillItemId


abstract class BuffAbilityItem(
    abilityItem: AbilityItem,
    attacker: Participant
) : BattleAbilityItem(
    abilityItem,
    attacker
) {

    override fun createCopyForPreview(): BattleAbilityItem {
        throw IllegalStateException("BuffAbilityItem does not support preview copies.")
    }

    override fun handleSuccess(attackData: AttackData) {
        throw IllegalStateException("BuffAbilityItem does not support handleSuccess.")
    }

    override fun createPreviewMessage(): String {
        return """
            $name on ${target.character.name}

            Effect:
            ${getBuffDescription()}
        """.trimIndent().trimMargin()
    }

    override fun handle(): List<AttackData> {
        val specialData = AttackData()
        specialData.attacker = attacker.character.name
        specialData.target = target.character.name

        val bonusValue: Int = calculateBonusValue()

        specialData.castMessage = getCastMessage(bonusValue)
        applyBuff(bonusValue)

        return listOf(specialData)
    }

    protected fun calculateBonusValue(): Int {
        return attacker.character.getCalculatedTotalSkillOf(SkillItemId.WIZARD)
    }

    abstract fun isBuffActive(): Boolean
    protected abstract fun getBuffDescription(): String
    protected abstract fun getCastMessage(bonusValue: Int): String
    protected abstract fun applyBuff(bonusValue: Int)

}

