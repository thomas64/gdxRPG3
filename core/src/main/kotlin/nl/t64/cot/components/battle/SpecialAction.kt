package nl.t64.cot.components.battle

import nl.t64.cot.Utils.gameData
import nl.t64.cot.components.party.abilities.BattleAbilityItem
import nl.t64.cot.components.party.abilities.MagicShield


class SpecialAction(
    currentParticipant: Participant,
    currentTarget: Participant,
    selectedSpecial: BattleAbilityItem,
) : AttackAction(currentParticipant, currentTarget, selectedSpecial) {

    private val resource: String = selectedSpecial.abilityItem.requiredResource.title

    fun isAlreadyCast(): String? {
        return when (selectedAbility) {
            is MagicShield -> {
                if (currentTarget.isProtected) {
                    """${createPreviewMessage()}
                        |_________________
                        |
                        |[FIREBRICK]${selectedAbility.name} is already
                        |cast on ${target.name}!""".trimIndent().trimMargin()
                } else null
            }
            else -> throw IllegalArgumentException("SpecialAction does not support ${selectedAbility.name}.")
        }
    }

    fun isCostingTooMuchResources(): String? {
        return when {
            !selectedAbility.hasEnoughResources() -> {
                """${createPreviewMessage()}
                    |_________________
                    |
                    |[FIREBRICK]Not enough ${resource}s!""".trimIndent().trimMargin()
            }
            else -> null
        }
    }

    override fun createCommand(): String {
        return "Cast"
    }

    override fun handle(): List<AttackData> {
        currentParticipant.currentAP -= selectedAbility.ap
        instigator.currentSp -= selectedAbility.sp
        gameData.inventory.autoRemoveItem(resource.lowercase(), 1)

        return selectedAbility.handle()
    }

}
