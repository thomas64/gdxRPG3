package nl.t64.cot.components.party.skills


class Gambler(rank: Int = 0) : SkillItem(
    SkillItemId.GAMBLER, SkillItemId.GAMBLER.title, 1f, rank
) {
    override fun getDescription(): String {
        return """
            Randomly increases/ decreases chance-to-hit with all attacks in combat.
            Each rank in Gambler increases/ decreases Total Hit by 5%.
            
            Randomly increases/ decreases damage-to-inflict with all attacks in combat.
            Each rank in Gambler increases/ decreases Damage by 5%.""".trimIndent()
    }
}
