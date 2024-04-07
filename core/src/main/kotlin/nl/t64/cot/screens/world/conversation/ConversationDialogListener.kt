package nl.t64.cot.screens.world.conversation

import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.ui.List
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe
import nl.t64.cot.components.conversation.ConversationChoice
import nl.t64.cot.constants.Constant


internal class ConversationDialogListener(
    private val answers: List<ConversationChoice>,
    private val selectAnswer: () -> Unit
) : InputListener() {

    override fun keyDown(event: InputEvent, keycode: Int): Boolean {
        when (keycode) {
            Input.Keys.UP -> inputUp()
            Input.Keys.DOWN -> inputDown()
            Constant.KEYCODE_BOTTOM,
            Input.Keys.ENTER,
            Input.Keys.SPACE,
            Input.Keys.A -> inputConfirm()
        }
        return true
    }

    private fun inputUp() {
        if (answers.items.size > 2 && answers.selectedIndex == 0) {
            playSe(AudioEvent.SE_CONVERSATION_CURSOR)
            answers.selectedIndex = answers.items.size - 1
        } else if (answers.items.size > 1 && answers.selectedIndex > 0) {
            playSe(AudioEvent.SE_CONVERSATION_CURSOR)
            answers.selectedIndex -= 1
        } else if (answers.selectedIndex == -1) {
            playSe(AudioEvent.SE_CONVERSATION_CURSOR)
            answers.selectedIndex = answers.items.size - 1
        }
    }

    private fun inputDown() {
        if (answers.items.size > 2 && answers.selectedIndex == answers.items.size - 1) {
            playSe(AudioEvent.SE_CONVERSATION_CURSOR)
            answers.selectedIndex = 0
        } else if (answers.items.size > 1 && answers.selectedIndex < answers.items.size - 1) {
            playSe(AudioEvent.SE_CONVERSATION_CURSOR)
            answers.selectedIndex += 1
        }
    }

    private fun inputConfirm() {
        if (answers.selectedIndex != -1) {
            selectAnswer.invoke()
        }
    }

}
