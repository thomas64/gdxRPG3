package nl.t64.cot.components.battle

import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.preferenceManager
import nl.t64.cot.components.party.skills.SkillItemId
import kotlin.random.Random


class FleeAction(
    private val currentParticipant: Participant,
    private val battleId: String
) {
    private val character: Character = currentParticipant.character
    private val message =
        "When successful, fleeing will return you to the the location of" +
            System.lineSeparator() +
            "your last save with all progress intact. Otherwise, the turn ends." +
            System.lineSeparator()

    fun isUnableToFlee(): String? {
        if (!gameData.battles.isBattleEscapable(battleId)) {
            return message + """

                You can't flee from this battle.""".trimIndent()
        }

        if (currentParticipant.currentAP < currentParticipant.maximumAP) {
            return message + """

                Not enough AP!""".trimIndent()
        }
        return null
    }

    fun createConfirmationMessage(): String {
        return message + """

            Do you want to flee (${currentParticipant.maximumAP} AP) ?""".trimIndent()
    }

    fun handle(): Pair<Boolean, String> {
        currentParticipant.currentAP = 0
        if (preferenceManager.isInDebugMode){
            return Pair(true, "The party successfully debug fled the battle.")
        }
        val chanceToFlee: Int = 10 + (character.getCalculatedTotalSkillOf(SkillItemId.STEALTH) * 6)
        return if (chanceToFlee >= Random.nextInt(0, 100)) {
            Pair(true, "The party successfully fled the battle.")
        } else {
            Pair(false, "The party failed to flee the battle.")
        }
    }

}
