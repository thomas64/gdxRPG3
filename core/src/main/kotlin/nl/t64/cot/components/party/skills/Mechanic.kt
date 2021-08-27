package nl.t64.cot.components.party.skills


class Mechanic(rank: Int = 0) : SkillItem(
    SkillItemId.MECHANIC, SkillItemId.MECHANIC.title, 4f, rank
) {
    override fun getDescription(): String {
        return "Tekst en uitleg over $name."
    }
}
