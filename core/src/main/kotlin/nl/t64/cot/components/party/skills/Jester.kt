package nl.t64.cot.components.party.skills


class Jester(rank: Int = 0) : SkillItem(
    SkillItemId.JESTER, SkillItemId.JESTER.title, 1f, rank
) {
    override fun getDescription(): String {
        return """
            Allows for additional conversation responses
            with possibly more favorable outcomes.""".trimIndent()
    }
}
