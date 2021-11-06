package nl.t64.cot.screens.inventory

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import nl.t64.cot.Utils
import nl.t64.cot.components.party.CalcAttributeId
import nl.t64.cot.components.party.PersonalityItem
import nl.t64.cot.components.party.inventory.InventoryGroup
import nl.t64.cot.components.party.toCalcAttributeId
import nl.t64.cot.constants.Constant
import nl.t64.cot.screens.inventory.tooltip.PersonalityTooltip


private const val FIRST_COLUMN_WIDTH = 190f
private const val SECOND_COLUMN_WIDTH = 40f
private const val THIRD_COLUMN_WIDTH = 35f

internal class CalcsTable(tooltip: PersonalityTooltip) : BaseTable(tooltip) {

    init {
        table.columnDefaults(0).width(FIRST_COLUMN_WIDTH)
        table.columnDefaults(1).width(SECOND_COLUMN_WIDTH)
        table.columnDefaults(2).width(THIRD_COLUMN_WIDTH)

        container.add(table)
        container.background = Utils.createTopBorder()
        container.addListener(ListenerKeyVertical { updateIndex(it, table.rows) })
    }

    override fun fillRows() {
        table.add(Label(CalcAttributeId.ACTION_POINTS.title, createLabelStyle()))
        table.add(selectedHero.getCalculatedActionPoints().toString())
        table.add("").row()

        table.add(Label(CalcAttributeId.BASE_HIT.title, createLabelStyle()))
        table.add(selectedHero.getCalcValueOf(InventoryGroup.WEAPON, CalcAttributeId.BASE_HIT).toString() + "%")
        table.add("").row()

        table.add(Label("Total Hit", createLabelStyle()))
        table.add(selectedHero.getCalculatedTotalHit().toString() + "%")
        table.add("").row()

        table.add(Label("Weapon " + CalcAttributeId.DAMAGE.title, createLabelStyle()))
        table.add(selectedHero.getCalcValueOf(InventoryGroup.WEAPON, CalcAttributeId.DAMAGE).toString())
        table.add("").row()

        table.add(Label("Total " + CalcAttributeId.DAMAGE.title, createLabelStyle()))
        table.add(selectedHero.getCalculatedTotalDamage().toString())
        table.add("").row()

        table.add(Label("Shield " + CalcAttributeId.DEFENSE.title, createLabelStyle()))
        table.add(selectedHero.getCalcValueOf(InventoryGroup.SHIELD, CalcAttributeId.DEFENSE).toString() + "%")
        table.add("").row()

        table.add(Label("Total " + CalcAttributeId.DEFENSE.title, createLabelStyle()))
        table.add(selectedHero.getCalculatedTotalDefense().toString() + "%")
        table.add("").row()

//        table.add(Label("Shield " + CalcAttributeId.PROTECTION.title, createLabelStyle()))
//        table.add(selectedHero.getCalcValueOf(InventoryGroup.SHIELD, CalcAttributeId.PROTECTION).toString())
//        table.add("").row()

        table.add(Label("Total " + CalcAttributeId.PROTECTION.title, createLabelStyle()))
        table.add(selectedHero.getTotalCalcOf(CalcAttributeId.PROTECTION).toString())
        addExtraToTable(selectedHero.getPossibleExtraProtection())

        table.add(Label(CalcAttributeId.SPELL_BATTERY.title, createLabelStyle()))
        table.add(selectedHero.getTotalCalcOf(CalcAttributeId.SPELL_BATTERY).toString())
        table.add("").row()

        if (table.hasKeyboardFocus()) {
            setSelected()
        }
    }

    private fun setSelected() {
        val child = table.getChild(selectedIndex * 3) as Label
        child.style.fontColor = Constant.DARK_RED
        if (hasJustUpdated) {
            hasJustUpdated = false
            tooltip.setPosition(FIRST_COLUMN_WIDTH / 1.5f) { getTooltipY() }
            tooltip.refresh(child, getPersonalityItemForDescriptionOnly(child))
        }
    }

    private fun getPersonalityItemForDescriptionOnly(child: Label): PersonalityItem {
        return object : PersonalityItem {
            override fun getTotalDescription(): String {
                return child.text.toString().toCalcAttributeId().getDescription()
            }
        }
    }

    private fun createLabelStyle(): LabelStyle {
        return LabelStyle(font, Color.BLACK)
    }

    private fun getTooltipY(): Float {
        val rowHeight = table.getRowHeight(0)
        return container.height - (rowHeight * selectedIndex) - (rowHeight * 0.5f)
    }

}
