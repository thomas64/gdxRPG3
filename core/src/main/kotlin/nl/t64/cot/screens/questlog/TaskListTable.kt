package nl.t64.cot.screens.questlog

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.ui.List
import com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import ktx.collections.GdxArray
import nl.t64.cot.Utils
import nl.t64.cot.Utils.resourceManager
import nl.t64.cot.components.quest.QuestGraph
import nl.t64.cot.components.quest.QuestState
import nl.t64.cot.components.quest.QuestTask


private const val TEXT_FONT = "fonts/spectral_extra_bold_20.ttf"
private const val TEXT_SIZE = 20
private const val WIDTH = -102f
private const val HEIGHT = 704f
private const val PAD_LEFT = 20f

internal class TaskListTable {

    private val taskListFont: BitmapFont = resourceManager.getTrueTypeAsset(TEXT_FONT, TEXT_SIZE)
    private val taskList: List<QuestTask> = createList()
    private val scrollPane: ScrollPane = fillScrollPane()
    val container: Table = fillContainer()

    fun populateTaskList(quest: QuestGraph) {
        taskList.clearItems()
        if (quest.isOneOfBothStatesEqualOrHigherThan(QuestState.ACCEPTED)) {
            val questTasks = GdxArray(quest.getAllQuestTasksForVisual())
            taskList.setItems(questTasks)
            taskList.setAlignment(Align.left)
        } else {
            taskList.setItems(QuestTask(taskPhrase = "(No tasks visible until this quest is accepted)"))
            taskList.setAlignment(Align.center)
        }
    }

    private fun createList(): List<QuestTask> {
        return List(ListStyle().apply {
            font = taskListFont
            fontColorSelected = Color.BLACK
            fontColorUnselected = Color.BLACK
            background = Utils.createDrawable(Color.CLEAR)
            selection = Utils.createDrawable(Color.CLEAR)
        })
    }

    private fun fillScrollPane(): ScrollPane {
        return ScrollPane(taskList).apply {
            setOverscroll(false, false)
            fadeScrollBars = false
            setScrollingDisabled(true, true)
            setForceScroll(false, false)
            setScrollBarPositions(false, false)
        }
    }

    private fun fillContainer(): Table {
        return Table().apply {
            background = Utils.createTopBorder()
            padLeft(PAD_LEFT)
            val threeQuartersOfScreenWidth = Gdx.graphics.width * .75f
            add(scrollPane).width(threeQuartersOfScreenWidth + WIDTH).height(HEIGHT)
        }
    }

}
