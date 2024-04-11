package nl.t64.cot.screens.inventory

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import nl.t64.cot.Utils
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe
import nl.t64.cot.components.party.CalcAttributeId
import nl.t64.cot.components.party.PersonalityItem
import nl.t64.cot.components.party.inventory.InventoryGroup
import nl.t64.cot.components.party.stats.StatItem
import nl.t64.cot.components.party.stats.StatItemId
import nl.t64.cot.components.party.toCalcAttributeId
import nl.t64.cot.screens.inventory.tooltip.PersonalityTooltip


private const val FIRST_COLUMN_WIDTH = 190f
private const val SECOND_COLUMN_WIDTH = 40f
private const val THIRD_COLUMN_WIDTH = 35f
private const val CONTAINER_HEIGHT = 704f
private const val FIRST_INDEX_OF_CALCS = 16
private const val COLUMN_ROW_DELTA = 13
private const val THREE_COLUMNS = 3
private const val TOOLTIP_X = 130f
private const val TOOLTIP_Y = -18f

internal class StatsTable(tooltip: PersonalityTooltip) : BaseTable(tooltip) {

    init {
        table.columnDefaults(0).width(FIRST_COLUMN_WIDTH)
        table.columnDefaults(1).width(SECOND_COLUMN_WIDTH)
        table.columnDefaults(2).width(THIRD_COLUMN_WIDTH)

        container.add(table).height(CONTAINER_HEIGHT)
        container.background = Utils.createTopBorder()
        container.addListener(ListenerKeyVertical { updateIndex(it, table.rows) })
    }

    override fun selectAnotherSlotWhenIndexBecameOutOfBounds() {
        // empty
    }

    override fun getTooltipPosition() = Vector2(TOOLTIP_X, getSelected().y + TOOLTIP_Y)

    override fun updateIndex(deltaIndex: Int, size: Int) {
        selectedIndex += deltaIndex
        if (selectedIndex < 0) {
            selectedIndex = size
        } else if (selectedIndex == StatItemId.entries.count()) {
            selectedIndex = FIRST_INDEX_OF_CALCS
        } else if (selectedIndex == FIRST_INDEX_OF_CALCS - 1) {
            selectedIndex = StatItemId.entries.count() - 1
        } else if (selectedIndex > size) {
            selectedIndex = 0
        }
        hasJustUpdated = true
        playSe(AudioEvent.SE_MENU_CURSOR)
    }

    override fun fillRows() {
        fillStats()
        fillExperience()
        fillCalcs()
    }

    override fun doAction() {
        if (isCalcsSelected()) return
        val statToUpgrade = selectedHero.getAllStats()[selectedIndex]
        StatUpgrader.upgradeStat(statToUpgrade, table.stage) { hasJustUpdated = true }
    }

    private fun fillStats() {
        selectedHero.getAllStats().forEachIndexed { index, statItem -> fillRow(statItem, index) }
    }

    private fun fillExperience() {
        table.add("").row()
        fillRow("XP to Invest", selectedHero.xpToInvest)
        fillRow("Total XP", selectedHero.totalXp)
        fillRow("Next Level", selectedHero.xpNeededForNextLevel)
        table.add("").row()
    }

    private fun fillCalcs() {
        table.add("--------------------------------").row()
        table.add("Derived Attributes:").row()
        table.add("").row()

        table.add(Label(CalcAttributeId.ACTION_POINTS.title, createLabelStyle()))
        table.add(selectedHero.getCalculatedActionPoints().toString())
        table.add("").row()

        table.add(Label("Weapon " + CalcAttributeId.BASE_HIT.title, createLabelStyle()))
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

        table.add(Label("Shield " + CalcAttributeId.PROTECTION.title, createLabelStyle()))
        table.add(selectedHero.getCalcValueOf(InventoryGroup.SHIELD, CalcAttributeId.PROTECTION).toString())
        table.add("").row()

        table.add(Label("Total " + CalcAttributeId.PROTECTION.title, createLabelStyle()))
        table.add(selectedHero.getSumOfEquipmentOfCalc(CalcAttributeId.PROTECTION).toString())
        addExtraToTable(selectedHero.getPossibleExtraProtection())

        table.add(Label(CalcAttributeId.SPELL_BATTERY.title, createLabelStyle()))
        table.add(selectedHero.getSumOfEquipmentOfCalc(CalcAttributeId.SPELL_BATTERY).toString())
        table.add("").row()

        possibleSetSelected()
    }

    private fun fillRow(statItem: StatItem, index: Int) {
        val statTitle = Label(statItem.name, createLabelStyle())
        table.add(statTitle)
        table.add(statItem.rank.toString())
        val totalExtra = selectedHero.getExtraStatForVisualOf(statItem)
        addExtraToTable(totalExtra)
        super.possibleSetSelected(index, statTitle, statItem)
    }

    private fun fillRow(key: String, value: Int) {
        table.add(key)
        table.add(value.toString())
        table.add("").row()
    }

    private fun possibleSetSelected() {
        if (table.hasKeyboardFocus() && isCalcsSelected()) {
            val calcTitle = table.getChild(selectedIndex * THREE_COLUMNS - COLUMN_ROW_DELTA) as Label
            super.setSelected(calcTitle, getPersonalityItemForDescriptionOnly(calcTitle))
        }
    }

    private fun getPersonalityItemForDescriptionOnly(calcTitle: Label): PersonalityItem {
        return object : PersonalityItem {
            override fun getTotalDescription(): String {
                return calcTitle.text.toString().toCalcAttributeId().getDescription()
            }
        }
    }

    private fun createLabelStyle() = LabelStyle(font, Color.BLACK)

    private fun isCalcsSelected(): Boolean = selectedIndex >= FIRST_INDEX_OF_CALCS

}
