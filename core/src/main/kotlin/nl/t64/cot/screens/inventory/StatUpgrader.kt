package nl.t64.cot.screens.inventory

import com.badlogic.gdx.scenes.scene2d.Stage
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.stopAllSe
import nl.t64.cot.components.party.HeroItem
import nl.t64.cot.components.party.stats.StatItem
import nl.t64.cot.screens.inventory.messagedialog.MessageDialog
import nl.t64.cot.screens.menu.DialogQuestion


class StatUpgrader private constructor(
    private val statToUpgrade: StatItem,
    private val stage: Stage,
    private val setHasJustUpdatedToTrue: () -> Unit
) {

    companion object {
        fun upgradeStat(statToUpgrade: StatItem, stage: Stage, actionAfterSuccess: () -> Unit) {
            StatUpgrader(statToUpgrade, stage, actionAfterSuccess).upgrade()
        }
    }

    private val selectedHero: HeroItem = InventoryUtils.getSelectedHero()
    private val statName: String = statToUpgrade.name
    private val xpCost: Int = statToUpgrade.getXpCostForNextRank()
    private val hasEnoughXp: Boolean = selectedHero.hasEnoughXpFor(xpCost)

    private fun upgrade() {
        when {
            xpCost == 0 -> showError("You cannot train $statName any further.")
            !hasEnoughXp -> showError("You need $xpCost 'XP to Invest' to train $statName.")
            else -> showConfirmDialog()
        }
    }

    private fun showError(message: String) {
        MessageDialog(message).show(stage, AudioEvent.SE_MENU_ERROR)
    }

    private fun showConfirmDialog() {
        DialogQuestion({ upgradeStat() }, """
                Are you sure you wish to train 
                $statName for $xpCost XP?""".trimIndent())
            .show(stage, AudioEvent.SE_CONVERSATION_NEXT, 0)
    }

    private fun upgradeStat() {
        selectedHero.doUpgrade(statToUpgrade, xpCost)
        setHasJustUpdatedToTrue.invoke()
        showConfirmMessage()
    }

    private fun showConfirmMessage() {
        stopAllSe()
        MessageDialog("""
                ${statToUpgrade.name}:
                ${statToUpgrade.rank - 1} -> ${statToUpgrade.rank}""".trimIndent())
            .show(stage, AudioEvent.SE_UPGRADE)
    }

}
