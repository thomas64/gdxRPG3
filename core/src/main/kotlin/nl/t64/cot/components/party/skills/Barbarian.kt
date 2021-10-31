package nl.t64.cot.components.party.skills


class Barbarian(rank: Int = 0) : SkillItem(
    SkillItemId.BARBARIAN, SkillItemId.BARBARIAN.title, 1f, rank
) {
    override fun getDescription(): String {
        return """
            Allows for additional conversation responses
            with possibly more favorable outcomes.""".trimIndent()
    }
}
