package nl.t64.cot.components.party.skills


class Shield(rank: Int = 0) : SkillItem(
    SkillItemId.SHIELD, SkillItemId.SHIELD.title, 4f, rank
) {
    override fun getDescription(): String {
        return """
            Allows the possibility of equipping shields.
            Improves shield defenses while blocking in combat.""".trimIndent()
    }
}
