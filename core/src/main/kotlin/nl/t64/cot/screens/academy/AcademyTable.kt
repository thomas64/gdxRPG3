package nl.t64.cot.screens.academy

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import nl.t64.cot.Utils
import nl.t64.cot.Utils.audioManager
import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.resourceManager
import nl.t64.cot.audio.AudioCommand
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.components.party.skills.SkillDatabase
import nl.t64.cot.components.party.skills.SkillItem
import nl.t64.cot.components.party.skills.SkillItemId
import nl.t64.cot.constants.Constant
import nl.t64.cot.screens.inventory.BaseTable
import nl.t64.cot.screens.inventory.ListenerKeyVertical
import nl.t64.cot.screens.inventory.messagedialog.MessageDialog
import nl.t64.cot.screens.menu.DialogQuestion


private const val FIRST_COLUMN_WIDTH = 48f
private const val SECOND_COLUMN_WIDTH = 136f
private const val THIRD_COLUMN_WIDTH = 40f
private const val FOURTH_COLUMN_WIDTH = 35f
private const val CONTAINER_HEIGHT = 704f
private const val ROW_HEIGHT = 48f
private const val SECOND_COLUMN_PAD_LEFT = 15f

internal class AcademyTable(
    academyId: String,
    tooltip: AcademyTooltip
) : BaseTable(tooltip) {

    private val skillsToTrain: List<SkillItem> = resourceManager.getAcademyInventory(academyId)
        .map { SkillDatabase.createSkillItem(it.key, it.value) }
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
        container.addListener(ListenerKeyVertical { updateIndex(it, skillsToTrain.size) })
    }

    fun upgradeSkill() {
        val trainerSkill = skillsToTrain[selectedIndex]
        val heroSkill = selectedHero.getSkillById(trainerSkill.id)
        val totalScholar = selectedHero.getCalculatedTotalSkillOf(SkillItemId.SCHOLAR)
        val skillName = heroSkill.name
        val xpCost = heroSkill.getXpCostForNextLevel(trainerSkill, totalScholar)
        val goldCost = heroSkill.getGoldCostForNextLevel(trainerSkill)

        if (xpCost == -2) {
            showError("I cannot train you in $skillName any further.")
        } else if (xpCost == -1) {
            showError("You cannot train $skillName.")
        } else if (xpCost == 0) {
            showError("You cannot train $skillName any further.")
        } else if (!selectedHero.hasEnoughXpFor(xpCost)) {
            showError("I'm sorry. You don't seem to have enough 'XP to Invest'.")
        } else if (!gameData.inventory.hasEnoughOfItem("gold", goldCost)) {
            showError("I'm sorry. You don't seem to have enough gold.")
        } else {
            DialogQuestion({ upgradeSkill(heroSkill, xpCost, goldCost) }, """
                Are you sure you wish to train
                $skillName for $xpCost XP and $goldCost gold?""".trimIndent())
                .show(table.stage, AudioEvent.SE_CONVERSATION_NEXT, 0)
        }
    }

    private fun showError(message: String) {
        MessageDialog(message).show(table.stage, AudioEvent.SE_MENU_ERROR)
    }

    private fun upgradeSkill(skillItem: SkillItem, xpCost: Int, goldCost: Int) {
        selectedHero.doUpgrade(skillItem, xpCost, goldCost)
        hasJustUpdated = true
        audioManager.handle(AudioCommand.SE_STOP_ALL)
        MessageDialog("""
            ${skillItem.name}:
            ${skillItem.rank - 1} -> ${skillItem.rank}""".trimIndent())
            .show(table.stage, AudioEvent.SE_UPGRADE)
    }

    override fun selectCurrentSlot() {
        super.selectCurrentSlot()
        if (selectedIndex >= skillsToTrain.size) {
            selectedIndex = skillsToTrain.size - 1
        }
    }

    override fun fillRows() {
        skillsToTrain.indices.forEach { fillRow(skillsToTrain[it], it) }
    }

    private fun fillRow(skillItem: SkillItem, index: Int) {
        table.add(createImageOf(skillItem.id.name))
        val skillName = Label(skillItem.name, LabelStyle(font, Color.BLACK))
        table.add(skillName).padLeft(SECOND_COLUMN_PAD_LEFT)
        if (table.hasKeyboardFocus() && index == selectedIndex) {
            setSelected(skillName, skillItem)
        }
        table.add(skillItem.rank.toString())
        table.add("").row()
        scrollScrollPane()
    }

    private fun setSelected(skillName: Label, skillItem: SkillItem) {
        skillName.style.fontColor = Constant.DARK_RED
        if (hasJustUpdated) {
            hasJustUpdated = false
            tooltip.setPosition(FIRST_COLUMN_WIDTH + SECOND_COLUMN_WIDTH) { getTooltipY() }
            tooltip.refresh(skillName, skillItem)
        }
    }

    private fun getTooltipY(): Float =
        CONTAINER_HEIGHT - (ROW_HEIGHT * selectedIndex) - (ROW_HEIGHT * 0.5f)

    private fun scrollScrollPane() {
        val selectedY = CONTAINER_HEIGHT - (ROW_HEIGHT * selectedIndex)
        scrollPane.scrollTo(0f, selectedY, 0f, 0f, false, true)
    }

}
