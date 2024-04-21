package nl.t64.cot.screens.school

import com.badlogic.gdx.scenes.scene2d.Stage
import nl.t64.cot.Utils.gameData
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.stopAllSe
import nl.t64.cot.components.party.HeroItem
import nl.t64.cot.components.party.skills.SkillItemId
import nl.t64.cot.components.party.spells.SchoolType
import nl.t64.cot.components.party.spells.SpellItem
import nl.t64.cot.screens.inventory.InventoryUtils
import nl.t64.cot.screens.inventory.messagedialog.MessageDialog
import nl.t64.cot.screens.menu.DialogQuestion


class SpellUpgrader private constructor(
    teacherSpell: SpellItem,
    private val stage: Stage,
    private val setHasJustUpdatedToTrue: () -> Unit
) {

    companion object {
        fun upgradeSpell(spellToUpgrade: SpellItem, stage: Stage, actionAfterSuccess: () -> Unit) {
            SpellUpgrader(spellToUpgrade, stage, actionAfterSuccess).upgrade()
        }
    }

    private val selectedHero: HeroItem = InventoryUtils.getSelectedHero()
    private val spellToUpgrade: SpellItem = selectedHero.getSpellById(teacherSpell.id)
    private val wizardSkill: Int = selectedHero.getSkillById(SkillItemId.WIZARD).rank
    private val totalScholar: Int = selectedHero.getCalculatedTotalSkillOf(SkillItemId.SCHOLAR)
    private val spellName: String = spellToUpgrade.name

    private val isWizard: Boolean = selectedHero.school != SchoolType.NONE
    private val hasWizardSkill: Boolean = wizardSkill >= 1
    private val isCompatibleWithSpellSchool: Boolean =
        spellToUpgrade.school == selectedHero.school
                || spellToUpgrade.school == SchoolType.NEUTRAL
                || selectedHero.school == SchoolType.UNKNOWN
    private val hasEnoughWizardSkill: Boolean = wizardSkill >= spellToUpgrade.minWizard
    private val xpCost: Int = spellToUpgrade.getXpCostForNextRank(teacherSpell, wizardSkill, totalScholar)
    private val hasEnoughXp: Boolean = selectedHero.hasEnoughXpFor(xpCost)
    private val goldCost: Int = spellToUpgrade.getGoldCostForNextRank(teacherSpell, wizardSkill)
    private val hasEnoughGold: Boolean = gameData.inventory.hasEnoughOfItem("gold", goldCost)

    private fun upgrade() {
        when {
            !isWizard -> showError("Only wizards can learn spells.")
            !hasWizardSkill -> showError("You need the Wizard skill to learn spells.")
            !isCompatibleWithSpellSchool -> showError("You cannot learn $spellName as you are from the wrong school.")
            !hasEnoughWizardSkill -> showError("Your Wizard skill is not high enough to learn $spellName.")
            xpCost == -2 -> showError("I cannot teach you in $spellName any further.")
            xpCost == -1 -> throw IllegalStateException("Should have been handled by '!hasWizardSkill'.")
            xpCost == 0 -> showError("You cannot learn $spellName any further.")
            !hasEnoughXp -> showError("I'm sorry. You don't seem to have enough XP.")
            !hasEnoughGold -> showError("I'm sorry. You don't seem to have enough gold.")
            else -> showConfirmDialog()
        }
    }

    private fun showError(message: String) {
        MessageDialog(message).show(stage, AudioEvent.SE_MENU_ERROR)
    }

    private fun showConfirmDialog() {
        DialogQuestion({ upgradeSpell() }, """
                Are you sure you wish to learn
                $spellName for $xpCost XP and $goldCost gold?""".trimIndent())
            .show(stage, AudioEvent.SE_CONVERSATION_NEXT, 0)
    }

    private fun upgradeSpell() {
        gameData.inventory.autoRemoveItem("gold", goldCost)
        selectedHero.doUpgrade(spellToUpgrade, xpCost)
        setHasJustUpdatedToTrue.invoke()
        showConfirmMessage()
    }

    private fun showConfirmMessage() {
        stopAllSe()
        MessageDialog("""
                ${spellToUpgrade.name}:
                ${spellToUpgrade.rank - 1} -> ${spellToUpgrade.rank}""".trimIndent())
            .show(stage, AudioEvent.SE_UPGRADE)
    }

}
