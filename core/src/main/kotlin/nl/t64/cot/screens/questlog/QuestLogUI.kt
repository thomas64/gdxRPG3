package nl.t64.cot.screens.questlog

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Window
import nl.t64.cot.Utils.createDefaultWindow
import nl.t64.cot.components.quest.QuestGraph


private const val TITLE_QUESTS = "   Quests"
private const val TITLE_SUMMARY = "   Summary"
private const val TITLE_TASKS = "   Tasks"

internal class QuestLogUI {

    val questListTable: QuestListTable = QuestListTable()
    val questListWindow: Window = createDefaultWindow(TITLE_QUESTS, questListTable.container)
    val summaryTable: SummaryTable = SummaryTable()
    val summaryWindow: Window = createDefaultWindow(TITLE_SUMMARY, summaryTable.container)
    val taskListTable: TaskListTable = TaskListTable()
    val taskListWindow: Window = createDefaultWindow(TITLE_TASKS, taskListTable.container)

    fun addToStage(stage: Stage) {
        stage.addActor(questListWindow)
        stage.addActor(summaryWindow)
        stage.addActor(taskListWindow)
    }

    fun applyListeners() {
        val questListListener = QuestListListener(questListTable.questList) { populateQuestSpecifics(it) }
        questListWindow.addListener(questListListener)
    }

    private fun populateQuestSpecifics(quest: QuestGraph) {
        taskListTable.populateTaskList(quest)
        summaryTable.populateSummary(quest)
    }

}
