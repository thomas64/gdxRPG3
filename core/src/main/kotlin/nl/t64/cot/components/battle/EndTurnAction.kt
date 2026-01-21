package nl.t64.cot.components.battle


class EndTurnAction(
    private val currentParticipant: Participant
) {

    fun handle(): String {
        val surplus: Int = currentParticipant.currentAP - currentParticipant.maximumAP
        return when {
            currentParticipant.currentAP == 0 || surplus == 2 -> ""
            currentParticipant.currentAP == 1 || surplus == 1 -> "1 AP"
            else -> "2 AP"
        }
    }

}
