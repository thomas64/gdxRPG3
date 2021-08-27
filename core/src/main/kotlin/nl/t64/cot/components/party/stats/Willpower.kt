package nl.t64.cot.components.party.stats


// no super.variable for Willpower
class Willpower(rank: Int = 0) : StatItem(
    StatItemId.WILLPOWER, StatItemId.WILLPOWER.title, 30, 0.12f, rank, bonus = 0
) {
    override fun getDescription(): String {
        return "Tekst en uitleg over $name."
    }
}
