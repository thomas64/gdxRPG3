package nl.t64.cot.components.party.skills


class Ranger(rank: Int = 0) : SkillItem(
    SkillItemId.RANGER, SkillItemId.RANGER.title, 8f, rank
) {
    override fun getDescription(): String {
        return "Tekst en uitleg over $name."
    }
}
