package nl.t64.cot.components.party.skills


class Alchemist(rank: Int = 0) : SkillItem(
    SkillItemId.ALCHEMIST, SkillItemId.ALCHEMIST.title, 12f, rank
) {
    override fun getDescription(): String {
        return "Tekst en uitleg over $name."
    }
}
