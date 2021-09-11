package nl.t64.cot.components.party.skills


class Ranger(rank: Int = 0) : SkillItem(
    SkillItemId.RANGER, SkillItemId.RANGER.title, 8f, rank
) {
    override fun getDescription(): String {
        return """
            Increases the amount of resources you'll find in the world.
            For every rank of $name, 2% more resources.
            Rank is stacked cumulative for all party members.""".trimIndent()
    }
}
