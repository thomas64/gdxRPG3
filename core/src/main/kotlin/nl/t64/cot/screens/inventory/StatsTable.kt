package nl.t64.cot.screens.inventory

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import nl.t64.cot.Utils
import nl.t64.cot.Utils.audioManager
import nl.t64.cot.audio.AudioCommand
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.components.party.stats.StatItem
import nl.t64.cot.constants.Constant
import nl.t64.cot.screens.inventory.messagedialog.MessageDialog
import nl.t64.cot.screens.inventory.tooltip.PersonalityTooltip
import nl.t64.cot.screens.menu.DialogQuestion


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

    override fun fillRows() {
        fillStats()
        fillExperience()
    }

    override fun doAction() {
        val statItem = selectedHero.getAllStats()[selectedIndex]
        val statName = statItem.name
        val xpCost = statItem.getXpCostForNextRank()
        if (xpCost == 0) {
            MessageDialog("You cannot train $statName any further.")
                .show(table.stage, AudioEvent.SE_MENU_ERROR)
        } else if (selectedHero.hasEnoughXpFor(xpCost)) {
            DialogQuestion({ upgradeStat(statItem, xpCost) }, """
                Are you sure you wish to train 
                $statName for $xpCost XP?""".trimIndent())
                .show(table.stage, AudioEvent.SE_CONVERSATION_NEXT, 0)
        } else {
            MessageDialog("You need $xpCost 'XP to Invest' to train $statName.")
                .show(table.stage, AudioEvent.SE_MENU_ERROR)
        }
    }

    private fun upgradeStat(statItem: StatItem, xpCost: Int) {
        selectedHero.doUpgrade(statItem, xpCost)
        hasJustUpdated = true
        audioManager.handle(AudioCommand.SE_STOP_ALL)
        MessageDialog("""
            ${statItem.name}:
            ${statItem.rank - 1} -> ${statItem.rank}""".trimIndent())
            .show(table.stage, AudioEvent.SE_UPGRADE)
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
        if (table.hasKeyboardFocus() && index == selectedIndex) {
            setSelected(statTitle, statItem)
        }
        table.add(statItem.rank.toString())
        val totalExtra = selectedHero.getExtraStatForVisualOf(statItem)
        addExtraToTable(totalExtra)
    }

    private fun setSelected(statTitle: Label, statItem: StatItem) {
        statTitle.style.fontColor = Constant.DARK_RED
        if (hasJustUpdated) {
            hasJustUpdated = false
            tooltip.setPosition(FIRST_COLUMN_WIDTH / 1.5f) { getTooltipY() }
            tooltip.refresh(statTitle, statItem)
        }
    }

    private fun fillRow(key: String, value: Int) {
        table.add(key)
        table.add(value.toString())
        table.add("").row()
    }

    private fun getTooltipY(): Float {
        val rowHeight = table.getRowHeight(0)
        return container.height - (rowHeight * selectedIndex) - (rowHeight * 0.5f)
    }

}
