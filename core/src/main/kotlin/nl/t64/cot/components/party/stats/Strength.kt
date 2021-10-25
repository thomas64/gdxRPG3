package nl.t64.cot.components.party.stats


// no super.variable for Strength
class Strength(rank: Int = 0) : StatItem(
    StatItemId.STRENGTH, StatItemId.STRENGTH.title, 30, 0.12f, rank, bonus = 0
) {
    override fun getDescription(): String {
        return """
            Increases damage-to-inflict with weapons in hand-to-hand combat.
            Each rank in Strength increases Damage by 5%.""".trimIndent()
    }
}
