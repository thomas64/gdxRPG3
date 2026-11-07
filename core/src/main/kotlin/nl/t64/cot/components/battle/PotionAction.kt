package nl.t64.cot.components.battle

import com.badlogic.gdx.graphics.Color
import nl.t64.cot.Utils.gameData
import nl.t64.cot.components.party.CalcAttributeId
import nl.t64.cot.components.party.inventory.BattlePotionItem
import nl.t64.cot.components.party.skills.SkillItemId
import nl.t64.cot.components.party.stats.StatItemId


const val POTION_AP: Int = 3

class PotionAction(
    private val currentParticipant: Participant,
    private val selectedPotion: BattlePotionItem,
    private val potionAp: Int = POTION_AP
) {
    private val character: Character = currentParticipant.character

    fun isAble(): Pair<Boolean, String> {
        return if (!character.canUsePotion(selectedPotion.inventoryItem)) {
            val message =
                """${selectedPotion.description}
                    |
                    |It would have no effect!""".trimIndent().trimMargin()
            Pair(false, message)
        } else if (currentParticipant.currentAP < potionAp) {
            val message =
                """${selectedPotion.description}
                    |
                    |Not enough AP!""".trimIndent().trimMargin()
            Pair(false, message)
        } else {
            val message =
                """${selectedPotion.description}
                    |
                    |Do you want to drink a ${selectedPotion.name}?${getApCostText()}""".trimIndent().trimMargin()
            Pair(true, message)
        }
    }

    fun handle(): List<StatChange> {
        currentParticipant.currentAP -= potionAp
        gameData.inventory.autoRemoveItem(selectedPotion.id, 1)
        return character.drink(selectedPotion)
    }

    private fun getApCostText(): String {
        return if (potionAp == 0) "" else " ($potionAp AP)"
    }

    private fun Character.drink(potion: BattlePotionItem): List<StatChange> {
        val (recoveredHp, recoveredSp) = this.applyRecoveryEffects(potion)
        this.applyBuffEffects(potion)
        return determineStatChanges(potion, recoveredHp, recoveredSp)
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
        this.bonus.applyPotion(potion.inventoryItem)
    }

    private fun determineStatChanges(potion: BattlePotionItem,
                                     recoveredHp: Int,
                                     recoveredSp: Int): List<StatChange> {
        val effect = potion.inventoryItem
        val isRecoveryPotion: Boolean = effect.hp > 0 || effect.sp > 0
        return if (isRecoveryPotion) {
            determineRecoveryChanges(recoveredHp, recoveredSp)
        } else {
            listOf(determineBuffChange(potion))
        }
    }

    // a recovery that had nothing left to restore is not shown.
    private fun determineRecoveryChanges(recoveredHp: Int, recoveredSp: Int): List<StatChange> {
        return listOfNotNull(
            StatChange("+$recoveredHp", "Hit Points", Color.GREEN).takeIf { recoveredHp > 0 },
            StatChange("+$recoveredSp", "Stamina Points", Color.CYAN).takeIf { recoveredSp > 0 }
        )
    }

    private fun determineBuffChange(potion: BattlePotionItem): StatChange {
        val effect = potion.inventoryItem

        return when {
            // @formatter:off
            effect.protection > 0   -> StatChange("+${effect.protection}%",  CalcAttributeId.PROTECTION.title, Color.YELLOW)
            effect.intelligence > 0 -> StatChange("+${effect.intelligence}", StatItemId.INTELLIGENCE.title,    Color.MAGENTA)
            effect.dexterity > 0    -> StatChange("+${effect.dexterity}",    StatItemId.DEXTERITY.title,       Color.MAGENTA)
            effect.strength > 0     -> StatChange("+${effect.strength}",     StatItemId.STRENGTH.title,        Color.MAGENTA)
            effect.speed > 0        -> StatChange("+${effect.speed}",        StatItemId.SPEED.title,           Color.MAGENTA)
            effect.willpower > 0    -> StatChange("+${effect.willpower}",    StatItemId.WILLPOWER.title,       Color.MAGENTA)
            effect.stealth > 0      -> StatChange("+${effect.stealth}",      SkillItemId.STEALTH.title,        Color.LIGHT_GRAY)
            // @formatter:on
            else -> throw IllegalStateException("Effect of potion ${potion.name} unknown.")
        }
    }

}
