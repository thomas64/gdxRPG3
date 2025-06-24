package nl.t64.cot.components.party.abilities

import nl.t64.cot.resources.ConfigDataLoader


object AbilityDatabase {

    private val abilityItems: Map<String, AbilityItem> = ConfigDataLoader.createAbilities()

    fun createAbilityItem(abilityId: String): AbilityItem {
        val abilityItem = abilityItems[abilityId.lowercase()]!!
        return abilityItem.createCopy()
    }

    fun getDescription(abilityId: AbilityItemId): List<String> {
        return when (abilityId) {
            AbilityItemId.STRIKE_2 -> emptyList()
            AbilityItemId.STRIKE_3 -> strikeDescription()
            AbilityItemId.STRIKE_4 -> emptyList()
            AbilityItemId.BITE_3 -> throw IllegalArgumentException("AbilityItemId cannot be BITE_3.")
            AbilityItemId.BITE_4 -> throw IllegalArgumentException("AbilityItemId cannot be BITE_4.")
            AbilityItemId.BODY_SLAM_2 -> throw IllegalArgumentException("AbilityItemId cannot be BODY_SLAM_2.")
            AbilityItemId.STAGGER -> staggerDescription()
            AbilityItemId.DOUBLE_THROW -> doubleThrowDescription()
            AbilityItemId.FIRE -> fireDescription()
            AbilityItemId.ELFIRE -> elfireDescription()
            AbilityItemId.ARCFIRE -> arcfireDescription()
            AbilityItemId.REXFIRE -> rexfireDescription()
            AbilityItemId.WIND -> windDescription()
            AbilityItemId.ELWIND -> elwindDescription()
            AbilityItemId.ARCWIND -> arcwindDescription()
            AbilityItemId.REXWIND -> rexwindDescription()
            AbilityItemId.THUNDER -> thunderDescription()
            AbilityItemId.ELTHUNDER -> elthunderDescription()
            AbilityItemId.ARCTHUNDER -> arcthunderDescription()
            AbilityItemId.REXTHUNDER -> rexthunderDescription()
            AbilityItemId.MAGIC_SHIELD -> magicShieldDescription()
            AbilityItemId.RESISTANCE -> resistanceDescription()
            AbilityItemId.TELEPORTATION -> teleportationDescription()
            AbilityItemId.BRILLIANCE -> brillianceDescription()
            AbilityItemId.FINESSE -> finesseDescription()
            AbilityItemId.MIGHT -> mightDescription()
            AbilityItemId.HASTE -> hasteDescription()
            AbilityItemId.STUPIDITY -> stupidityDescription()
            AbilityItemId.CLUMSINESS -> clumsinessDescription()
            AbilityItemId.DEBILITATION -> debilitationDescription()
            AbilityItemId.SLUGGISHNESS -> sluggishnessDescription()
        }
    }

    private fun strikeDescription(): List<String> {
        return listOf(
            "A basic physical attack with a weapon,",
            "that deals damage to a single target."
        )
    }

    private fun staggerDescription(): List<String> {
        return listOf(
            "A hand-to-hand attack with a weapon, that deals damage to a single target.",
            "When successful, Stagger moves the target to the bottom of the turn order.",
            "Each successful Stagger reduces the chance of the next Stagger by half."
        )
    }

    private fun doubleThrowDescription(): List<String> {
        return listOf(
            "A ranged attack with a throw weapon, that deals damage to a single target.",
            "Double Throw attacks twice, but both attacks have a lower chance to hit."
        )
    }

    private fun fireDescription(): List<String> {
        return listOf(
            "A magical attack with a Fire Staff,",
            "that deals damage to a single target."
        )
    }

    private fun elfireDescription(): List<String> {
        return listOf(
            "A magical attack with a Fire Staff,",
            "that deals damage to a single target.",
            "1.5 times more powerful than Fire."
        )
    }

    private fun arcfireDescription(): List<String> {
        return listOf(
            "A magical attack with a Fire Staff,",
            "that deals damage to a single target.",
            "2 times more powerful than Fire."
        )
    }

    private fun rexfireDescription(): List<String> {
        return listOf(
            "A magical attack with a Fire Staff,",
            "that deals damage to a single target.",
            "2.5 times more powerful than Fire."
        )
    }

    private fun windDescription(): List<String> {
        return listOf(
            "A magical attack with a Wind Staff,",
            "that deals damage to a single target."
        )
    }

    private fun elwindDescription(): List<String> {
        return listOf(
            "A magical attack with a Wind Staff,",
            "that deals damage to a single target.",
            "1.5 times more powerful than Wind."
        )
    }

    private fun arcwindDescription(): List<String> {
        return listOf(
            "A magical attack with a Wind Staff,",
            "that deals damage to a single target.",
            "2 times more powerful than Wind."
        )
    }

    private fun rexwindDescription(): List<String> {
        return listOf(
            "A magical attack with a Wind Staff,",
            "that deals damage to a single target.",
            "2.5 times more powerful than Wind."
        )
    }

    private fun thunderDescription(): List<String> {
        return listOf(
            "A magical attack with a Thunder Staff,",
            "that deals damage to a single target."
        )
    }

    private fun elthunderDescription(): List<String> {
        return listOf(
            "A magical attack with a Thunder Staff,",
            "that deals damage to a single target.",
            "1.5 times more powerful than Thunder."
        )
    }

    private fun arcthunderDescription(): List<String> {
        return listOf(
            "A magical attack with a Thunder Staff,",
            "that deals damage to a single target.",
            "2 times more powerful than Thunder."
        )
    }

    private fun rexthunderDescription(): List<String> {
        return listOf(
            "A magical attack with a Thunder Staff,",
            "that deals damage to a single target.",
            "2.5 times more powerful than Thunder."
        )
    }

    private fun magicShieldDescription(): List<String> {
        return listOf(
            "Friendly target of the spell add 2 to their",
            "Protection value for each rank in 'Wizard'.",
            "May only be cast once on each target."
        )
    }

    private fun resistanceDescription(): List<String> {
        return listOf(
            "Friendly target of the spell add 2 to their",
            "Resistance value for each rank in 'Wizard'.",
            "May only be cast once on each target."
        )
    }

    private fun teleportationDescription(): List<String> {
        return listOf(
            "Caster teleports to a chosen point on the battlefield."
        )
    }

    private fun brillianceDescription(): List<String> {
        return listOf(
            "Friendly target of the spell gains 2",
            "Intelligence for each rank in 'Wizard'.",
            "May only be cast once on each target."
        )
    }

    private fun finesseDescription(): List<String> {
        return listOf(
            "Friendly target of the spell gains 2",
            "Dexterity for each rank in 'Wizard'.",
            "May only be cast once on each target."
        )
    }

    private fun mightDescription(): List<String> {
        return listOf(
            "Friendly target of the spell gains 2",
            "Strength for each rank in 'Wizard'.",
            "May only be cast once on each target."
        )
    }

    private fun hasteDescription(): List<String> {
        return listOf(
            "Friendly target of the spell gains 2",
            "Speed for each rank in 'Wizard'.",
            "May only be cast once on each target."
        )
    }

    private fun stupidityDescription(): List<String> {
        return listOf(
            "Enemy target of the spell loses 1",
            "Intelligence for each rank in 'Wizard'.",
            "May only be cast once on each target."
        )
    }

    private fun clumsinessDescription(): List<String> {
        return listOf(
            "Enemy target of the spell loses 1",
            "Dexterity for each rank in 'Wizard'.",
            "May only be cast once on each target."
        )
    }

    private fun debilitationDescription(): List<String> {
        return listOf(
            "Enemy target of the spell loses 1",
            "Strength for each rank in 'Wizard'.",
            "May only be cast once on each target."
        )
    }

    private fun sluggishnessDescription(): List<String> {
        return listOf(
            "Enemy target of the spell loses 1",
            "Speed for each rank in 'Wizard'.",
            "May only be cast once on each target."
        )
    }

}
