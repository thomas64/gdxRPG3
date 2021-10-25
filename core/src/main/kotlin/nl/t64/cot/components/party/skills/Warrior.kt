package nl.t64.cot.components.party.skills


class Warrior(rank: Int = 0) : SkillItem(
    SkillItemId.WARRIOR, SkillItemId.WARRIOR.title, 8f, rank
) {
    override fun getDescription(): String {
        return """
            Allows the possibility of scoring critical hits in combat.
            Each rank in Warrior increases the probability of this by 4%.""".trimIndent()
    }
}
