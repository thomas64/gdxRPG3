package nl.t64.cot.screens.school

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import nl.t64.cot.Utils
import nl.t64.cot.Utils.gameData
import nl.t64.cot.components.party.skills.SkillItemId
import nl.t64.cot.screens.inventory.BaseTable
import nl.t64.cot.screens.inventory.tooltip.PersonalityTooltip


private const val FIRST_COLUMN_WIDTH = 200f
private const val SECOND_COLUMN_WIDTH = 60f

internal class CalcsTable(tooltip: PersonalityTooltip) : BaseTable(tooltip) {

    init {
        table.columnDefaults(0).width(FIRST_COLUMN_WIDTH)
        table.columnDefaults(1).width(SECOND_COLUMN_WIDTH)

        container.add(table)
        container.background = Utils.createTopBorder()
    }

    override fun fillRows() {
        table.add(Label("XP", createLabelStyle()))
        table.add(Label(selectedHero.xpPoints.toString(), createLabelStyle())).row()

        table.add(Label("Gold", createLabelStyle()))
        table.add(Label(gameData.inventory.getTotalOfItem("gold").toString(), createLabelStyle())).row()

        table.add("").row()

        table.add(Label("Wizard skill rank", createLabelStyle()))
        table.add(Label(getWizardRankForVisual(), createLabelStyle()))
    }

    private fun getWizardRankForVisual(): String {
        return selectedHero.getSkillById(SkillItemId.WIZARD).rank.toString().takeUnless { it == "-1" } ?: "N/A"
    }

    private fun createLabelStyle(): LabelStyle {
        return LabelStyle(font, Color.BLACK)
    }

}
