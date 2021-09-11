package nl.t64.cot.components.party.skills


class Scholar(rank: Int = 0) : SkillItem(
    SkillItemId.SCHOLAR, SkillItemId.SCHOLAR.title, 6f, rank
) {
    override fun getDescription(): String {
        return """
            Lowers the 'XP to Invest' needed for training Skills and learning Spells.
            For every 1 rank of $name, 1% less XP.""".trimIndent()
    }
}
