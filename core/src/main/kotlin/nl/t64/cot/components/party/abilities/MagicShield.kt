package nl.t64.cot.components.party.abilities

import nl.t64.cot.components.battle.AttackData
import nl.t64.cot.components.battle.Participant
import nl.t64.cot.components.party.skills.SkillItemId


class MagicShield(
    abilityItem: AbilityItem,
    attacker: Participant
) : BattleAbilityItem(
    abilityItem,
    attacker
) {

    override fun createCopyForPreview(): BattleAbilityItem {
        throw IllegalStateException("MagicShield does not support preview copies.")
    }

    override fun handleSuccess(attackData: AttackData) {
        throw IllegalStateException("MagicShield does not support handleSuccess.")
    }

    override fun createPreviewMessage(): String {
        return """
            $name on ${target.character.name}

            Requires:
            ${abilityItem.requiredResource.title}

            Effect:
            +${calculateProtection()} Protection
        """.trimIndent().trimMargin()
    }

    override fun handle(): List<AttackData> {
        val specialData = AttackData()
        specialData.attacker = attacker.character.name
        specialData.target = target.character.name

        val bonusProtection: Int = calculateProtection()

        specialData.castMessage = "+$bonusProtection Prt"
        target.character.bonus.protectionFromSpell = bonusProtection

        return listOf(specialData)
    }

    fun isShieldActive(): Boolean {
        return target.character.bonus.protectionFromSpell > 0
    }

    private fun calculateProtection(): Int {
        return 2 * attacker.character.getCalculatedTotalSkillOf(SkillItemId.WIZARD)
    }

}
