package nl.t64.cot.components.battle


const val DELAY_AP: Int = 1

class DelayTurnAction(
    private val currentParticipant: Participant
) {
    private val character: Character = currentParticipant.character
    private val message = """
        Delaying will make ${character.name} end this
        turn and become next in line, keeping
        ${character.gender} current AP for the next turn.

        """

    fun isAble(): Pair<Boolean, String> {
        return if (currentParticipant.currentAP < DELAY_AP) {
            Pair(false, (message + "Not enough AP!").trimIndent())
        } else {
            Pair(true, (message + "Do you want to delay your turn? ($DELAY_AP AP)").trimIndent())
        }
    }

    fun handle() {
        currentParticipant.currentAP -= DELAY_AP
    }

}
