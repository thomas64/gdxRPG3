package nl.t64.cot.components.battle

import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.preferenceManager
import nl.t64.cot.components.party.skills.SkillItemId
import kotlin.random.Random


private const val FAILED_BONUS_PERCENTAGE = 5
private const val STEALTH_BONUS_PERCENTAGE = 4
private const val MAX_FLEE_PERCENTAGE = 95

class FleeAction(
    private val currentParticipant: Participant,
    private val battleId: String
) {
    private val character: Character = currentParticipant.character
    private val message = """
        When successful, ${character.name} will leave the battle
        and take no further part in it. A hero that fled gains no
        XP from this battle, but rejoins the party right after it.

        The higher your Stealth skill, the higher the chance
        to flee successfully. Each failure in fleeing will also
        raise this chance to flee.

        Your chance to successfully flee is ${getChanceToFlee()}%.

        """

    fun isAble(): Pair<Boolean, String> {
        if (!gameData.battles.isBattleEscapable(battleId)) {
            return Pair(false, "You can't flee from this battle.")
        }
        if (currentParticipant.currentAP < currentParticipant.maximumAP) {
            return Pair(false, (message + "Not enough AP!").trimIndent())
        }
        return Pair(true, (message + "Do you want to flee? (${currentParticipant.currentAP} AP)").trimIndent())
    }

    fun handle(): Pair<Boolean, String> {
        currentParticipant.currentAP = 0
        return if (preferenceManager.isDebugModeOn) {
            Pair(true, "${character.name} successfully debug fled the battle. (This text is not used)")
        } else if (getChanceToFlee() > Random.nextInt(0, 100)) {
            Pair(true, "${character.name} fled the battle. (This text is not used)")
        } else {
            currentParticipant.fleeChance += FAILED_BONUS_PERCENTAGE
            currentParticipant.hasFailedToFlee = true
            Pair(false, "${character.name} failed to flee the battle.")
        }
    }

    private fun getChanceToFlee(): Int {
        return (currentParticipant.fleeChance
            + (character.getCalculatedTotalSkillOf(SkillItemId.STEALTH) * STEALTH_BONUS_PERCENTAGE)
            ).coerceAtMost(MAX_FLEE_PERCENTAGE)
    }

}
