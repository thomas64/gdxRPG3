package nl.t64.cot.components.party


enum class CalcAttributeId(override val title: String) : SuperEnum {

    WEIGHT("Weight") {
        override fun getDescription(): String {
            return """
                Decreases amount of movepoints in combat.
                Allows the possibility of shoving a less weighted character in combat.""".trimIndent()
        }
    },

    MOVEPOINTS("Movepoints") {
        override fun getDescription(): String {
            return """
                Defines how many steps this character is able to take in combat.
                Movepoints are calculated from Stamina and Weight.
                More stamina, more movepoints. More weight, less movepoints.""".trimIndent()
        }
    },

    BASE_HIT("Base Hit") {
        override fun getDescription(): String {
            return "ToDo Base Hit"
        }
    },

    DAMAGE("Damage") {
        override fun getDescription(): String {
            return """
                Defines the amount of damage-to-inflict to an enemy with physical weapons in combat.
                Damage is the counterpart of Protection.
                An enemy's protection decreases the damage you inflict.""".trimIndent()
        }
    },

    PROTECTION("Protection") {
        override fun getDescription(): String {
            return """
                Decreases the enemy's damage-to-inflict with physical weapons in combat.
                Protection is the counterpart of Damage.
                A complete armor set from the same type results into bonus protection.""".trimIndent()
        }
    },

    DEFENSE("Defense") {
        override fun getDescription(): String {
            return "ToDo Defense"
        }
    },

    SPELL_BATTERY("Spell Battery") {
        override fun getDescription(): String {
            return "ToDo Spell Battery"
        }
    };

    abstract fun getDescription(): String

}

fun String.toCalcAttributeId(): CalcAttributeId? {
    return CalcAttributeId.values().firstOrNull { this.lowercase().contains(it.title.lowercase()) }
}
