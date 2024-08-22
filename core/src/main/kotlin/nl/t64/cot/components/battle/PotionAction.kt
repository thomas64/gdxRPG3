package nl.t64.cot.components.battle

import nl.t64.cot.Utils.gameData
import nl.t64.cot.components.party.inventory.BattlePotionItem


class PotionAction(
    private val character: Character,
    private val selectedPotion: BattlePotionItem
) {

    fun createConfirmationMessage(): String {
        return """
            ${selectedPotion.description}

            Do you want to drink a ${selectedPotion.name} ?""".trimIndent()
    }

    fun handle(): String {
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
