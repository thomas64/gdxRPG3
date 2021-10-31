package nl.t64.cot.components.party.skills


class Loremaster(rank: Int = 0) : SkillItem(
    SkillItemId.LOREMASTER, SkillItemId.LOREMASTER.title, 6f, rank
) {
    override fun getDescription(): String {
        return """
            Allows the possibility of deciphering
            books, scrolls and other old writings.""".trimIndent()
    }
}
