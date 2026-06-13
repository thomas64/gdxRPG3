package nl.t64.cot.screens.inventory

import com.badlogic.gdx.scenes.scene2d.Stage
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.stopAllSe
import nl.t64.cot.components.party.HeroItem
import nl.t64.cot.components.party.skills.SkillItem
import nl.t64.cot.screens.dialog.MessageDialog
import nl.t64.cot.screens.dialog.QuestionDialog


class SkillSelfUpgrader private constructor(
    skillToUpgrade: SkillItem,
    private val stage: Stage,
    private val setHasJustUpdatedToTrue: () -> Unit
) {

    companion object {
        fun upgradeSkill(skillToUpgrade: SkillItem, stage: Stage, actionAfterSuccess: () -> Unit) {
            SkillSelfUpgrader(skillToUpgrade, stage, actionAfterSuccess).upgrade()
        }
    }

    private val selectedHero: HeroItem = InventoryUtils.getSelectedHero()
    private val skillToUpgrade: SkillItem = selectedHero.getSkillById(skillToUpgrade.id)
    private val skillName: String = skillToUpgrade.name

    private val xpCost: Int = skillToUpgrade.getSelfXpCostForNextRank()
    private val hasEnoughXp: Boolean = selectedHero.hasEnoughXpFor(xpCost)

    private fun upgrade() {
        when {
            selectedHero.isDead -> showError("${selectedHero.name} is deceased.")
            xpCost == 0 -> showError("You cannot train $skillName any further.")
            !hasEnoughXp -> showError("You need $xpCost XP Points to train $skillName.")
            else -> showConfirmDialog()
        }
    }

    private fun showError(message: String) {
        MessageDialog(message).show(stage, AudioEvent.SE_MENU_ERROR)
    }

    private fun showConfirmDialog() {
        val question = """
            Are you sure you wish to train
            ${selectedHero.name} with $skillName for $xpCost XP?""".trimIndent()
        val dialog = QuestionDialog(question) { upgradeSkill() }
        dialog.show(stage, AudioEvent.SE_CONVERSATION_NEXT, 0)
    }

    private fun upgradeSkill() {
        selectedHero.doUpgrade(skillToUpgrade, xpCost)
        setHasJustUpdatedToTrue.invoke()
        showConfirmMessage()
    }

    private fun showConfirmMessage() {
        stopAllSe()
        MessageDialog("""
                ${skillToUpgrade.name}:
                ${skillToUpgrade.rank - 1} -> ${skillToUpgrade.rank}""".trimIndent())
            .show(stage, AudioEvent.SE_UPGRADE)
    }

}
