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
    private val chanceToFlee: Int = 10 + (character.getCalculatedTotalSkillOf(SkillItemId.STEALTH) * 6)
    private val message = """
        When successful, fleeing will return you to the the location of
        your last save with all progress intact. Otherwise, the turn ends.

        Your chance to flee is $chanceToFlee%. The higher your Stealth
        skill, the higher the chance to flee successfully.

        """

    fun isAble(): Pair<Boolean, String> {
        if (!gameData.battles.isBattleEscapable(battleId)) {
            return Pair(false, (message + "You can't flee from this battle.").trimIndent())
        }
        if (currentParticipant.currentAP < currentParticipant.maximumAP) {
            return Pair(false, (message + "Not enough AP!").trimIndent())
        }
        return Pair(true, (message + "Do you want to flee (${currentParticipant.maximumAP} AP) ?").trimIndent())
    }

    fun handle(): Pair<Boolean, String> {
        currentParticipant.currentAP -= currentParticipant.maximumAP
        if (preferenceManager.isInDebugMode) {
            return Pair(true, "The party successfully debug fled the battle.")
        }
        return if (chanceToFlee >= Random.nextInt(0, 100)) {
            Pair(true, "The party successfully fled the battle.")
        } else {
            Pair(false, "The party failed to flee the battle.")
        }
    }

}
