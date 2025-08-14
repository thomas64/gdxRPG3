package nl.t64.cot.screens.mechanic

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import nl.t64.cot.Utils
import nl.t64.cot.Utils.gameData
import nl.t64.cot.components.party.skills.SkillItemId
import nl.t64.cot.screens.inventory.BaseTable
import nl.t64.cot.screens.inventory.tooltip.PersonalityTooltip


private const val FIRST_COLUMN_WIDTH = 150f
private const val SECOND_COLUMN_WIDTH = 60f

internal class ResourcesTable(tooltip: PersonalityTooltip) : BaseTable(tooltip) {

    init {
        table.columnDefaults(0).width(FIRST_COLUMN_WIDTH)
        table.columnDefaults(1).width(SECOND_COLUMN_WIDTH)

        container.add(table)
        container.background = Utils.createTopBorder()
    }

    override fun selectAnotherSlotWhenIndexBecameOutOfBounds() {
        // ToDo
    }

    override fun fillRows() {
        table.add(Label("Cloth", createLabelStyle()))
        table.add(Label(gameData.inventory.getTotalOfItem("cloth").toString(), createLabelStyle())).row()

        table.add(Label("Leather", createLabelStyle()))
        table.add(Label(gameData.inventory.getTotalOfItem("leather").toString(), createLabelStyle())).row()

        table.add(Label("Wood", createLabelStyle()))
        table.add(Label(gameData.inventory.getTotalOfItem("wood").toString(), createLabelStyle())).row()

        table.add(Label("Stone", createLabelStyle()))
        table.add(Label(gameData.inventory.getTotalOfItem("stone").toString(), createLabelStyle())).row()

        table.add(Label("Metal", createLabelStyle()))
        table.add(Label(gameData.inventory.getTotalOfItem("metal").toString(), createLabelStyle())).row()

        table.add("").row()

        table.add(Label("Mechanic", createLabelStyle()))
        table.add(Label(getMechanicRankForVisual(), createLabelStyle()))
    }

    private fun getMechanicRankForVisual(): String {
        return selectedHero.getSkillById(SkillItemId.MECHANIC).rank.toString().takeUnless { it == "-1" } ?: "N/A"
    }

    private fun createLabelStyle(): LabelStyle {
        return LabelStyle(font, Color.BLACK)
    }

}
