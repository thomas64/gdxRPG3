package nl.t64.cot.screens.mechanic

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
        table.add(createImageOf(ResourceType.LEATHER.name))
        table.add(Label(ResourceType.LEATHER.title, createLabelStyle())).padLeft(SECOND_COLUMN_PAD_LEFT)
        table.add(Label(gameData.inventory.getTotalOfItem(ResourceType.LEATHER.name).toString(), createLabelStyle())).row()

        table.add(createImageOf(ResourceType.WOOD.name))
        table.add(Label(ResourceType.WOOD.title, createLabelStyle())).padLeft(SECOND_COLUMN_PAD_LEFT)
        table.add(Label(gameData.inventory.getTotalOfItem(ResourceType.WOOD.name).toString(), createLabelStyle())).row()

        table.add(createImageOf(ResourceType.METAL.name))
        table.add(Label(ResourceType.METAL.title, createLabelStyle())).padLeft(SECOND_COLUMN_PAD_LEFT)
        table.add(Label(gameData.inventory.getTotalOfItem(ResourceType.METAL.name).toString(), createLabelStyle())).row()

        table.add("").row()

        table.add(createImageOf(SkillItemId.MECHANIC.name))
        table.add(Label(SkillItemId.MECHANIC.title, createLabelStyle())).padLeft(SECOND_COLUMN_PAD_LEFT)
        table.add(Label(getMechanicRankForVisual(), createLabelStyle()))
    }

    private fun getMechanicRankForVisual(): String {
        return selectedHero.getCalculatedTotalSkillOf(SkillItemId.MECHANIC).toString()
    }

    private fun createLabelStyle(): LabelStyle {
        return LabelStyle(font, Color.BLACK)
    }

}
