package nl.t64.cot.screens.questlog

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.List
import com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import ktx.collections.GdxArray
import nl.t64.cot.Utils
import nl.t64.cot.components.quest.QuestGraph
import nl.t64.cot.components.quest.QuestState
import nl.t64.cot.components.quest.QuestTask
import nl.t64.cot.screens.FontProvider
import nl.t64.cot.toDrawable


private const val WIDTH = -102f
private const val HEIGHT = 704f
private const val PAD_LEFT = 20f

internal class TaskListTable {

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
            font = FontProvider.spectralExtraBold20
            fontColorSelected = Color.BLACK
            fontColorUnselected = Color.BLACK
            background = Color.CLEAR.toDrawable()
            selection = Color.CLEAR.toDrawable()
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
