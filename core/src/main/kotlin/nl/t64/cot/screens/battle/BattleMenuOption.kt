package nl.t64.cot.screens.battle

import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe


class BattleMenuOption(
    val name: String,
    private val cost: String,
    val isEnabled: Boolean,
    private val onSelect: () -> Unit,
    private val playsConfirmSound: Boolean = true
) {
    private val label: String = possibleGetGrayPrefix() + createLabelLine()

    fun select() {
        if (playsConfirmSound) {
            playSe(AudioEvent.SE_MENU_CONFIRM)
        }
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
