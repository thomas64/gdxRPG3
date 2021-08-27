package nl.t64.cot.components.party.stats


// no super.variable for Intelligence
class Intelligence(rank: Int = 0) : StatItem(
    StatItemId.INTELLIGENCE, StatItemId.INTELLIGENCE.title, 30, 0.12f, rank, bonus = 0
) {
    override fun getDescription(): String {
        return "Tekst en uitleg over $name."
    }
}
