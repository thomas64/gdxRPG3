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
        val (recoveredHp, recoveredSp) = applyRecoveryEffects(potion)
        applyBuffEffects(potion)
        return determineDisplayResult(potion, recoveredHp, recoveredSp)
    }

    private fun Character.applyRecoveryEffects(potion: BattlePotionItem): Pair<Int, Int> {
        val effect = potion.inventoryItem

        val oldHp = this.currentHp
        val oldSp = this.currentSp

        if (effect.hp > 0) this.recoverPartHp(effect.hp)
        if (effect.sp > 0) this.recoverPartSp(effect.sp)

        return Pair(this.currentHp - oldHp, this.currentSp - oldSp)
    }

    private fun Character.applyBuffEffects(potion: BattlePotionItem) {
        val effect = potion.inventoryItem
        with(this.bonus) {
            when {
                effect.protection > 0 -> this.protectionFromPotion = effect.protection
                effect.intelligence > 0 -> this.intelligenceFromPotion = effect.intelligence
                effect.dexterity > 0 -> this.dexterityFromPotion = effect.dexterity
                effect.strength > 0 -> this.strengthFromPotion = effect.strength
                effect.speed > 0 -> this.speedFromPotion = effect.speed
                effect.willpower > 0 -> this.willpowerFromPotion = effect.willpower
                effect.stealth > 0 -> this.stealthFromPotion = effect.stealth
            }
        }
    }

    private fun determineDisplayResult(potion: BattlePotionItem,
                                       recoveredHp: Int,
                                       recoveredSp: Int): Pair<String, Color> {
        val effect = potion.inventoryItem

        return when {
            // @formatter:off
            effect.hp > 0 && effect.sp > 0  -> displayResultForRestore(recoveredHp, recoveredSp)
            effect.hp > 0                   -> Pair(formatRecovery(recoveredHp),    Color.GREEN)
            effect.sp > 0                   -> Pair(formatRecovery(recoveredSp),    Color.CYAN)
            effect.protection > 0           -> Pair("+${effect.protection} Prt",    Color.YELLOW)
            effect.intelligence > 0         -> Pair("+${effect.intelligence} Int",  Color.MAGENTA)
            effect.dexterity > 0            -> Pair("+${effect.dexterity} Dex",     Color.MAGENTA)
            effect.strength > 0             -> Pair("+${effect.strength} Str",      Color.MAGENTA)
            effect.speed > 0                -> Pair("+${effect.speed} Spd",         Color.MAGENTA)
            effect.willpower > 0            -> Pair("+${effect.willpower} Wil",     Color.MAGENTA)
            effect.stealth > 0              -> Pair("+${effect.stealth} Stl",       Color.LIGHT_GRAY)
            // @formatter:on
            else -> throw IllegalStateException("Effect of potion ${potion.name} unknown.")
        }
    }

    private fun displayResultForRestore(recoveredHp: Int, recoveredSp: Int): Pair<String, Color> {
        // Color exactly between GREEN and CYAN.
        return Pair("${formatRecovery(recoveredHp)}/${formatRecovery(recoveredSp)}", Color(0f, 1f, 0.5f, 1f))
    }

    private fun formatRecovery(amount: Int): String {
        return if (amount <= 0) "0" else "$amount"
    }

}
