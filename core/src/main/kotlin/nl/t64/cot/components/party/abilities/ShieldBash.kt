package nl.t64.cot.components.party.abilities

import nl.t64.cot.components.battle.AttackData
import nl.t64.cot.components.battle.Participant
import nl.t64.cot.components.party.CalcAttributeId
import nl.t64.cot.components.party.inventory.InventoryGroup
import nl.t64.cot.components.party.inventory.InventoryItem


class ShieldBash(
    abilityItem: AbilityItem,
    attacker: Participant
) : BattleAbilityItem(
    abilityItem,
    attacker
) {

    private val currentShield: InventoryItem? get() = attacker.character.getInventoryItem(InventoryGroup.SHIELD)

    override fun createCopyForPreview(): BattleAbilityItem {
        return ShieldBash(abilityItem.copy(isPreview = true), attacker)
    }

    override fun createPreviewMessage(): String {
        return currentShield?.let {
            val defense: Int = it.getAttributeOfCalcAttributeId(CalcAttributeId.DEFENSE)
            """
                $name
                ${it.name} ${it.getDurabilityText()}
                [GRAY]Neither advantage
                nor disadvantage.[BLACK]

                Mod hit: 100 %
                Damage:  ${String.format("%3d", defense)}
                Crit:      0 %
            """.trimIndent().trimMargin()
        } ?: createNoShieldMessage()
    }

    override fun isWeaponAllowed(): Boolean {
        return abilityItem.isWeaponAllowed(currentShield)
    }

    override fun isHit(): Boolean {
        return true
    }

    override fun handleDurability(attackData: AttackData) {
        val shield: InventoryItem = currentShield!!
        shield.durability--
        if (attacker.isHero && shield.durability <= 0) {
            attackData.attackerShieldBrokeMessage = "Your ${shield.name} broke!"
            attacker.character.clearInventoryItemFor(InventoryGroup.SHIELD)
        }
    }

    override fun handleSuccess(attackData: AttackData) {
        val damageDone: Int = currentShield!!.getAttributeOfCalcAttributeId(CalcAttributeId.DEFENSE)
        target.character.takeDamage(damageDone)
        attackData.damage = damageDone
    }

    private fun createNoShieldMessage(): String {
        return """
            $name

            No shield equipped.
        """.trimIndent()
    }

}
