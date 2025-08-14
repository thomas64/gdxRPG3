package nl.t64.cot.components.party.skills

import nl.t64.cot.resources.ConfigDataLoader


object SkillDatabase {

    private val skillItems: Map<String, SkillItem> = ConfigDataLoader.createSkills()

    fun createSkillItem(skillId: String, rank: Int): SkillItem {
        val skillItem = skillItems[skillId.lowercase()]!!
        return skillItem.createCopy(rank)
    }

    fun getDescription(skillId: SkillItemId): List<String> {
        return when (skillId) {
            SkillItemId.NONE -> throw IllegalArgumentException("SkillItemId cannot be NONE.")

            SkillItemId.BARBARIAN -> barbarianDescription()
            SkillItemId.DIPLOMAT -> diplomatDescription()
            SkillItemId.JESTER -> jesterDescription()
            SkillItemId.DRUID -> druidDescription()
            SkillItemId.LOREMASTER -> loremasterDescription()

            SkillItemId.ALCHEMIST -> alchemistDescription()
            SkillItemId.MECHANIC -> mechanicDescription()
            SkillItemId.RANGER -> rangerDescription()
            SkillItemId.MERCHANT -> merchantDescription()

            SkillItemId.STEALTH -> stealthDescription()
            SkillItemId.GAMBLER -> gamblerDescription()
            SkillItemId.HEALER -> healerDescription()
            SkillItemId.TROUBADOUR -> troubadourDescription()
            SkillItemId.THIEF -> thiefDescription()
            SkillItemId.WARRIOR -> warriorDescription()
            SkillItemId.WIZARD -> wizardDescription()

            SkillItemId.SWORD -> swordDescription()
            SkillItemId.AXE -> axeDescription()
            SkillItemId.SPEAR -> spearDescription()
            SkillItemId.DAGGER -> daggerDescription()
            SkillItemId.THROW -> throwDescription()
            SkillItemId.BOW -> bowDescription()
            SkillItemId.STAFF -> staffDescription()
            SkillItemId.SHIELD -> shieldDescription()

            SkillItemId.STAFF_FIRE -> throw IllegalArgumentException("SkillItemId cannot be STAFF_FIRE.")
            SkillItemId.STAFF_WIND -> throw IllegalArgumentException("SkillItemId cannot be STAFF_WIND.")
            SkillItemId.STAFF_THUNDER -> throw IllegalArgumentException("SkillItemId cannot be STAFF_THUNDER.")
        }
    }

    // communication //

    private fun barbarianDescription(): List<String> {
        return listOf(
            "- 'Barbarian' allows for additional conversation",
            "  responses, mostly used for intimidating people,",
            "  with possibly more favorable outcomes."
        )
    }

    private fun diplomatDescription(): List<String> {
        return listOf(
            "- 'Diplomat' allows for additional conversation",
            "  responses, mostly used for persuading people,",
            "  with possibly more favorable outcomes."
        )
    }

    private fun jesterDescription(): List<String> {
        return listOf(
            "- 'Jester' allows for additional conversation responses,",
            "  mostly used for joking and lying to people,",
            "  with possibly more favorable outcomes."
        )
    }

    private fun druidDescription(): List<String> {
        return listOf(
            "- 'Druid' allows the possibility of talking to animals."
        )
    }

    private fun loremasterDescription(): List<String> {
        return listOf(
            "- 'Loremaster' allows the possibility of deciphering",
            "  books, scrolls and other old writings."
        )
    }

    // civil //

    private fun alchemistDescription(): List<String> {
        return listOf(
            "- 'Alchemist' allows for manufacturing various",
            "  magical potions out of various resources.",
            "  To do this, you can select this skill now."
        )
    }

    private fun mechanicDescription(): List<String> {
        return listOf(
            "- 'Mechanic' allows the possibility to disarm",
            "  traps on treasure chests.",
            "",
            "- 'Mechanic' allows for manufacturing various",
            "  weapons and armor out of various resources.",
            "  To do this, you can select this skill now."
        )
    }

    private fun rangerDescription(): List<String> {
        return listOf(
            "- 'Ranger' increases the amount of resources you'll find in the world.",
            "  For every rank of 'Ranger', 2% more resources.",
            "  'Ranger' ranks are stacked cumulative for all party members."
        )
    }

    private fun merchantDescription(): List<String> {
        return listOf(
            "- 'Merchant' lowers the price of items in shops when buying,",
            "  and raises the value of your items when selling.",
            "  For every rank of 'Merchant', 1% of the value in your favor.",
            "  'Merchant' ranks are stacked cumulative for all party members."
        )
    }

    // combat //

    private fun stealthDescription(): List<String> {
        return listOf(
            "- 'Stealth' increases the chance to successfully flee combat.",
            "  Each rank in 'Stealth' increases the probability of this by 2%.",
            "",
            "- 'Stealth' decreases the attention this character draws,",
            "  in relation to other party members, from enemies in combat.",
            "",
            "- 'Stealth' decreases the amount of Action Points it takes to",
            "  free this character from when they are locked in battle."
        )
    }

    private fun gamblerDescription(): List<String> {
        return listOf(
            "- 'Gambler' randomly increases or decreases your chance to hit with all attacks in combat.",
            "  Each rank in 'Gambler' increases or decreases 'Chance to hit' by 5%.",
            "",
            "- 'Gambler' randomly increases or decreases the damage you inflict with all attacks in combat.",
            "  Each rank in 'Gambler' increases or decreases 'Damage' by 5%."
        )
    }

    private fun healerDescription(): List<String> {
        return listOf(
            "- 'Healer' allows for healing other party members in combat.",
            "  It's also possible to use herbs to double the healing effect."
        )
    }

    private fun troubadourDescription(): List<String> {
        return listOf(
            "- 'Troubadour' allows the possibility to play and sing inspirationally in combat,",
            "  increasing your party's 'Chance to hit' and decreasing the enemy's 'Chance to hit'."
        )
    }

    private fun thiefDescription(): List<String> {
        return listOf(
            "- 'Thief' allows the possibility to use 'Thief' abilities in combat.",
            "",
            "- 'Thief' allows the possibility to pick locks on treasure chests."
        )
    }

    private fun warriorDescription(): List<String> {
        return listOf(
            "- 'Warrior' allows the possibility to use 'Warrior' abilities in combat.",
            "",
            "- 'Warrior' allows the possibility of scoring critical hits with weapons in combat.",
            "  Each rank in 'Warrior' increases the probability of this by 4%.",
            "  A critical hit inflicts damage equal to 175% of the normal damage.",
            "",
            "- 'Warrior' increases the power of Warrior based abilities."
        )
    }

    private fun wizardDescription(): List<String> {
        return listOf(
            "- 'Wizard' allows the possibility to learn and cast magical spells in combat.",
            "",
            "- 'Wizard' allows the possibility of scoring critical hits with offensive spells in combat.",
            "  Each rank in 'Wizard' increases the probability of this by 4%.",
            "  A critical hit inflicts damage equal to 175% of the normal damage.",
            "",
            "- 'Wizard' increases the power of buff and debuff spells."
        )
    }

    // weapon //

    private fun swordDescription(): List<String> {
        return listOf(
            "- 'Sword' allows the possibility of equipping swords.",
            "",
            "- 'Sword' increases your chance to hit with swords in combat.",
            "  Each rank in 'Sword' increases 'Chance to hit' by 5%.",
            "",
            "- Sword advantage: Spear > Sword > Axe.",
            "  Swords also have advantage or disadvantage against",
            "  certain weaponless enemies."
        )
    }

    private fun axeDescription(): List<String> {
        return listOf(
            "- 'Axe' allows the possibility of equipping axes.",
            "",
            "- 'Axe' increases your chance to hit with axes in combat.",
            "  Each rank in 'Axe' increases 'Chance to hit' by 5%.",
            "",
            "- Axe advantage: Sword > Axe > Spear.",
            "  Axes also have advantage or disadvantage against",
            "  certain weaponless enemies."
        )
    }

    private fun spearDescription(): List<String> {
        return listOf(
            "- 'Spear' allows the possibility of equipping spears.",
            "",
            "- 'Spear' increases your chance to hit with spears in combat.",
            "  Each rank in 'Spear' increases 'Chance to hit' by 5%.",
            "",
            "- Spear advantage: Axe > Spear > Sword.",
            "  Spears also have advantage or disadvantage against",
            "  certain weaponless enemies."
        )
    }

    private fun daggerDescription(): List<String> {
        return listOf(
            "- 'Dagger' allows the possibility of equipping daggers.",
            "",
            "- 'Dagger' increases your chance to hit with daggers in combat.",
            "  Each rank in 'Dagger' increases 'Chance to hit' by 5%.",
            "",
            "- Dagger advantage: Bow > Dagger > Throw.",
            "  Daggers also have advantage or disadvantage against",
            "  certain weaponless enemies."
        )
    }

    private fun throwDescription(): List<String> {
        return listOf(
            "- 'Throw' allows the possibility of equipping throwing weapons.",
            "",
            "- 'Throw' increases your chance to hit with throwing weapons in combat.",
            "  Each rank in 'Throw' increases 'Chance to hit' by 5%.",
            "",
            "- Throwing weapon advantage: Dagger > Throw > Bow.",
            "  Throwing weapons also have advantage or disadvantage against",
            "  certain weaponless enemies."
        )
    }

    private fun bowDescription(): List<String> {
        return listOf(
            "- 'Bow' allows the possibility of equipping bows.",
            "",
            "- 'Bow' increases your chance to hit with bows in combat.",
            "  Each rank in 'Bow' increases 'Chance to hit' by 5%.",
            "",
            "- Bow advantage: Throw > Bow > Dagger.",
            "  Bows also have advantage or disadvantage against",
            "  certain weaponless enemies."
        )
    }

    private fun staffDescription(): List<String> {
        return listOf(
            "- 'Staff' allows the possibility of equipping staffs.",
            "",
            "- 'Staff' increases your chance to hit with staffs in combat.",
            "  Each rank in 'Staff' increases 'Chance to hit' by 5%.",
            "",
            "- Magic advantage: Fire > Wind > Thunder > Fire."
        )
    }

    private fun shieldDescription(): List<String> {
        return listOf(
            "- 'Shield' allows the possibility of equipping shields.",
            "",
            "- In combat, a shield may block the enemy's attack with physical weapons.",
            "  Each rank in 'Shield' increases 'Defense' by 10%."
        )
    }

}
