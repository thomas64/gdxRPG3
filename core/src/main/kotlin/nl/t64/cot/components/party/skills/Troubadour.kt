package nl.t64.cot.components.party.skills


class Troubadour(rank: Int = 0) : SkillItem(
    SkillItemId.TROUBADOUR, SkillItemId.TROUBADOUR.title, 8f, rank
) {
    override fun getDescription(): String {
        return """
            Allows the possibility to play and sing inspirationally in combat,
            increasing your party's chance-to-hit
            and decreasing the enemy's chance-to-hit.""".trimIndent()
    }
}
