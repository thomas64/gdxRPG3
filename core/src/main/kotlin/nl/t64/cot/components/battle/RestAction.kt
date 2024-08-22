package nl.t64.cot.components.battle

import nl.t64.cot.components.party.HeroItem


class RestAction(
    private val character: Character
) {

    fun createConfirmationMessage(): String {
        return """
            Resting will make the current character
            skip this turn and recover 1 HP.

            Do you want to rest for this turn?""".trimIndent()
    }

    fun handle(): String {
        if (character.currentHp == character.maximumHp) {
            return "${character.name} skipped a turn."
        } else {
            (character as HeroItem).recoverPartHp(1)
            return "${character.name} rested for a turn and recovered 1 HP."
        }
    }

}
