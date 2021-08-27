package nl.t64.cot.components.party.skills


class Healer(rank: Int = 0) : SkillItem(
    SkillItemId.HEALER, SkillItemId.HEALER.title, 8f, rank
) {
    override fun getDescription(): String {
        return "Tekst en uitleg over $name."
    }
}
