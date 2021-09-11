package nl.t64.cot.components.party.skills


class Merchant(rank: Int = 0) : SkillItem(
    SkillItemId.MERCHANT, SkillItemId.MERCHANT.title, 6f, rank
) {
    override fun getDescription(): String {
        return """
            Lowers the price of items in shops when buying.
            Raises the value of your items when selling.
            For every rank of $name, 1% of the value in your favor.
            Rank is stacked cumulative for all party members.""".trimIndent()
    }
}
