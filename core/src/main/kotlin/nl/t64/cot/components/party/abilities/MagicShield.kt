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

            Effect:
            +${calculateProtection()} Protection
        """.trimIndent().trimMargin()
    }

    override fun handle(): List<AttackData> {
        val specialData = AttackData()
        specialData.attacker = this.attacker.character.name
        specialData.target = this.target.character.name

        val bonusProtection: Int = calculateProtection()

        specialData.castMessage = "+$bonusProtection Prt"
        this.target.isProtected = true
        this.target.character.temporaryProtection = bonusProtection

        return listOf(specialData)
    }

    private fun calculateProtection(): Int {
        return 2 * attacker.character.getCalculatedTotalSkillOf(SkillItemId.WIZARD)
    }

}
