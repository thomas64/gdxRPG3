package nl.t64.cot.components.party.stats

import nl.t64.cot.resources.ConfigDataLoader


object StatDatabase {

    private val statItems: Map<String, StatItem> = ConfigDataLoader.createStats()

    fun createStatItem(statItemId: StatItemId, rank: Int): StatItem {
        val statItem = statItems[statItemId.name.lowercase()]!!
        return statItem.createCopy(rank)
    }

    fun getDescription(statId: StatItemId): List<String> {
        return when (statId) {
            StatItemId.INTELLIGENCE -> intelligenceDescription()
            StatItemId.DEXTERITY -> dexterityDescription()
            StatItemId.STRENGTH -> strengthDescription()
            StatItemId.SPEED -> speedDescription()
            StatItemId.WILLPOWER -> willpowerDescription()
            StatItemId.CONSTITUTION -> constitutionDescription()
            StatItemId.STAMINA -> staminaDescription()
        }
    }

    private fun intelligenceDescription(): List<String> {
        return listOf(
            "- 'Intelligence' increases the damage you inflict with intelligence based weapons in combat.",
            "  Each rank in 'Intelligence' increases the 'Damage' of those weapons by 5%.",
            "",
            "- 'Intelligence' increases the damage you inflict with offensive magic spells in combat.",
            "  Each rank in 'Intelligence' increases 'Spell Damage' by 5%.",
            "",
            "- Each 20 ranks in 'Intelligence' increases AP by 1."
        )
    }

    private fun dexterityDescription(): List<String> {
        return listOf(
            "- 'Dexterity' increases the damage you inflict with dexterity based weapons in combat.",
            "  Each rank in 'Dexterity' increases the 'Damage' of those weapons by 5%.",
            "",
            "- Each 20 ranks in 'Dexterity' increases AP by 1."
        )
    }

    private fun strengthDescription(): List<String> {
        return listOf(
            "- 'Strength' increases the damage you inflict with strength based weapons in combat.",
            "  Each rank in 'Strength' increases the 'Damage' of those weapons by 5%.",
            "",
            "- Each 20 ranks in 'Strength' increases AP by 1."
        )
    }

    private fun speedDescription(): List<String> {
        return listOf(
            "- 'Speed' increases your combat initiative.",
            "  More 'Speed' means a faster turn rate in combat.",
            "",
            "- Each 20 ranks in 'Speed' increases AP by 1."
        )
    }

    private fun willpowerDescription(): List<String> {
        return listOf(
            "- 'Willpower' increases your defenses against enemy offensive magic spells in combat.",
            "  Each rank in 'Willpower' increases 'Magic Protection' by 3."
        )
    }

    private fun constitutionDescription(): List<String> {
        return listOf(
            "- 'Constitution' represents the real physical damage this",
            "  character can take in combat before dying.",
            "  Each rank in 'Constitution' increases 'HP' by 4 points."
        )
    }

    private fun staminaDescription(): List<String> {
        return listOf(
            "- 'Stamina' represents energy for magic spells and other",
            "  special attacks in combat.",
            "  Each rank in 'Stamina' increases 'SP' by 2 points."
        )
    }

}
