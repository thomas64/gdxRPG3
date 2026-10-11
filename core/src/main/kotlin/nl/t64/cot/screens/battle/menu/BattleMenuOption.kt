package nl.t64.cot.screens.battle.menu


class BattleMenuOption(
    val name: String,
    private val cost: String,
    val isEnabled: Boolean,
    private val onSelect: () -> Unit
) {
    private val label: String = possibleGetGrayPrefix() + createLabelLine()

    fun select() {
        if (!isEnabled) return

        onSelect.invoke()
    }

    override fun toString(): String {
        return label
    }

    private fun possibleGetGrayPrefix(): String {
        return if (isEnabled) "" else "[GRAY]"
    }

    private fun createLabelLine(): String {
        if (cost.isEmpty()) return name
        return String.format("%-11s%7s", name, cost)
    }

}
