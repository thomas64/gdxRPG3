package nl.t64.cot.components.party


enum class CalcAttributeId(override val title: String) : SuperEnum {

    WEIGHT("Weight") {
        override fun getDescription(): String {
            return """
                Decreases amount of Action Points in combat.
                Allows the possibility of shoving a less weighted character in combat.""".trimIndent()
        }
    },

    ACTION_POINTS("Action Points") {
        override fun getDescription(): String {
            return """
                Defines how many actions this character is able to do in combat.
                Action Points are calculated from Stamina and Weight.
                More stamina, more AP. More weight, less AP.""".trimIndent()
        }
    },

    BASE_HIT("Base Hit") {
        override fun getDescription(): String {
            return """
                Defines the change-to-hit an enemy with physical weapons in combat.
                $title is that of the weapon itself clean with no modifiers.
                Total Hit is modified with the skill of the weapon you are currently holding.""".trimIndent()
        }
    },

    DAMAGE("Damage") {
        override fun getDescription(): String {
            return """
                Defines the amount of damage-to-inflict to an enemy with physical weapons in combat.
                Damage is the counterpart of Protection.
                An enemy's protection decreases the damage you inflict.
                Weapon Damage is that of the weapon itself clean with no modifiers. 
                Total Damage is modified with Strength for hand-to-hand combat,
                or Dexterity for ranged combat.""".trimIndent()
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
            return """
                Defines the possibility to block an enemy's
                attack with physical weapons in combat.""".trimIndent()
        }
    },

    SPELL_BATTERY("Spell Battery") {
        override fun getDescription(): String {
            return """
                When casting magic spells,
                $title is used instead of Stamina.""".trimIndent()
        }
    },

    TRANSFORMATION("Transformation") {
        override fun getDescription(): String {
            return "No description"
        }
    };

    abstract fun getDescription(): String

}

fun String.toCalcAttributeId(): CalcAttributeId {
    return when {
        this.lowercase().contains("hit") -> CalcAttributeId.BASE_HIT
        else -> CalcAttributeId.values().first { this.lowercase().contains(it.title.lowercase()) }
    }
}
