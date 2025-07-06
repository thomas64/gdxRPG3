package nl.t64.cot.components.battle

import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe
import kotlin.math.abs


class MoveAction(
    private val battleField: BattleField,
    private val currentParticipant: Participant,
) {
    private val currentSpace: Int = battleField.getSpaceIndexOfCurrentParticipant()
    private val startingSpace: Int = battleField.startingSpace
    private val penaltyAP: Int = battleField.getPenaltyApForHero()
    private val difference: Int = abs(currentSpace - startingSpace) + penaltyAP

    fun didCharacterRemainOnTheSameSpace(): Boolean {
        if (difference - penaltyAP == 0) {
            playSe(AudioEvent.SE_MENU_BACK)
            return true
        } else {
            return false
        }
    }

    fun handle() {
        playSe(AudioEvent.SE_MENU_CONFIRM)
        currentParticipant.currentAP -= difference
        battleField.setStartingSpace()
    }

}
