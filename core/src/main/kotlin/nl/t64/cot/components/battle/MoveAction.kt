package nl.t64.cot.components.battle

import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe
import kotlin.math.abs


class MoveAction(
    private val battleField: BattleField,
    private val currentParticipant: Participant,
) {
    private val currentSpace: Int = battleField.getCurrentSpace(currentParticipant)
    private val startingSpace: Int = battleField.startingSpace
    private val penaltyAP: Int = battleField.getPenaltyApForHero(currentParticipant)
    private val difference: Int = abs(currentSpace - startingSpace) + penaltyAP

    fun createConfirmationMessage(): String {
        playSe(AudioEvent.SE_MENU_CONFIRM)
        return """
            Do you want to move here for $difference AP?""".trimIndent()
    }

    fun didCharacterRemainOnTheSameSpace(): Boolean {
        if (difference - penaltyAP == 0) {
            playSe(AudioEvent.SE_MENU_BACK)
            return true
        } else {
            return false
        }
    }

    fun handle() {
        currentParticipant.currentAP -= difference
        battleField.setStartingSpace(currentParticipant)
    }

}
