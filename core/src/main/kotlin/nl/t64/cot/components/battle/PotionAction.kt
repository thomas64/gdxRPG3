package nl.t64.cot.components.battle

import com.badlogic.gdx.graphics.Color
import nl.t64.cot.Utils.gameData
import nl.t64.cot.components.party.inventory.BattlePotionItem


private const val POTION_AP: Int = 3

class PotionAction(
    private val currentParticipant: Participant,
    private val selectedPotion: BattlePotionItem
) {
    private val character: Character = currentParticipant.character
    private val message = """
        ${selectedPotion.description}

        """

    fun isAble(): Pair<Boolean, String> {
        return if (currentParticipant.currentAP < POTION_AP) {
            Pair(false, (message + "Not enough AP!").trimIndent())
        } else {
            Pair(true, (message + "Do you want to drink a ${selectedPotion.name}? ($POTION_AP AP)").trimIndent())
        }
    }

    fun handle(): Pair<String, Color> {
        currentParticipant.currentAP -= POTION_AP
        gameData.inventory.autoRemoveItem(selectedPotion.id, 1)
        return character.drink(selectedPotion)
    }

    private fun Character.drink(potion: BattlePotionItem): Pair<String, Color> {
        return when (potion.id) {
            "healing_potion" -> recoverHp { recoverPartHp(20) }
            "curing_potion" -> recoverHp { recoverPartHp(80) }
            "restore_potion" -> recoverHp { recoverFullHp() }
            "energy_potion" -> recoverSp { recoverPartSp(20) }
            "endurance_potion" -> recoverSp { recoverPartSp(80) }
            "stamina_potion" -> recoverSp { recoverFullSp() }
            else -> throw NotImplementedError("ToDo")
        }
    }

    private inline fun Character.recoverHp(action: Character.() -> Unit): Pair<String, Color> {
        val oldHp = currentHp
        action()
        val recovered = currentHp - oldHp
        val text = if (recovered <= 0) "0" else "$recovered"
        return Pair(text, Color.GREEN)
    }

    private inline fun Character.recoverSp(action: Character.() -> Unit): Pair<String, Color> {
        val oldSp = currentSp
        action()
        val recovered = currentSp - oldSp
        val text = if (recovered <= 0) "0" else "$recovered"
        return Pair(text, Color.CYAN)
    }

}
