package nl.t64.cot.components.party.skills


class Hafted(rank: Int = 0) : SkillItem(
    SkillItemId.HAFTED, SkillItemId.HAFTED.title, 3.2f, rank
) {
    override fun getDescription(): String {
        return """
            Allows the possibility of equipping hafted weapons.
            Increases chance-to-hit with hafted weapons in combat.
            Each rank in Hafted increases Total Hit by 10%.""".trimIndent()
    }
}

