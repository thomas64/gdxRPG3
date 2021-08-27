package nl.t64.cot.components.party.stats


// no super.variable for Dexterity
class Dexterity(rank: Int = 0) : StatItem(
    StatItemId.DEXTERITY, StatItemId.DEXTERITY.title, 30, 0.12f, rank, bonus = 0
) {
    override fun getDescription(): String {
        return """
            Increases chance-to-hit with ranged weapons in combat.
            Increases damage-to-inflict with ranged weapons in combat.""".trimIndent()
    }
}
