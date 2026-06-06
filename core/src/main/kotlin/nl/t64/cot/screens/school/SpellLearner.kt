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


class SpellLearner private constructor(
    private val spellToLearn: AbilityItem,
    private val stage: Stage,
    private val setHasJustUpdatedToTrue: () -> Unit
) {

    companion object {
        fun learnSpell(spellToLearn: AbilityItem, stage: Stage, actionAfterSuccess: () -> Unit) {
            SpellLearner(spellToLearn, stage, actionAfterSuccess).learn()
        }

        fun learnSpellForFree(spellToLearn: AbilityItem) {
            InventoryUtils.getSelectedHero().learn(spellToLearn.id, 0)
        }
    }

    private val selectedHero: HeroItem = InventoryUtils.getSelectedHero()
    private val wizardSkill: Int = selectedHero.getSkillById(SkillItemId.WIZARD).rank
    private val spellName: String = spellToLearn.name

    private val hasSpell: Boolean = selectedHero.getAbilityById(spellToLearn.id) != null
    private val isWizard: Boolean = wizardSkill != -1
    private val hasWizardSkill: Boolean = wizardSkill >= 1
    private val hasEnoughWizardSkill: Boolean = wizardSkill >= spellToLearn.minSkill
    private val xpCost: Int = spellToLearn.calculateXpCost(selectedHero.totalXp)
    private val hasEnoughXp: Boolean = selectedHero.hasEnoughXpFor(xpCost)
    private val goldCost: Int = spellToLearn.goldCost
    private val hasEnoughGold: Boolean = gameData.inventory.hasEnoughOfItem("gold", goldCost)

    private fun learn() {
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
            ${selectedHero.name} with $spellName for $xpCost XP and $goldCost gold?""".trimIndent()
        val dialog = QuestionDialog(question) { learnSpell() }
        dialog.show(stage, AudioEvent.SE_CONVERSATION_NEXT, 0)
    }

    private fun learnSpell() {
        gameData.inventory.autoRemoveItem("gold", goldCost)
        selectedHero.learn(spellToLearn.id, xpCost)
        setHasJustUpdatedToTrue.invoke()
        showConfirmMessage()
    }

    private fun showConfirmMessage() {
        stopAllSe()
        MessageDialog("${spellToLearn.name} learned.").show(stage, AudioEvent.SE_UPGRADE)
    }

}
