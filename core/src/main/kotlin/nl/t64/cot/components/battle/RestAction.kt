package nl.t64.cot.components.battle

import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.components.party.HeroItem
import kotlin.math.roundToInt


private const val REST_AP: Int = 1

class RestAction(
    private val currentParticipant: Participant
) {
    private val character: Character = currentParticipant.character
    private val hero: HeroItem = character as HeroItem
    private val amount: Int = createAmount()
    private val message = """
        Resting will make the current character
        end this turn and recover $amount HP.

        """

    fun isAble(): Pair<Boolean, String> {
        return if (currentParticipant.currentAP < REST_AP) {
            Pair(false, (message + "Not enough AP!").trimIndent())
        } else {
            Pair(true, (message + "Do you want to rest ($REST_AP AP) ?").trimIndent())
        }
    }

    fun handle(): Pair<String, AudioEvent> {
        currentParticipant.currentAP -= REST_AP
        if (character.currentHp == character.maximumHp) {
            return createEndTurnMessage()
        } else {
            hero.recoverPartHp(amount)
            return createRestMessage()
        }
    }

    private fun createEndTurnMessage(): Pair<String, AudioEvent> {
        if (currentParticipant.currentAP >= 1) {
            return """
                ${character.name} ended ${character.gender} turn,
                taking along ${currentParticipant.currentAP.coerceAtMost(2)} AP to the next turn.
            """.trimIndent() to AudioEvent.SE_CONVERSATION_NEXT
        } else {
            return "${character.name} ended ${character.gender} turn." to AudioEvent.SE_CONVERSATION_NEXT
        }
    }

    private fun createRestMessage(): Pair<String, AudioEvent> {

        if (currentParticipant.currentAP >= 1) {
            return """
                ${character.name} rested for a turn,
                taking along ${currentParticipant.currentAP.coerceAtMost(2)} AP to the next turn
                and recovered $amount HP.
            """.trimIndent() to AudioEvent.SE_POTION
        } else {
            return "${character.name} rested for a turn and recovered $amount HP." to AudioEvent.SE_POTION
        }
    }

    private fun createAmount(): Int {
        return (hero.maximumHp / 20f).roundToInt().coerceAtMost(hero.maximumHp - hero.currentHp)
    }

}
