package nl.t64.cot.components.battle


private const val DELAY_AP: Int = 1

class DelayTurnAction(
    private val currentParticipant: Participant
) {
    private val character: Character = currentParticipant.character
    private val message = """
        Delaying will make the current character
        end this turn and become next in line,
        keeping ${character.gender} current AP for the next turn.

        """

    fun isAble(): Pair<Boolean, String> {
        return if (currentParticipant.currentAP < DELAY_AP) {
            Pair(false, (message + "Not enough AP!").trimIndent())
        } else {
            Pair(true, (message + "Do you want to delay your turn? ($DELAY_AP AP)").trimIndent())
        }
    }

    fun handle(): String {
        currentParticipant.currentAP -= DELAY_AP
        return """
            ${character.name} delayed ${character.gender} turn,
            becoming next in line.
        """.trimIndent()
    }

}
