package nl.t64.cot.screens.inventory

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import nl.t64.cot.Utils
import nl.t64.cot.components.party.stats.StatItem
import nl.t64.cot.screens.inventory.tooltip.PersonalityTooltip


private const val FIRST_COLUMN_WIDTH = 190f
private const val SECOND_COLUMN_WIDTH = 40f
private const val THIRD_COLUMN_WIDTH = 35f

internal class StatsTable(tooltip: PersonalityTooltip) : BaseTable(tooltip) {

    init {
        table.columnDefaults(0).width(FIRST_COLUMN_WIDTH)
        table.columnDefaults(1).width(SECOND_COLUMN_WIDTH)
        table.columnDefaults(2).width(THIRD_COLUMN_WIDTH)

        container.add(table)
        container.background = Utils.createTopBorder()
        container.addListener(ListenerKeyVertical { updateIndex(it, selectedHero.getAllStats().size) })
    }

    override fun selectAnotherSlotWhenIndexBecameOutOfBounds() {
        // empty
    }

    override fun fillRows() {
        fillStats()
        fillExperience()
    }

    override fun doAction() {
        val statToUpgrade = selectedHero.getAllStats()[selectedIndex]
        StatUpgrader.upgradeStat(statToUpgrade, table.stage) { setHasJustUpdate(true) }
    }

    private fun fillStats() {
        val statItemList = selectedHero.getAllStats()
        statItemList.indices.forEach { fillRow(statItemList[it], it) }
    }

    private fun fillExperience() {
        table.add("").row()
        fillRow("XP to Invest", selectedHero.xpToInvest)
        fillRow("Total XP", selectedHero.totalXp)
        fillRow("Next Level", selectedHero.xpNeededForNextLevel)
    }

    private fun fillRow(statItem: StatItem, index: Int) {
        val statTitle = Label(statItem.name, LabelStyle(font, Color.BLACK))
        table.add(statTitle)
        super.possibleSetSelected(index, statTitle, statItem)
        table.add(statItem.rank.toString())
        val totalExtra = selectedHero.getExtraStatForVisualOf(statItem)
        addExtraToTable(totalExtra)
    }

    private fun fillRow(key: String, value: Int) {
        table.add(key)
        table.add(value.toString())
        table.add("").row()
    }

    override fun getTooltipPosition(): Vector2 {
        val x = FIRST_COLUMN_WIDTH / 1.5f
        val rowHeight = table.getRowHeight(0)
        val y = container.height - (rowHeight * selectedIndex) - (rowHeight * 0.5f)
        return Vector2(x, y)
    }

}
