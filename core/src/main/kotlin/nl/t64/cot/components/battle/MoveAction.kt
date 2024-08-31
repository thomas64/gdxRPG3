package nl.t64.cot.components.battle


class MoveAction(
    private val battleField: BattleField,
    currentParticipant: Participant,
    private val newSpace: Int
) {
    private val character: Character = currentParticipant.character


    fun createConfirmationMessage(): String {
        return """
            Do you want to move ${character.name} to space ${newSpace + 1} ?""".trimIndent()
    }

    fun handle() {
        battleField.moveCharacter(character, newSpace)
    }

}
