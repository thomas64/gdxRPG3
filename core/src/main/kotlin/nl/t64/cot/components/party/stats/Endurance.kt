package nl.t64.cot.components.party.stats


// no super.bonus for Endurance
class Endurance(rank: Int = 0) : StatItem(
    StatItemId.ENDURANCE, StatItemId.ENDURANCE.title, 40, 0.12f, rank, variable = rank
) {
    override fun getDescription(): String {
        return """
            Affects HP directly. Represents the real physical
            damage your character can take before dying.
            $name also affects turn speed.""".trimIndent()
    }

    override fun doUpgrade() {
        rank += 1
        variable += 1
    }

    // todo, speciale bonus toepassen inventoryItem: epic_ring_of_healing, die 1 edu geeft om je leven 1malig te redden.
    fun takeDamage(damage: Int) {
        variable -= damage
        if (variable < 0) {
            variable = 0
        }
    }

    fun restorePart(healPoints: Int): Int? {
        variable += healPoints
        if (variable > rank) {
            val remainingHealPoints = variable - rank
            variable = rank
            return remainingHealPoints
        }
        return null
    }

    fun restore() {
        variable = rank
    }

}
