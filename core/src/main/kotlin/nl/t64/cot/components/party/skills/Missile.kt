package nl.t64.cot.components.party.skills


class Missile(rank: Int = 0) : SkillItem(
    SkillItemId.MISSILE, SkillItemId.MISSILE.title, 4.8f, rank
) {
    override fun getDescription(): String {
        return """
            Allows the possibility of equipping missile weapons.
            Increases chance-to-hit with missile weapons in combat.
            Each rank in Missile increases Total Hit by 10%.""".trimIndent()
    }
}
