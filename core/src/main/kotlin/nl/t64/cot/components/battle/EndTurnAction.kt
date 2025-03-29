package nl.t64.cot.components.battle


class EndTurnAction(
    private val currentParticipant: Participant
) {
    private val character: Character = currentParticipant.character

    fun handle(): String {
        if (currentParticipant.currentAP >= 1) {
            return """
                ${character.name} ended ${character.gender} turn,
                taking along ${currentParticipant.currentAP.coerceAtMost(2)} AP to the next turn.
            """.trimIndent()
        } else {
            return "${character.name} ended ${character.gender} turn."
        }
    }

}
