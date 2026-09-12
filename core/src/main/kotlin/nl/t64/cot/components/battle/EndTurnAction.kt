package nl.t64.cot.components.battle


class EndTurnAction(
    private val currentParticipant: Participant
) {

    fun handle(): String {
        val roomUntilMaximum: Int = MAXIMUM_AP - currentParticipant.maximumAP
        val carryOver: Int = currentParticipant.currentAP.coerceAtMost(currentParticipant.maxCarryOverAp).coerceAtMost(roomUntilMaximum)
        val alreadyCarried: Int = (currentParticipant.currentAP - currentParticipant.maximumAP).coerceAtLeast(0)
        val gain: Int = carryOver - alreadyCarried
        return if (gain <= 0) "" else "$gain AP"
    }

}
