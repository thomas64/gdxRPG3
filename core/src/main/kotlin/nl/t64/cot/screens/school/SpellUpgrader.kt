package nl.t64.cot.screens.school

import com.badlogic.gdx.scenes.scene2d.Stage
import nl.t64.cot.Utils.gameData
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.stopAllSe
import nl.t64.cot.components.party.HeroItem
import nl.t64.cot.components.party.abilities.AbilityItem
import nl.t64.cot.components.party.skills.SkillItemId
import nl.t64.cot.screens.dialog.MessageDialog
import nl.t64.cot.screens.dialog.QuestionDialog
import nl.t64.cot.screens.inventory.InventoryUtils


class SpellUpgrader private constructor(
    private val spellToUpgrade: AbilityItem,
    private val stage: Stage,
    private val setHasJustUpdatedToTrue: () -> Unit
) {

    companion object {
        fun upgradeSpell(spellToUpgrade: AbilityItem, stage: Stage, actionAfterSuccess: () -> Unit) {
            SpellUpgrader(spellToUpgrade, stage, actionAfterSuccess).upgrade()
        }
    }

    private val selectedHero: HeroItem = InventoryUtils.getSelectedHero()
    private val wizardSkill: Int = selectedHero.getSkillById(SkillItemId.WIZARD).rank
    private val spellName: String = spellToUpgrade.name

    private val hasSpell: Boolean = selectedHero.getAbilityById(spellToUpgrade.id) != null
    private val isWizard: Boolean = wizardSkill != -1
    private val hasWizardSkill: Boolean = wizardSkill >= 1
    private val hasEnoughWizardSkill: Boolean = wizardSkill >= spellToUpgrade.minSkill
    private val xpCost: Int = spellToUpgrade.calculateXpCost(selectedHero.totalXp)
    private val hasEnoughXp: Boolean = selectedHero.hasEnoughXpFor(xpCost)
    private val goldCost: Int = spellToUpgrade.goldCost
    private val hasEnoughGold: Boolean = gameData.inventory.hasEnoughOfItem("gold", goldCost)

    private fun upgrade() {
        when {
            selectedHero.isDead -> showError("${selectedHero.name} is deceased.")
            hasSpell -> showError("You already know $spellName.")
            !isWizard -> showError("Only wizards can learn spells.")
            !hasWizardSkill -> showError("You need the Wizard skill to learn spells.")
            !hasEnoughWizardSkill -> showError("Your Wizard skill is not high enough to learn $spellName.")
            !hasEnoughXp -> showError("I'm sorry. You don't seem to have enough XP.")
            !hasEnoughGold -> showError("I'm sorry. You don't seem to have enough gold.")
            else -> showConfirmDialog()
        }
    }

    private fun showError(message: String) {
        MessageDialog(message).show(stage, AudioEvent.SE_MENU_ERROR)
    }

    private fun showConfirmDialog() {
        val question = """
            Are you sure you wish to learn
            $spellName for $xpCost XP and $goldCost gold?""".trimIndent()
        val dialog = QuestionDialog(question) { upgradeSpell() }
        dialog.show(stage, AudioEvent.SE_CONVERSATION_NEXT, 0)
    }

    private fun upgradeSpell() {
        gameData.inventory.autoRemoveItem("gold", goldCost)
        selectedHero.learn(spellToUpgrade.id, xpCost)
        setHasJustUpdatedToTrue.invoke()
        showConfirmMessage()
    }

    private fun showConfirmMessage() {
        stopAllSe()
        MessageDialog("${spellToUpgrade.name} learned.").show(stage, AudioEvent.SE_UPGRADE)
    }

}
