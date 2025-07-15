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
            "%action%" -> str.replace(substr, if (hasGamePad) "[A] [BLACK]button" else "[A] [BLACK]key")
            "%inventory%" -> str.replace(substr, if (hasGamePad) "[Y] [BLACK]button" else "[I] [BLACK]key")
            "%fast%" -> str.replace(substr, if (hasGamePad) "[RB] [BLACK]button" else "[Shift] [BLACK]key")
            "%slow%" -> str.replace(substr, if (hasGamePad) "[LB] [BLACK]button" else "[Ctrl] [BLACK]key")
            "%minimap%" -> str.replace(substr, if (hasGamePad) "[Select] [BLACK]button" else "[M] [BLACK]key")
            "%logbook%" -> str.replace(substr, if (hasGamePad) "[X] [BLACK]button" else "[L] [BLACK]key")
            "%manual%" -> str.replace(substr, if (hasGamePad) "[L] [BLACK]stick" else "[T] [BLACK]key")
            else -> throw IllegalArgumentException("Unexpected value: '$substr'")
        }
    }

}
