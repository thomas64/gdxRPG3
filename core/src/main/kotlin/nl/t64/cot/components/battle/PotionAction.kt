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

    private enum class RecoveryType(val color: Color) {
        HP(Color.GREEN),
        SP(Color.CYAN),
        BOTH(Color(0f, 1f, 0.5f, 1f)) // Exactly between GREEN and CYAN
    }

    private data class PotionEffect(
        val type: RecoveryType,
        val hpAmount: Int = 0,
        val spAmount: Int = 0
    )

    fun isAble(): Pair<Boolean, String> {
        return if (currentParticipant.currentAP < POTION_AP) {
            val message =
                """${selectedPotion.description}
                    |
                    |Not enough AP!""".trimIndent().trimMargin()
            Pair(false, message)
        } else {
            val message =
                """${selectedPotion.description}
                    |
                    |Do you want to drink a ${selectedPotion.name}? ($POTION_AP AP)""".trimIndent().trimMargin()
            Pair(true, message)
        }
    }

    fun handle(): Pair<String, Color> {
        currentParticipant.currentAP -= POTION_AP
        gameData.inventory.autoRemoveItem(selectedPotion.id, 1)
        return character.drink(selectedPotion)
    }

    private fun Character.drink(potion: BattlePotionItem): Pair<String, Color> {
        val effect: PotionEffect = when (potion.id) {
            "healing_potion" -> PotionEffect(RecoveryType.HP, hpAmount = 20)
            "healing_potion_+" -> PotionEffect(RecoveryType.HP, hpAmount = 80)
            "healing_potion_++" -> PotionEffect(RecoveryType.HP, hpAmount = 200)
            "energy_potion" -> PotionEffect(RecoveryType.SP, spAmount = 10)
            "energy_potion_+" -> PotionEffect(RecoveryType.SP, spAmount = 30)
            "energy_potion_++" -> PotionEffect(RecoveryType.SP, spAmount = 70)
            "restore_potion" -> PotionEffect(RecoveryType.BOTH, hpAmount = 20, spAmount = 10)
            "restore_potion_+" -> PotionEffect(RecoveryType.BOTH, hpAmount = 80, spAmount = 30)
            "restore_potion_++" -> PotionEffect(RecoveryType.BOTH, hpAmount = 200, spAmount = 70)
            else -> throw NotImplementedError("ToDo")
        }
        return this.applyEffect(effect)
    }

    private fun Character.applyEffect(effect: PotionEffect): Pair<String, Color> {
        val oldHp = currentHp
        val oldSp = currentSp

        if (effect.hpAmount > 0) recoverPartHp(effect.hpAmount)
        if (effect.spAmount > 0) recoverPartSp(effect.spAmount)

        val recoveredHp = currentHp - oldHp
        val recoveredSp = currentSp - oldSp

        val text: String = when (effect.type) {
            RecoveryType.HP -> formatRecovery(recoveredHp)
            RecoveryType.SP -> formatRecovery(recoveredSp)
            RecoveryType.BOTH -> "${formatRecovery(recoveredHp)}/${formatRecovery(recoveredSp)}"
        }

        return Pair(text, effect.type.color)
    }

    private fun formatRecovery(amount: Int): String {
        return if (amount <= 0) "0" else "$amount"
    }

}
