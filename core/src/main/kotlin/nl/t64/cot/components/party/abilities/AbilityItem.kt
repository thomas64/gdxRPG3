package nl.t64.cot.components.party.abilities

import nl.t64.cot.components.party.PersonalityItem


data class AbilityItem(
    val id: AbilityItemId = AbilityItemId.STRIKE_1,   // Value will be replaced when constructed.
    val name: String = "",
    val ap: Int = 0,
    val sp: Int = 0,
    private val description: List<String> = emptyList()
) : PersonalityItem {

    fun createCopy(): AbilityItem {
        return copy()
    }

    override fun getTotalDescription(): String {
        TODO("Not yet implemented")
    }

}
