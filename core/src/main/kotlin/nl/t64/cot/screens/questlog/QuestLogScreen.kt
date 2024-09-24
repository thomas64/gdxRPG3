package nl.t64.cot.screens.questlog

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.scenes.scene2d.ui.Window
import nl.t64.cot.Utils
import nl.t64.cot.Utils.preferenceManager
import nl.t64.cot.Utils.screenManager
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe
import nl.t64.cot.components.quest.QuestGraph
import nl.t64.cot.components.quest.QuestState
import nl.t64.cot.constants.Constant
import nl.t64.cot.constants.ScreenType
import nl.t64.cot.screens.ParchmentScreen
import nl.t64.cot.screens.inventory.InventoryScreen
import nl.t64.cot.screens.inventory.messagedialog.MessageDialog


private const val TITLE_QUESTS = "   Quests"
private const val TITLE_SUMMARY = "   Summary"
private const val TITLE_TASKS = "   Tasks"
private const val QUESTS_WINDOW_POSITION_X = 63f
private const val QUESTS_WINDOW_POSITION_Y = 150f
private const val SUMMARY_WINDOW_POSITION_X = 18f
private const val SUMMARY_WINDOW_POSITION_Y = 834f
private const val TASKS_WINDOW_POSITION_X = 18f
private const val TASKS_WINDOW_POSITION_Y = 50f
private const val LABEL_PADDING_LEFT = 100f
private const val LABEL_PADDING_BOTTOM = 26f

class QuestLogScreen : ParchmentScreen() {

    private val questListTable: QuestListTable = QuestListTable()
    private val questListWindow: Window = Utils.createDefaultWindow(TITLE_QUESTS, questListTable.container)
    private val clockTable: ClockTable = ClockTable()
    private val summaryTable: SummaryTable = SummaryTable()
    private val summaryWindow: Window = Utils.createDefaultWindow(TITLE_SUMMARY, summaryTable.container)
    private val taskListTable: TaskListTable = TaskListTable()
    private val taskListWindow: Window = Utils.createDefaultWindow(TITLE_TASKS, taskListTable.container)
    private val buttonLabel: Label = Label(createText(), LabelStyle(BitmapFont(), Color.BLACK))

    companion object {
        fun load() {
            playSe(AudioEvent.SE_SCROLL)
            screenManager.openParchmentLoadScreen(ScreenType.QUEST_LOG)
        }
    }

    init {
        setPositions()
    }

    override fun show() {
        setInputProcessors(stage)
        fillStage()
        stage.keyboardFocus = questListTable.questList
        stage.scrollFocus = questListTable.scrollPane
        handleQuestList()
        clockTable.fill()
    }

    override fun render(dt: Float) {
        renderStage(dt)
    }

    private fun fillStage() {
        stage.addActor(questListWindow)
        clockTable.addTo(stage)
        stage.addActor(summaryWindow)
        stage.addActor(taskListWindow)
        stage.addActor(buttonLabel)
        stage.addListener(QuestLogScreenListener(questListTable.questList,
                                                 { closeScreen() },
                                                 { openInventoryScreen() },
                                                 { showLegend() },
                                                 { populateQuestSpecifics(it) },
                                                 { cheatAllQuestsFinished() }))
    }

    private fun handleQuestList() {
        questListTable.populateQuestList()
        val questList = questListTable.questList
        if (questList.selectedIndex == -1) questList.selectedIndex = 0
        val selectedIndex = questList.selectedIndex
        val selectedQuest = questList.items[selectedIndex]
        populateQuestSpecifics(selectedQuest)
    }

    private fun openInventoryScreen() {
        closeScreen()
        Utils.runWithDelay(Constant.FADE_DURATION + 0.1f) { InventoryScreen.load() }
    }

    private fun showLegend() {
        val message = """
            These symbols mean:

            v   -   Quest finished or task complete.
            o   -   Quest finished, but reward unclaimed.
            r   -   Quest finished or task complete, but was reset.
            x   -   Quest or task failed.""".trimIndent()
        val messageDialog = MessageDialog(message)
        messageDialog.setLeftAlignment()
        messageDialog.show(stage, AudioEvent.SE_CONVERSATION_NEXT)
    }

    private fun populateQuestSpecifics(quest: QuestGraph) {
        taskListTable.populateTaskList(quest)
        summaryTable.populateSummary(quest)
    }

    private fun createText(): String {
        return if (Utils.isGamepadConnected()) {
            "[LB] Page-up      [RB] Page-down      [Select] Legend      [B] Back"
        } else {
            "[Left] Page-up      [Right] Page-down      [H] Legend      [L] Back"
        }
    }

    private fun setPositions() {
        questListWindow.setPosition(QUESTS_WINDOW_POSITION_X, QUESTS_WINDOW_POSITION_Y)
        val quarterOfScreenX = Gdx.graphics.width * .25f
        summaryWindow.setPosition(quarterOfScreenX + SUMMARY_WINDOW_POSITION_X, SUMMARY_WINDOW_POSITION_Y)
        taskListWindow.setPosition(quarterOfScreenX + TASKS_WINDOW_POSITION_X, TASKS_WINDOW_POSITION_Y)
        val buttonLabelX = (LABEL_PADDING_LEFT + questListWindow.width) - (buttonLabel.width / 2f)
        buttonLabel.setPosition(buttonLabelX, LABEL_PADDING_BOTTOM)
    }

    private fun cheatAllQuestsFinished() {
        if (preferenceManager.isInDebugMode) {
            playSe(AudioEvent.SE_MENU_ERROR)
            questListTable.questList.selected.currentState = QuestState.FINISHED
        }
    }

}
