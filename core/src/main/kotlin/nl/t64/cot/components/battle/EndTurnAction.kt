package nl.t64.cot.components.battle


class EndTurnAction(
    private val currentParticipant: Participant
) {

    fun handle(): String {
        if (currentParticipant.currentAP >= 1) {
            return "${currentParticipant.currentAP.coerceAtMost(2)} AP"
        } else {
            return ""
        }
    }

}
