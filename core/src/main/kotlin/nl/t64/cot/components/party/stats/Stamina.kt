package nl.t64.cot.components.party.stats

import kotlin.math.abs


// no super.bonus for Stamina
class Stamina(rank: Int = 0) : StatItem(
    StatItemId.STAMINA, StatItemId.STAMINA.title, 90, 0.04f, rank, variable = rank
) {
    override fun getDescription(): String {
        return """
            Affects Action Points in battle and is used by magic spells
            and some skill actions. $name is also lost when 
            taking damage from attacks. When $name hits zero,
            your character will experience combat penalties.""".trimIndent()
    }

    override fun doUpgrade() {
        rank += 1
        variable += 1
    }

    fun takeDamage(damage: Int): Int? {
        variable -= damage
        if (variable < 0) {
            val remainingDamage = abs(variable)
            variable = 0
            return remainingDamage
        }
        return null
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

    fun getInflictDamagePenalty(): Int {
        return if (variable <= 0) 5 else 1
    }

    fun getDefensePenalty(): Int {
        return if (variable <= 0) 50 else 0
    }

    fun getChanceToHitPenalty(): Int {
        return if (variable <= 0) 25 else 0
    }

}
