package nl.t64.cot.components.battle


class Battle(
    val battlers: List<Battler> = emptyList(),
    val background: String = "",
    val isEscapable: Boolean = true,
    var hasWon: Boolean = false
) {

    fun reset() {
        hasWon = false
    }

}
