package nl.t64.cot.screens.alchemist

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import nl.t64.cot.Utils
import nl.t64.cot.Utils.gameData
import nl.t64.cot.components.party.abilities.ResourceType
import nl.t64.cot.components.party.skills.SkillItemId
import nl.t64.cot.screens.inventory.BaseTable
import nl.t64.cot.screens.inventory.tooltip.PersonalityTooltip


private const val FIRST_COLUMN_WIDTH = 64f
private const val SECOND_COLUMN_WIDTH = 150f
private const val THIRD_COLUMN_WIDTH = 60f
private const val ROW_HEIGHT = 64f
private const val SECOND_COLUMN_PAD_LEFT = 15f

internal class ResourcesTable() : BaseTable(PersonalityTooltip()) {

    init {
        table.columnDefaults(0).width(FIRST_COLUMN_WIDTH)
        table.columnDefaults(1).width(SECOND_COLUMN_WIDTH)
        table.columnDefaults(2).width(THIRD_COLUMN_WIDTH)
        table.defaults().height(ROW_HEIGHT)

        container.add(table)
        container.background = Utils.createTopBorder()
    }

    override fun fillRows() {
        table.add(createImageOf(ResourceType.HERB.name))
        table.add(Label(ResourceType.HERB.title, createLabelStyle())).padLeft(SECOND_COLUMN_PAD_LEFT)
        table.add(Label(gameData.inventory.getTotalOfItem(ResourceType.HERB.name).toString(), createLabelStyle())).row()

        table.add(createImageOf(ResourceType.SPICE.name))
        table.add(Label(ResourceType.SPICE.title, createLabelStyle())).padLeft(SECOND_COLUMN_PAD_LEFT)
        table.add(Label(gameData.inventory.getTotalOfItem(ResourceType.SPICE.name).toString(), createLabelStyle())).row()

        table.add(createImageOf(ResourceType.GEMSTONE.name))
        table.add(Label(ResourceType.GEMSTONE.title, createLabelStyle())).padLeft(SECOND_COLUMN_PAD_LEFT)
        table.add(Label(gameData.inventory.getTotalOfItem(ResourceType.GEMSTONE.name).toString(), createLabelStyle())).row()

        table.add("").row()

        table.add(createImageOf(SkillItemId.ALCHEMIST.name))
        table.add(Label(SkillItemId.ALCHEMIST.title, createLabelStyle())).padLeft(SECOND_COLUMN_PAD_LEFT)
        table.add(Label(getAlchemistRankForVisual(), createLabelStyle()))
    }

    private fun getAlchemistRankForVisual(): String {
        return selectedHero.getCalculatedTotalSkillOf(SkillItemId.ALCHEMIST).toString()
    }

    private fun createLabelStyle(): LabelStyle {
        return LabelStyle(font, Color.BLACK)
    }

}
