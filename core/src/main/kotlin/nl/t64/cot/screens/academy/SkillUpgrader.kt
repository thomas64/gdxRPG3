package nl.t64.cot.screens.academy

import com.badlogic.gdx.scenes.scene2d.Stage
import nl.t64.cot.Utils.audioManager
import nl.t64.cot.Utils.gameData
import nl.t64.cot.audio.AudioCommand
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.components.party.HeroItem
import nl.t64.cot.components.party.skills.SkillItem
import nl.t64.cot.components.party.skills.SkillItemId
import nl.t64.cot.screens.inventory.InventoryUtils
import nl.t64.cot.screens.inventory.messagedialog.MessageDialog
import nl.t64.cot.screens.menu.DialogQuestion


class SkillUpgrader private constructor(
    trainerSkill: SkillItem,
    private val stage: Stage,
    private val setHasJustUpdatedToTrue: () -> Unit
) {

    companion object {
        fun upgradeSkill(skillToUpgrade: SkillItem, stage: Stage, actionAfterSuccess: () -> Unit) {
            SkillUpgrader(skillToUpgrade, stage, actionAfterSuccess).upgrade()
        }
    }

    private val selectedHero: HeroItem = InventoryUtils.getSelectedHero()
    private val skillToUpgrade: SkillItem = selectedHero.getSkillById(trainerSkill.id)
    private val totalScholar: Int = selectedHero.getCalculatedTotalSkillOf(SkillItemId.SCHOLAR)
    private val skillName: String = skillToUpgrade.name

    private val xpCost: Int = skillToUpgrade.getXpCostForNextLevel(trainerSkill, totalScholar)
    private val goldCost: Int = skillToUpgrade.getGoldCostForNextLevel(trainerSkill)
    private val hasEnoughXp: Boolean = selectedHero.hasEnoughXpFor(xpCost)
    private val hasEnoughGold: Boolean = gameData.inventory.hasEnoughOfItem("gold", goldCost)

    private fun upgrade() {
        when {
            xpCost == -2 -> showError("I cannot train you in $skillName any further.")
            xpCost == -1 -> showError("You cannot train $skillName.")
            xpCost == 0 -> showError("You cannot train $skillName any further.")
            !hasEnoughXp -> showError("I'm sorry. You don't seem to have enough 'XP to Invest'.")
            !hasEnoughGold -> showError("I'm sorry. You don't seem to have enough gold.")
            else -> showConfirmDialog()
        }
    }

    private fun showError(message: String) {
        MessageDialog(message).show(stage, AudioEvent.SE_MENU_ERROR)
    }

    private fun showConfirmDialog() {
        DialogQuestion({ upgradeSkill() }, """
                Are you sure you wish to train
                $skillName for $xpCost XP and $goldCost gold?""".trimIndent())
            .show(stage, AudioEvent.SE_CONVERSATION_NEXT, 0)
    }

    private fun upgradeSkill() {
        selectedHero.doUpgrade(skillToUpgrade, xpCost, goldCost)
        setHasJustUpdatedToTrue.invoke()
        showConfirmMessage()
    }

    private fun showConfirmMessage() {
        audioManager.handle(AudioCommand.SE_STOP_ALL)
        MessageDialog("""
                ${skillToUpgrade.name}:
                ${skillToUpgrade.rank - 1} -> ${skillToUpgrade.rank}""".trimIndent())
            .show(stage, AudioEvent.SE_UPGRADE)
    }

}
