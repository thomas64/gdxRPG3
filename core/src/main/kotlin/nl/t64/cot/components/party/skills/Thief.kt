package nl.t64.cot.components.party.skills


class Thief(rank: Int = 0) : SkillItem(
    SkillItemId.THIEF, SkillItemId.THIEF.title, 8f, rank
) {
    override fun getDescription(): String {
        return """
            Allows the possibility to pick locks on treasure chests.
            Increases chance-to-hit with weapons FROM BEHIND in hand-to-hand combat.
            Increases damage-to-inflict with weapons FROM BEHIND in hand-to-hand combat.""".trimIndent()
    }
}
