package nl.t64.cot.components.party.skills


class Shield(rank: Int = 0) : SkillItem(
    SkillItemId.SHIELD, SkillItemId.SHIELD.title, 4f, rank
) {
    override fun getDescription(): String {
        return """
            Allows the possibility of equipping shields.
            In combat, a shield may block the enemy's attack with physical weapons.
            Each rank in Shield increases Defense by 10%.""".trimIndent()
    }
}
