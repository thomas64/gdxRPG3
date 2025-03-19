package nl.t64.cot.components.battle

import nl.t64.cot.components.party.HeroItem


private const val REST_AP: Int = 2

class RestAction(
    private val currentParticipant: Participant
) {
    private val character: Character = currentParticipant.character
    private val hero: HeroItem = character as HeroItem

    fun isCostingTooMuchAp(): String? {
        return when {
            currentParticipant.currentAP < REST_AP -> {
                """
                    Resting will make the current character
                    end this turn and recover 1 HP.

                    Not enough AP!""".trimIndent()
            }
            else -> null
        }
    }

    fun createConfirmationMessage(): String {
        return """
            Resting will make the current character
            end this turn and recover 1 HP.

            Do you want to rest ($REST_AP AP) ?""".trimIndent()
    }

    fun handle(): String {
        currentParticipant.currentAP -= REST_AP
        if (character.currentHp == character.maximumHp) {
            return "${character.name} ended their turn."
        } else {
            hero.recoverPartHp(1)
            return "${character.name} rested for a turn and recovered 1 HP."
        }
    }

}
