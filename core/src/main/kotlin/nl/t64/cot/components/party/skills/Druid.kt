package nl.t64.cot.components.party.skills


class Druid(rank: Int = 0) : SkillItem(
    SkillItemId.DRUID, SkillItemId.DRUID.title, 1f, rank
) {
    override fun getDescription(): String {
        return """
            Allows the possibility of talking to animals.""".trimIndent()
    }
}
