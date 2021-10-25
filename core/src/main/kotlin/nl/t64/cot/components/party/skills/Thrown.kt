package nl.t64.cot.components.party.skills


class Thrown(rank: Int = 0) : SkillItem(
    SkillItemId.THROWN, SkillItemId.THROWN.title, 3.2f, rank
) {
    override fun getDescription(): String {
        return """
            Allows the possibility of equipping thrown weapons.
            Increases chance-to-hit with thrown weapons in combat.
            Each rank in Thrown increases Total Hit by 10%.""".trimIndent()
    }
}
