package nl.t64.cot.components.party.skills


class Merchant(rank: Int = 0) : SkillItem(
    SkillItemId.MERCHANT, SkillItemId.MERCHANT.title, 6f, rank
) {
    override fun getDescription(): String {
        return "Tekst en uitleg over $name."
    }
}
