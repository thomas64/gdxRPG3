package nl.t64.cot.components.party.skills


class Wizard(rank: Int = 0) : SkillItem(
    SkillItemId.WIZARD, SkillItemId.WIZARD.title, 12f, rank
) {
    override fun getDescription(): String {
        return "Tekst en uitleg over $name."
    }
}
