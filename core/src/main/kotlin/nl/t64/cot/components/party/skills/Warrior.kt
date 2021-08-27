package nl.t64.cot.components.party.skills


class Warrior(rank: Int = 0) : SkillItem(
    SkillItemId.WARRIOR, SkillItemId.WARRIOR.title, 8f, rank
) {
    override fun getDescription(): String {
        return """
            Allows the possibility of scoring critical hits in combat.
            Increases chance-to-hit with physical weapons in combat.
            Increases damage-to-inflict with physical weapons in combat.
            Improves shield defenses while blocking in combat.""".trimIndent()
    }
}
