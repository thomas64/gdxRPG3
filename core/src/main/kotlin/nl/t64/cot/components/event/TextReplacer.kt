package nl.t64.cot.components.event

import nl.t64.cot.Utils


object TextReplacer {

    fun replace(listOfStrings: List<String>): String {
        return listOfStrings.joinToString(System.lineSeparator()) { replace(it) }
    }

    private fun replace(str: String): String {
        val firstIndex = str.indexOf("%").takeUnless { it == -1 } ?: return str
        val substr = str.substring(firstIndex).let {
            it.substring(0, it.indexOf("%", 1) + 1)
        }
        val hasGamePad = Utils.isGamepadConnected()
        return when (substr) {
            "%action%" -> str.replace(substr, if (hasGamePad) "[A] button" else "[A] key")
            "%inventory%" -> str.replace(substr, if (hasGamePad) "[Y] button" else "[I] key")
            "%fast%" -> str.replace(substr, if (hasGamePad) "[RB] button" else "[Shift] key")
            "%slow%" -> str.replace(substr, if (hasGamePad) "[LB] button" else "[Ctrl] key")
            "%minimap%" -> str.replace(substr, if (hasGamePad) "[Select] button" else "[M] key")
            "%logbook%" -> str.replace(substr, if (hasGamePad) "[X] button" else "[L] key")
            else -> throw IllegalArgumentException("Unexpected value: '$substr'")
        }
    }

}
