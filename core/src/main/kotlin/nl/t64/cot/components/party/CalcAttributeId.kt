package nl.t64.cot.components.party


enum class CalcAttributeId(override val title: String) : SuperEnum {

    ACTION_POINTS("Action Points") {
        override fun getDescription(): String {
            return """
                - '$title' (AP) define how many actions this
                  character is able to do each turn in combat.

                - Each 10 ranks in 'Intelligence', 'Dexterity',
                  'Strength' and 'Speed' together increases AP by 1.""".trimIndent()
        }
    },

    BASE_HIT("Hit Chance") {
        override fun getDescription(): String {
            return """
                - 'Chance to hit' defines your chance to hit
                  an enemy with physical weapons in combat.

                - 'Chance to hit' is derived from the weapon
                  you are currently holding and the rank of
                  your skill of that weapon's skill type.""".trimIndent()
        }
    },

    DAMAGE("Damage") {
        override fun getDescription(): String {
            return """
                - '$title' defines the amount of damage you inflict
                  to an enemy with physical weapons in combat.

                - '$title' is the counterpart of 'Protection'.

                - An enemy's 'Protection' decreases the '$title' you inflict.

                - '$title' is derived from the weapon you are currently holding
                  and the rank of your 'Strength' for hand-to-hand combat,
                  or the rank of your 'Dexterity' for ranged combat.""".trimIndent()
        }
    },

    PROTECTION("Protection") {
        override fun getDescription(): String {
            return """
                - '$title' decreases the enemy's damage it inflicts
                  to you with physical weapons in combat.

                - '$title' is the counterpart of 'Damage'.

                - A complete armor set from the same
                  type results into bonus '$title'.""".trimIndent()
        }
    },

    DEFENSE("Defense") {
        override fun getDescription(): String {
            return """
                '$title' defines your possibility to block an
                enemy's attack with physical weapons in combat.""".trimIndent()
        }
    },

    SPELL_BATTERY("Spell Battery") {
        override fun getDescription(): String {
            return """
                When casting magic spells,
                '$title' is used instead of 'SP'.""".trimIndent()
        }
    },

    TRANSFORMATION("Transformation") {
        override fun getDescription(): String {
            return "No description"
        }
    },

    DURABILITY("Durability") {
        override fun getDescription(): String {
            return "No description"
        }
    };

    abstract fun getDescription(): String

}

fun String.toCalcAttributeId(): CalcAttributeId {
    return when {
        this.lowercase().contains("hit") -> CalcAttributeId.BASE_HIT
        else -> CalcAttributeId.entries.first { this.lowercase().contains(it.title.lowercase()) }
    }
}
