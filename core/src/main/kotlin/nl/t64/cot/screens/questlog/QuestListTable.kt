package nl.t64.cot.screens.questlog

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.ui.List
import com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import ktx.collections.GdxArray
import nl.t64.cot.Utils
import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.resourceManager
import nl.t64.cot.components.quest.QuestGraph
import nl.t64.cot.constants.Constant


private const val TEXT_FONT = "fonts/spectral_extra_bold_20.ttf"
private const val TEXT_SIZE = 20
private const val WIDTH = -102f
private const val HEIGHT = -252f
private const val PAD_LEFT = 20f

internal class QuestListTable {

    private val questListFont: BitmapFont = resourceManager.getTrueTypeAsset(TEXT_FONT, TEXT_SIZE)
    val questList: List<QuestGraph> = createList()
    val scrollPane: ScrollPane = fillScrollPane()
    val container: Table = fillContainer()

    fun populateQuestList() {
        val knownQuests = GdxArray(gameData.quests.getAllKnownQuestsForVisual())
        questList.setItems(knownQuests)
    }

    private fun createList(): List<QuestGraph> {
        return List(ListStyle().apply {
            font = questListFont
            fontColorSelected = Constant.DARK_RED
            fontColorUnselected = Color.BLACK
            selection = Utils.createFullBorder()
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
