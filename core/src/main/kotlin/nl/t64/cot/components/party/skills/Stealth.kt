package nl.t64.cot.components.party.skills


class Stealth(rank: Int = 0) : SkillItem(
    SkillItemId.STEALTH, SkillItemId.STEALTH.title, 4f, rank
) {
    override fun getDescription(): String {
        return "Tekst en uitleg over $name."
    }
}
