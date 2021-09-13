package nl.t64.cot.components.party.skills


class Diplomat(rank: Int = 0) : SkillItem(
    SkillItemId.DIPLOMAT, SkillItemId.DIPLOMAT.title, 4f, rank
) {
    override fun getDescription(): String {
        return """
            Allows for additional conversation responses
            with possibly more favorable outcomes.""".trimIndent()
    }
}
