package nl.t64.cot.components.party.skills


class Pole(rank: Int = 0) : SkillItem(
    SkillItemId.POLE, SkillItemId.POLE.title, 3.2f, rank
) {
    override fun getDescription(): String {
        return """
            Allows the possibility of equipping pole weapons.
            Increases chance-to-hit with pole weapons in combat.
            Increases damage-to-inflict with pole weapons in combat.""".trimIndent()
    }
}
