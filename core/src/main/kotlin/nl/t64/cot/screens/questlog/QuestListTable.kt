package nl.t64.cot.screens.questlog

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.List
import com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import ktx.collections.GdxArray
import ktx.collections.toGdxArray
import nl.t64.cot.Utils
import nl.t64.cot.Utils.gameData
import nl.t64.cot.components.quest.QuestGraph
import nl.t64.cot.constants.Constant
import nl.t64.cot.screens.FontProvider


private const val WIDTH = -102f
private const val HEIGHT = -252f
private const val PAD_LEFT = 20f

internal class QuestListTable {

    val questList: List<QuestGraph> = createList()
    val scrollPane: ScrollPane = fillScrollPane()
    val container: Table = fillContainer()

    fun populateQuestList() {
        val knownQuests: GdxArray<QuestGraph> = gameData.quests.getAllKnownQuestsForVisual().toGdxArray()
        questList.setItems(knownQuests)
    }

    private fun createList(): List<QuestGraph> {
        return List(ListStyle().apply {
            font = FontProvider.spectralExtraBold20
            fontColorSelected = Constant.DARK_RED
            fontColorUnselected = Color.BLACK
            selection = Utils.createFullBorderBlack()
            selection.leftWidth = PAD_LEFT
        })
    }

    private fun fillScrollPane(): ScrollPane {
        return ScrollPane(questList).apply {
            setOverscroll(false, false)
            fadeScrollBars = false
            setScrollingDisabled(true, false)
            setForceScroll(false, false)
            setScrollBarPositions(false, false)
        }
    }

    private fun fillContainer(): Table {
        return Table().apply {
            background = Utils.createTopBorder()
            val quarterOfScreenWidth = Gdx.graphics.width * .25f
            add(scrollPane).width(quarterOfScreenWidth + WIDTH).height(Gdx.graphics.height + HEIGHT)
        }
    }

}
