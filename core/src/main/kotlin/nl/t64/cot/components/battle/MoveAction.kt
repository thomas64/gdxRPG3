package nl.t64.cot.components.battle

import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe
import kotlin.math.abs


class MoveAction(
    private val battleField: BattleField,
    private val currentParticipant: Participant,
) {
    private val character: Character = currentParticipant.character
    private val currentSpace: Int = battleField.getCurrentSpace(currentParticipant)
    private val startingSpace: Int = battleField.startingSpace
    val difference: Int = abs(currentSpace - startingSpace)

    fun createConfirmationMessage(): String {
        playSe(AudioEvent.SE_MENU_CONFIRM)
        return """
            Do you want to move here for $difference AP?""".trimIndent()
    }

    fun handle() {
        // todo:       currentParticipant.currentAP -= amountOfSteps
        battleField.setStartingSpace(currentParticipant)
    }

}
