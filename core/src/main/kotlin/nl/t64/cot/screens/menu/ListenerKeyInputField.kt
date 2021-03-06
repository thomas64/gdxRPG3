package nl.t64.cot.screens.menu

import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe


private const val VALID_CHARACTERS = """^[\da-zA-Z]*$"""

internal class ListenerKeyInputField(
    private val updateInputFunction: (StringBuilder) -> Unit,
    private val maxSizeOfInput: Int
) : InputListener() {

    private lateinit var inputField: StringBuilder

    fun updateInputField(newInputField: StringBuilder) {
        inputField = newInputField
    }

    override fun keyTyped(event: InputEvent, character: Char): Boolean {
        val inputCharacter = character.toString()
        if (inputCharacter.matches(Regex(VALID_CHARACTERS))
            && inputField.length < maxSizeOfInput
        ) {
            playSe(AudioEvent.SE_MENU_TYPING)
            inputField.insert(inputField.length - 1, inputCharacter)
            updateInputFunction.invoke(inputField)
        }
        return true
    }

    override fun keyDown(event: InputEvent, keycode: Int): Boolean {
        if (keycode == Input.Keys.BACKSPACE
            && inputField.length - 1 > 0
        ) {
            playSe(AudioEvent.SE_MENU_TYPING)
            inputField.deleteCharAt(inputField.length - 2)
            updateInputFunction.invoke(inputField)
        }
        return true
    }

}
