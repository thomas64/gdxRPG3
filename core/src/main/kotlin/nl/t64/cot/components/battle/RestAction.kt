package nl.t64.cot.components.battle

import nl.t64.cot.components.party.HeroItem


class RestAction(
    currentParticipant: Participant
) {
    private val character: Character = currentParticipant.character


    fun createConfirmationMessage(): String {
        return """
            Resting will make the current character
            end this turn and recover 1 HP.

            Do you want to rest for 2 AP?""".trimIndent()
    }

    fun handle(): String {
        if (character.currentHp == character.maximumHp) {
            return "${character.name} ended their turn."
        } else {
            (character as HeroItem).recoverPartHp(1)
            return "${character.name} rested for a turn and recovered 1 HP."
        }
    }

}
