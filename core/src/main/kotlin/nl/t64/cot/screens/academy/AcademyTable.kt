package nl.t64.cot.screens.academy

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import nl.t64.cot.Utils
import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.resourceManager
import nl.t64.cot.components.party.HeroItem
import nl.t64.cot.components.party.skills.SkillDatabase
import nl.t64.cot.components.party.skills.SkillItem
import nl.t64.cot.components.party.skills.SkillItemId
import nl.t64.cot.screens.inventory.BaseTable
import nl.t64.cot.screens.inventory.InventoryUtils
import nl.t64.cot.screens.inventory.ListenerKeyVertical


private const val FIRST_COLUMN_WIDTH = 48f
private const val SECOND_COLUMN_WIDTH = 135f
private const val THIRD_COLUMN_WIDTH = 40f
private const val FOURTH_COLUMN_WIDTH = 35f
private const val CONTAINER_HEIGHT = 704f
private const val ROW_HEIGHT = 48f
private const val SECOND_COLUMN_PAD_LEFT = 15f

internal class AcademyTable(academyId: String, tooltip: AcademyTooltip) : BaseTable(tooltip) {

    private val skillsToTrain: List<SkillItem> = resourceManager.getAcademyInventory(academyId)
        .map { SkillDatabase.createSkillItem(it.key, it.value) }

    init {
        table.columnDefaults(0).width(FIRST_COLUMN_WIDTH)
        table.columnDefaults(1).width(SECOND_COLUMN_WIDTH)
        table.columnDefaults(2).width(THIRD_COLUMN_WIDTH)
        table.columnDefaults(3).width(FOURTH_COLUMN_WIDTH)
        table.defaults().height(ROW_HEIGHT)

        container.add(scrollPane).height(CONTAINER_HEIGHT)
        container.background = Utils.createTopBorder()
        container.addListener(ListenerKeyVertical { updateIndex(it, skillsToTrain.size) })
    }

    fun upgradeSkill() {
        val skillToUpgrade = skillsToTrain[selectedIndex]
        SkillUpgrader.upgradeSkill(skillToUpgrade, table.stage) { hasJustUpdated = true }
    }

    override fun selectAnotherSlotWhenIndexBecameOutOfBounds() {
        if (selectedIndex >= skillsToTrain.size) {
            selectedIndex = skillsToTrain.size - 1
        }
    }

    override fun fillRows() {
        skillsToTrain.forEachIndexed { index, skillItem -> fillRow(skillItem, index) }
    }

    private fun fillRow(trainerSkill: SkillItem, index: Int) {
        val (imageColor, labelColor) = createColorsFrom(trainerSkill)
        table.add(createImageOf(trainerSkill.id.name).apply { color = imageColor })
        val skillName = Label(trainerSkill.name, LabelStyle(font, labelColor))
        table.add(skillName).padLeft(SECOND_COLUMN_PAD_LEFT)
        val skillRank = Label(trainerSkill.rank.toString(), LabelStyle(font, labelColor))
        table.add(skillRank)
        table.add("").row()
        scrollScrollPane()
        super.possibleSetSelected(index, skillName, trainerSkill)
    }

    private fun scrollScrollPane() {
        val selectedY = CONTAINER_HEIGHT - (ROW_HEIGHT * selectedIndex)
        scrollPane.scrollTo(0f, selectedY, 0f, 0f)
    }

    private fun createColorsFrom(trainerSkill: SkillItem): Pair<Color, Color> {
        val selectedHero: HeroItem = InventoryUtils.getSelectedHero()
        val heroSkill: SkillItem = selectedHero.getSkillById(trainerSkill.id)
        val heroScholarSkill: Int = selectedHero.getCalculatedTotalSkillOf(SkillItemId.SCHOLAR)
        val xpCost: Int = heroSkill.getXpCostForNextRank(trainerSkill, heroScholarSkill)
        val goldCost: Int = heroSkill.getGoldCostForNextRank(trainerSkill)

        if (xpCost > 0 && selectedHero.hasEnoughXpFor(xpCost) &&
            goldCost > 0 && gameData.inventory.hasEnoughOfItem("gold", goldCost)) {
            return Color.WHITE to Color.BLACK
        } else {
            return Color.BLACK to Color.LIGHT_GRAY
        }
    }

}
