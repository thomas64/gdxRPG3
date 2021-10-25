package nl.t64.cot.screens.school

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import nl.t64.cot.Utils
import nl.t64.cot.Utils.audioManager
import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.resourceManager
import nl.t64.cot.audio.AudioCommand
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.components.party.skills.SkillItemId
import nl.t64.cot.components.party.spells.SchoolType
import nl.t64.cot.components.party.spells.SpellDatabase
import nl.t64.cot.components.party.spells.SpellItem
import nl.t64.cot.constants.Constant
import nl.t64.cot.screens.inventory.BaseTable
import nl.t64.cot.screens.inventory.ListenerKeyVertical
import nl.t64.cot.screens.inventory.messagedialog.MessageDialog
import nl.t64.cot.screens.menu.DialogQuestion


private const val FIRST_COLUMN_WIDTH = 48f
private const val SECOND_COLUMN_WIDTH = 210f
private const val THIRD_COLUMN_WIDTH = 40f
private const val FOURTH_COLUMN_WIDTH = 30f
private const val CONTAINER_HEIGHT = 704f
private const val ROW_HEIGHT = 48f
private const val SECOND_COLUMN_PAD_LEFT = 15f

class SchoolTable(
    schoolId: String,
    tooltip: SchoolTooltip
) : BaseTable(tooltip) {

    private val spellsToLearn: List<SpellItem> = resourceManager.getSchoolInventory(schoolId)
        .map { SpellDatabase.createSpellItem(it.key, it.value) }
    private val scrollPane: ScrollPane

    init {
        table.columnDefaults(0).width(FIRST_COLUMN_WIDTH)
        table.columnDefaults(1).width(SECOND_COLUMN_WIDTH)
        table.columnDefaults(2).width(THIRD_COLUMN_WIDTH)
        table.columnDefaults(3).width(FOURTH_COLUMN_WIDTH)
        table.top()
        table.defaults().height(ROW_HEIGHT)
        scrollPane = ScrollPane(table)
        container.add(scrollPane).height(CONTAINER_HEIGHT)
        container.background = Utils.createTopBorder()
        container.addListener(ListenerKeyVertical { updateIndex(it, spellsToLearn.size) })
    }

    fun upgradeSpell() {
        val trainerSpell = spellsToLearn[selectedIndex]
        val heroSpell = selectedHero.getSpellById(trainerSpell.id)
        val wizardSkill = selectedHero.getSkillById(SkillItemId.WIZARD).rank
        val totalScholar = selectedHero.getCalculatedTotalSkillOf(SkillItemId.SCHOLAR)
        val spellName = heroSpell.name
        val xpCost = heroSpell.getXpCostForNextRank(trainerSpell, wizardSkill, totalScholar)
        val goldCost = heroSpell.getGoldCostForNextRank(trainerSpell, wizardSkill)

        if (selectedHero.school == SchoolType.NONE) {
            showError("Only wizards can learn spells.")
        } else if (wizardSkill < 1) {
            showError("You need the Wizard skill to learn spells.")
        } else if (heroSpell.school != selectedHero.school
            && heroSpell.school != SchoolType.NEUTRAL
            && selectedHero.school != SchoolType.UNKNOWN
        ) {
            showError("You cannot learn $spellName as you are from the wrong school.")
        } else if (wizardSkill < heroSpell.minWizard) {
            showError("Your Wizard skill is not high enough to learn $spellName.")
        } else if (xpCost == -2) {
            showError("I cannot teach you in $spellName any further.")
        } else if (xpCost == 0) {
            showError("You cannot learn $spellName any further.")
        } else if (!selectedHero.hasEnoughXpFor(xpCost)) {
            showError("I'm sorry. You don't seem to have enough 'XP to Invest'.")
        } else if (!gameData.inventory.hasEnoughOfItem("gold", goldCost)) {
            showError("I'm sorry. You don't seem to have enough gold.")
        } else {
            DialogQuestion({ upgradeSpell(heroSpell, xpCost, goldCost) }, """
                Are you sure you wish to learn
                $spellName for $xpCost XP and $goldCost gold?""".trimIndent())
                .show(table.stage, AudioEvent.SE_CONVERSATION_NEXT, 0)
        }
    }

    private fun showError(message: String) {
        MessageDialog(message).show(table.stage, AudioEvent.SE_MENU_ERROR)
    }

    private fun upgradeSpell(spellItem: SpellItem, xpCost: Int, goldCost: Int) {
        selectedHero.doUpgrade(spellItem, xpCost, goldCost)
        hasJustUpdated = true
        audioManager.handle(AudioCommand.SE_STOP_ALL)
        MessageDialog("""
            ${spellItem.name}:
            ${spellItem.rank - 1} -> ${spellItem.rank}""".trimIndent())
            .show(table.stage, AudioEvent.SE_UPGRADE)
    }

    override fun selectCurrentSlot() {
        super.selectCurrentSlot()
        if (selectedIndex >= spellsToLearn.size) {
            selectedIndex = spellsToLearn.size - 1
        }
    }

    override fun fillRows() {
        fillSchoolRow()
        fillSpellRows()
    }

    private fun fillSchoolRow() {
        val lastSpell = spellsToLearn.last()
        table.add(createImageOf(lastSpell.school.name))
        table.add(lastSpell.school.title + " School").padLeft(SECOND_COLUMN_PAD_LEFT)
        table.add("")
        table.add("").row()
        table.add("").row()
    }

    private fun fillSpellRows() {
        spellsToLearn.indices.forEach { fillSpellRow(spellsToLearn[it], it) }
    }

    private fun fillSpellRow(spellItem: SpellItem, index: Int) {
        table.add(createImageOf(spellItem.id))
        val spellName = Label(spellItem.name, Label.LabelStyle(font, Color.BLACK))
        table.add(spellName).padLeft(SECOND_COLUMN_PAD_LEFT)
        if (table.hasKeyboardFocus() && index == selectedIndex) {
            setSelected(spellName, spellItem)
        }
        table.add(spellItem.rank.toString())
        table.add("").row()
        scrollScrollPane()
    }

    private fun setSelected(spellName: Label, spellItem: SpellItem) {
        spellName.style.fontColor = Constant.DARK_RED
        if (hasJustUpdated) {
            hasJustUpdated = false
            tooltip.setPosition(FIRST_COLUMN_WIDTH + SECOND_COLUMN_WIDTH) { getTooltipY() }
            tooltip.refresh(spellName, spellItem)
        }
    }

    private fun getTooltipY(): Float =
        CONTAINER_HEIGHT - (ROW_HEIGHT * selectedIndex) - (ROW_HEIGHT * 2f) - (ROW_HEIGHT * 0.4f)

    private fun scrollScrollPane() {
        val selectedY = CONTAINER_HEIGHT - (ROW_HEIGHT * selectedIndex)
        scrollPane.scrollTo(0f, selectedY, 0f, 0f, false, true)
    }

}
