package nl.t64.cot.components.battle

import nl.t64.cot.Utils.gameData
import nl.t64.cot.components.party.inventory.BattlePotionItem


private const val POTION_AP: Int = 3

class PotionAction(
    private val currentParticipant: Participant,
    private val selectedPotion: BattlePotionItem
) {
    private val character: Character = currentParticipant.character

    fun isCostingTooMuchAp(): String? {
        return when {
            currentParticipant.currentAP < POTION_AP -> {
                """
                    ${selectedPotion.description}

                    Not enough AP!""".trimIndent()
            }
            else -> null
        }
    }

    fun createConfirmationMessage(): String {
        return """
            ${selectedPotion.description}

            Do you want to drink a ${selectedPotion.name} ($POTION_AP AP) ?""".trimIndent()
    }

    fun handle(): String {
        currentParticipant.currentAP -= POTION_AP
        gameData.inventory.autoRemoveItem(selectedPotion.id, 1)
        val oldHp = character.currentHp
        character.drink(selectedPotion)
        val newHp = character.currentHp
        val recoveredHp = newHp - oldHp // todo, dit gaat natuurlijk nog fout met niet-healing potions.
        if (recoveredHp <= 0) {
            return "${selectedPotion.name} had no effect."
        } else {
            return "${character.name} used a ${selectedPotion.name} and recovered $recoveredHp HP."
        }
    }

    private fun Character.drink(potion: BattlePotionItem) {
        when (potion.id) {
            "healing_potion" -> this.recoverPartHp(20)
            "curing_potion" -> this.recoverPartHp(80)
            "restore_potion" -> this.recoverFullHp()
            "energy_potion" -> this.recoverPartSp(20)
            "endurance_potion" -> this.recoverPartSp(80)
            "stamina_potion" -> this.recoverFullSp()
            else -> throw NotImplementedError("ToDo")
        }
    }

}
