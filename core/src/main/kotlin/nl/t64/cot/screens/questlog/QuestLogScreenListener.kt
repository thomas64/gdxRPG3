package nl.t64.cot.screens.questlog

import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.ui.Dialog
import com.badlogic.gdx.scenes.scene2d.ui.List
import nl.t64.cot.Utils.audioManager
import nl.t64.cot.audio.AudioCommand
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.components.quest.QuestGraph
import nl.t64.cot.constants.Constant
import kotlin.math.max
import kotlin.math.min


private const val TEN = 10

internal class QuestLogScreenListener(
    private val questList: List<QuestGraph>,
    private val closeScreenFunction: () -> Unit,
    private val showLegendFunction: () -> Unit,
    private val populateQuestSpecifics: (QuestGraph) -> Unit
) : InputListener() {

    override fun keyDown(event: InputEvent, keycode: Int): Boolean {
        if (event.stage.actors.items.any { it is Dialog }) {
            return true
        }
        when (keycode) {
            Constant.KEYCODE_RIGHT, Input.Keys.L, Input.Keys.ESCAPE -> closeScreenFunction.invoke()
            Constant.KEYCODE_SELECT, Input.Keys.H -> showLegendFunction.invoke()

            Input.Keys.UP, Input.Keys.DOWN, Input.Keys.HOME, Input.Keys.END -> isSelected()
            Constant.KEYCODE_L1, Input.Keys.LEFT -> setSelectedUp()
            Constant.KEYCODE_R1, Input.Keys.RIGHT -> setSelectedDown()
        }
        return true
    }

    private fun setSelectedUp(): Boolean {
        if (questList.items.isEmpty) {
            questList.setSelectedIndex(max(questList.selectedIndex - TEN, -1))
        } else {
            questList.setSelectedIndex(max(questList.selectedIndex - TEN, 0))
        }
        return isSelected()
    }

    private fun setSelectedDown(): Boolean {
        questList.selectedIndex = min(questList.selectedIndex + TEN, questList.items.size - 1)
        return isSelected()
    }

    private fun isSelected(): Boolean {
        return questList.selected?.let { populateQuestSpecifics(it) } ?: false
    }

    private fun populateQuestSpecifics(quest: QuestGraph): Boolean {
        audioManager.handle(AudioCommand.SE_PLAY_ONCE, AudioEvent.SE_MENU_CURSOR)
        populateQuestSpecifics.invoke(quest)
        return true
    }

}
