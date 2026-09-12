package nl.t64.cot.components.party


enum class CalcAttributeId(override val title: String) : SuperEnum {

    ACTION_POINTS("Action Points") {
        override fun getDescription(): String {
            return """
                - '$title' (AP) define how many actions this
                  character is able to do each turn in combat.

                - Each 20 ranks in 'Intelligence', 'Dexterity',
                  'Strength' and 'Speed' together increases AP by 1.
                  Plus 2.""".trimIndent()
        }
    },

    DURABILITY("Durability") {
        override fun getDescription(): String {
            return "No description"
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
                  and the rank of your 'Intelligence', 'Dexterity' or 'Strength'.
                  Which one depends on the weapon's skill type.""".trimIndent()
        }
    },

    PROTECTION("Protection") {
        override fun getDescription(): String {
            return """
                - '$title' decreases the enemy's damage it inflicts
                  to you with physical weapons in combat.

                - '$title' is the counterpart of 'Damage'.""".trimIndent()
        }
    },

    MAGIC_PROTECTION("Magic Protection") {
        override fun getDescription(): String {
            return """
                - '$title' decreases the enemy's damage it inflicts
                  to you with magic spells in combat.

                - '$title' is the counterpart of 'Damage'.""".trimIndent()
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
    };

    abstract fun getDescription(): String

}
