package nl.t64.cot.components.party.abilities

import nl.t64.cot.components.battle.Participant


class DoubleThrow(
    abilityItem: AbilityItem,
    attacker: Participant
) : Strike(
    abilityItem,
    attacker
) {

    override fun createCopyForPreview(): BattleAbilityItem {
        return DoubleThrow(abilityItem.copy(isPreview = true), attacker)
    }

    override fun createPreviewMessage(): String {
        return currentWeapon?.let {
            """
                $name
                ${it.name} ${it.getDurabilityText()}
                ${createEffectiveMessage()}
                Mod hit: ${String.format("%3d", calculateHitPercentageCapped())} %
                Damage:  ${String.format("%3d", calculateDamage())} [BLUE]x2[BLACK]
                Crit:    ${String.format("%3d", calculateCriticalHitPercentage())} %
            """.trimIndent().trimMargin()
        } ?: createNoWeaponMessage()
    }

    override fun handle(messages: ArrayDeque<String>) {
        super.handle(messages)

        if (target.character.isDead) return

        messages.add("${attacker.character.name} used $name on ${target.character.name}.")

        super.handle(messages)
    }

}
