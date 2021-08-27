package nl.t64.cot.components.party.skills


class Sword(rank: Int = 0) : SkillItem(
    SkillItemId.SWORD, SkillItemId.SWORD.title, 4.8f, rank
) {
    override fun getDescription(): String {
        return """
            Allows the possibility of equipping swords and daggers.
            Increases chance-to-hit with swords and daggers in combat.
            Increases damage-to-inflict with swords and daggers in combat.""".trimIndent()
    }
}
