package nl.t64.cot.screens.questlog

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.scenes.scene2d.ui.List
import com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable
import com.badlogic.gdx.utils.Align
import ktx.collections.GdxArray
import nl.t64.cot.Utils.createTopBorder
import nl.t64.cot.Utils.resourceManager
import nl.t64.cot.components.quest.QuestGraph
import nl.t64.cot.components.quest.QuestState
import nl.t64.cot.components.quest.QuestTask


private const val SPRITE_TRANSPARENT = "sprites/transparent.png"
private const val TEXT_FONT = "fonts/spectral_extra_bold_20.ttf"
private const val TEXT_SIZE = 20
private const val WIDTH = -102f
private const val HEIGHT = 704f
private const val PAD_LEFT = 20f

internal class TaskListTable {

    private val font: BitmapFont = resourceManager.getTrueTypeAsset(TEXT_FONT, TEXT_SIZE)
    private val taskList: List<QuestTask> = createList()
    private val scrollPane: ScrollPane = fillScrollPane()
    val container: Table = fillContainer()

    fun populateTaskList(quest: QuestGraph) {
        taskList.clearItems()
        if (quest.currentState != QuestState.KNOWN) {
            val questTasks = GdxArray(quest.getAllTasks())
            taskList.setItems(questTasks)
            taskList.setAlignment(Align.left)
        } else {
            taskList.setItems(QuestTask(taskPhrase = "(No tasks visible until this quest is accepted)"))
            taskList.setAlignment(Align.center)
        }
    }

    private fun createList(): List<QuestTask> {
        val spriteTransparent = Sprite(resourceManager.getTextureAsset(SPRITE_TRANSPARENT))
        val listStyle = ListStyle()
        listStyle.font = font
        listStyle.fontColorSelected = Color.BLACK
        listStyle.fontColorUnselected = Color.BLACK
        listStyle.background = SpriteDrawable(spriteTransparent)
        listStyle.selection = SpriteDrawable(spriteTransparent)
        return List(listStyle)
    }

    private fun fillScrollPane(): ScrollPane {
        val newScrollPane = ScrollPane(taskList)
        newScrollPane.setOverscroll(false, false)
        newScrollPane.fadeScrollBars = false
        newScrollPane.setScrollingDisabled(true, true)
        newScrollPane.setForceScroll(false, false)
        newScrollPane.setScrollBarPositions(false, false)
        return newScrollPane
    }

    private fun fillContainer(): Table {
        val newContainer = Table()
        newContainer.background = createTopBorder()
        newContainer.padLeft(PAD_LEFT)
        newContainer.add(scrollPane).width(Gdx.graphics.width / 2f + WIDTH).height(HEIGHT)
        return newContainer
    }

}
