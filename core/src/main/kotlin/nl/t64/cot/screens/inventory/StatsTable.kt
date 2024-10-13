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


private const val FIRST_COLUMN_WIDTH = 175f
private const val SECOND_COLUMN_WIDTH = 15f
private const val THIRD_COLUMN_WIDTH = 40f
private const val FOURTH_COLUMN_WIDTH = 35f
private const val CONTAINER_HEIGHT = 704f
private const val FIRST_INDEX_OF_CALCS = 18
private const val FOUR_COLUMNS = 4
private const val TOOLTIP_X = 130f
private const val TOOLTIP_Y = -18f

internal class StatsTable(tooltip: PersonalityTooltip) : BaseTable(tooltip) {

    init {
        table.columnDefaults(0).width(FIRST_COLUMN_WIDTH)
        table.columnDefaults(1).width(SECOND_COLUMN_WIDTH)
        table.columnDefaults(2).width(THIRD_COLUMN_WIDTH)
        table.columnDefaults(3).width(FOURTH_COLUMN_WIDTH)

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
            selectedIndex = size - 1
        } else if (selectedIndex == StatItemId.entries.count()) {
            selectedIndex = FIRST_INDEX_OF_CALCS
        } else if (selectedIndex == FIRST_INDEX_OF_CALCS - 1) {
            selectedIndex = StatItemId.entries.count() - 1
        } else if (selectedIndex >= size) {
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
        fillEmptyRow()
        fillRow("XP Points", selectedHero.xpPoints)
        fillRow("Total XP", selectedHero.totalXp)
        fillEmptyRow()
        fillEmptyRow()
        fillEmptyRow()
    }

    private fun fillCalcs() {
        fillRow("--------------------------------")
        fillEmptyRow()
        fillEmptyRow()
        fillRow("Derived Attributes:")
        fillEmptyRow()

        table.add(Label(CalcAttributeId.ACTION_POINTS.title, createLabelStyle()))
        table.add("")
        table.add(selectedHero.getCalculatedActionPoints().toString())
        table.add("").row()

        table.add(Label("Chance to hit (%)", createLabelStyle()))
        table.add("")
        val baseHit: Int = selectedHero.getCalcValueOf(InventoryGroup.WEAPON, CalcAttributeId.BASE_HIT)
        table.add(baseHit.toString())
        val bonusHit: Int = selectedHero.getCalculatedTotalHit() - baseHit
        addExtraToTable(bonusHit)

        table.add(Label(CalcAttributeId.DEFENSE.title + " (%)", createLabelStyle()))
        table.add("")
        table.add(selectedHero.getCalculatedTotalDefense().toString())
        table.add("").row()

        table.add(Label(CalcAttributeId.DAMAGE.title, createLabelStyle()))
        table.add("")
        val baseDamage: Int = selectedHero.getCalcValueOf(InventoryGroup.WEAPON, CalcAttributeId.DAMAGE)
        table.add(baseDamage.toString())
        val bonusDamage: Int = selectedHero.getCalculatedTotalDamage() - baseDamage
        addExtraToTable(bonusDamage)

        table.add(Label(CalcAttributeId.PROTECTION.title, createLabelStyle()))
        table.add("")
        table.add(selectedHero.getSumOfEquipmentOfCalc(CalcAttributeId.PROTECTION).toString())
        addExtraToTable(selectedHero.getPossibleExtraProtection())

        table.add(Label(CalcAttributeId.SPELL_BATTERY.title, createLabelStyle()))
        table.add("")
        table.add(selectedHero.getSumOfEquipmentOfCalc(CalcAttributeId.SPELL_BATTERY).toString())
        table.add("").row()

        possibleSetSelected()
    }

    private fun fillRow(statItem: StatItem, index: Int) {
        val statTitle = Label(statItem.name, createLabelStyle())
        table.add(statTitle)
        val upgrade = Label("^", LabelStyle(font, Color.PURPLE))
        val xpCost: Int = statItem.getXpCostForNextRank()
        if (xpCost > 0 && selectedHero.xpPoints >= xpCost) table.add(upgrade) else table.add("")
        table.add(statItem.rank.toString())
        val totalExtra = selectedHero.getExtraStatForVisualOf(statItem)
        addExtraToTable(totalExtra)
        super.possibleSetSelected(index, statTitle, statItem)
    }

    private fun fillEmptyRow() {
        table.add("")
        table.add("")
        table.add("")
        table.add("").row()
    }

    private fun fillRow(key: String) {
        table.add(key)
        table.add("")
        table.add("")
        table.add("").row()
    }

    private fun fillRow(key: String, value: Int) {
        table.add(key)
        table.add("")
        table.add(value.toString())
        table.add("").row()
    }

    private fun possibleSetSelected() {
        if (table.hasKeyboardFocus() && isCalcsSelected()) {
            val calcTitle = table.getChild(selectedIndex * FOUR_COLUMNS) as Label
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
