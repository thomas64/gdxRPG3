package nl.t64.cot.components.party

import nl.t64.cot.GameTest
import nl.t64.cot.components.party.inventory.InventoryDatabase
import nl.t64.cot.components.party.inventory.InventoryGroup
import nl.t64.cot.components.party.inventory.InventoryItem
import nl.t64.cot.components.party.inventory.InventoryMinimal
import nl.t64.cot.components.party.skills.SkillDatabase
import nl.t64.cot.components.party.skills.SkillItemId
import nl.t64.cot.components.party.spells.ResourceType
import nl.t64.cot.components.party.spells.SchoolType
import nl.t64.cot.components.party.stats.StatItemId
import nl.t64.cot.constants.Constant
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.groups.Tuple
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever


internal class HeroTest : GameTest() {

    private lateinit var heroes: HeroContainer
    private lateinit var party: PartyContainer

    @BeforeEach
    private fun setup() {
        heroes = HeroContainer()
        party = PartyContainer()
        val hero = heroes.getCertainHero(Constant.PLAYER_ID)
        heroes.removeHero(Constant.PLAYER_ID)
        party.addHero(hero)
    }

    @Test
    fun whenImpossibleItemIsForceSet_ShouldOverwriteExistingItem() {
        val mozes = party.getCertainHero("mozes")
        assertThat(mozes.getInventoryItem(InventoryGroup.WEAPON))
            .hasFieldOrPropertyWithValue("id", "basic_shortsword")

        val newWeapon = InventoryDatabase.createInventoryItem("masterwork_lance")
        mozes.forceSetInventoryItemFor(InventoryGroup.WEAPON, newWeapon)
        assertThat(mozes.getInventoryItem(InventoryGroup.WEAPON)).isSameAs(newWeapon)
        mozes.clearInventoryItemFor(InventoryGroup.WEAPON)
        assertThat(mozes.getInventoryItem(InventoryGroup.WEAPON)).isNull()
    }

    @Test
    fun whenImpossibleItemIsChecked_ShouldGetMessage() {
        val mozes = party.getCertainHero("mozes")
        val ryiah = heroes.getCertainHero("ryiah")
        val legendaryStaff = InventoryDatabase.createInventoryItem("legendary_staff")
        val masterworkLance = InventoryDatabase.createInventoryItem("masterwork_lance")
        val basicDagger = InventoryDatabase.createInventoryItem("basic_dagger")
        val chest = InventoryDatabase.createInventoryItem("basic_light_chest")
        val bow = InventoryDatabase.createInventoryItem("basic_shortbow")
        val shield = InventoryDatabase.createInventoryItem("basic_light_shield")

        var message: String?

        message = mozes.createMessageIfNotAbleToEquip(legendaryStaff)
        assertThat(message).contains("Mozes needs the Pole skill\nto equip that Legendary Staff.")

        message = ryiah.createMessageIfNotAbleToEquip(legendaryStaff)
        assertThat(message).contains("Ryiah needs 30 Intelligence\nto equip that Legendary Staff.")

        message = ryiah.createMessageIfNotAbleToEquip(masterworkLance)
        assertThat(message).contains("Ryiah needs 20 Strength\nto equip that Masterwork Lance.")

        message = mozes.createMessageIfNotAbleToEquip(basicDagger)
        assertThat(message).isNull()

        message = mozes.createMessageIfNotAbleToEquip(chest)
        assertThat(message).isNull()

        message = mozes.createMessageIfNotAbleToEquip(bow)
        assertThat(message).contains("Cannot equip the Basic Shortbow.\nFirst unequip the Basic Light Shield.")

        mozes.clearInventoryItemFor(InventoryGroup.SHIELD)
        mozes.forceSetInventoryItemFor(InventoryGroup.WEAPON, bow)
        message = mozes.createMessageIfNotAbleToEquip(shield)
        assertThat(message).contains("Cannot equip the Basic Light Shield.\nFirst unequip the Basic Shortbow.")
    }

    @Test
    fun whenHeroesAreCreated_ShouldHaveRightStats() {
        val mozes = party.getCertainHero("mozes")
        val luana = heroes.getCertainHero("luana")
        val valter = heroes.getCertainHero("valter")
        val luthais = heroes.getCertainHero("luthais")
        val iellwen = heroes.getCertainHero("iellwen")

        val trainerStealth = SkillDatabase.createSkillItem("STEALTH", 10)
        val trainerWizard = SkillDatabase.createSkillItem("WIZARD", 10)


        assertThat(party.getHero(0)).isEqualTo(mozes)

        assertThat(mozes.getSkillValueOf(InventoryGroup.SHIELD, SkillItemId.STEALTH)).isEqualTo(-5)
        assertThat(mozes.getStatValueOf(InventoryGroup.SHIELD, StatItemId.SPEED)).isEqualTo(0)
        assertThat(mozes.id).isEqualTo("mozes")
        assertThat(mozes.name).isEqualTo("Mozes")
        assertThat(mozes.school).isEqualTo(SchoolType.UNKNOWN)
        assertThat(mozes.hasSameIdAs(mozes)).isTrue
        assertThat(mozes.isPlayer).isTrue
        assertThat(mozes.xpDeltaBetweenLevels).isEqualTo(20)
        assertThat(mozes.getAllStats()).extracting("id")
            .containsExactly(StatItemId.INTELLIGENCE,
                             StatItemId.WILLPOWER,
                             StatItemId.STRENGTH,
                             StatItemId.DEXTERITY,
                             StatItemId.CONSTITUTION,
                             StatItemId.STAMINA)
        assertThat(mozes.getStatById(StatItemId.INTELLIGENCE).getXpCostForNextRank()).isEqualTo(43)
        assertThat(mozes.getSkillById(SkillItemId.STEALTH).getXpCostForNextLevel(trainerStealth, 0)).isEqualTo(16)
        assertThat(mozes.getSkillById(SkillItemId.STEALTH).getGoldCostForNextLevel(trainerStealth)).isEqualTo(8)

        assertThat(mozes.getExtraStatForVisualOf(mozes.getStatById(StatItemId.SPEED))).isEqualTo(-1)
        assertThat(iellwen.getExtraSkillForVisualOf(iellwen.getSkillById(SkillItemId.STEALTH))).isEqualTo(-1)

        assertThat(mozes.getInventoryItem(InventoryGroup.WEAPON))
            .hasFieldOrPropertyWithValue("id", "basic_shortsword")
        assertThat(mozes.getInventoryItem(InventoryGroup.SHIELD))
            .hasFieldOrPropertyWithValue("id", "basic_light_shield")
        assertThat(mozes.getInventoryItem(InventoryGroup.HELMET)).isNull()
        assertThat(mozes.getInventoryItem(InventoryGroup.CHEST))
            .hasFieldOrPropertyWithValue("id", "basic_medium_chest")

        assertThat(luana.getInventoryItem(InventoryGroup.WEAPON))
            .hasFieldOrPropertyWithValue("id", "basic_dart")
        assertThat(luana.getInventoryItem(InventoryGroup.SHIELD)).isNull()
        assertThat(luana.getInventoryItem(InventoryGroup.HELMET)).isNull()
        assertThat(luana.getInventoryItem(InventoryGroup.CHEST))
            .hasFieldOrPropertyWithValue("id", "basic_light_chest")

        assertThat(luana.getAllSkillsAboveZero()).extracting("id")
            .containsExactly(SkillItemId.MECHANIC,
                             SkillItemId.STEALTH,
                             SkillItemId.THIEF,
                             SkillItemId.SWORD,
                             SkillItemId.THROWN)

        assertThat(valter.getAllSpells()).extracting("id").containsExactly("dragon_flames")

        assertThat(luthais.getAllSpells()).extracting("id", "rank").contains(Tuple.tuple("fireball", 8))
        val scholar = luthais.getCalculatedTotalSkillOf(SkillItemId.SCHOLAR)
        assertThat(luthais.getSkillById(SkillItemId.WIZARD).getXpCostForNextLevel(trainerWizard, scholar)).isZero

        assertThat(StatItemId.INTELLIGENCE.title).isEqualTo("Intelligence")
        assertThat(SkillItemId.ALCHEMIST.title).isEqualTo("Alchemist")
        assertThat(CalcAttributeId.ACTION_POINTS.title).isEqualTo("Weight")
        assertThat(SchoolType.NONE.title).isEqualTo("No")
        assertThat(ResourceType.GOLD.title).isEqualTo("Gold")
        assertThat(InventoryGroup.WEAPON.title).isEqualTo("Weapon")
        assertThat(InventoryMinimal.SKILL.title).isEqualTo("Skill")
    }

    @Test
    fun whenHeroIsCreated_ShouldHaveRightHpStats() {
        val mozes = party.getCertainHero("mozes")
        val actual = mozes.getAllHpStats()
        val expected = mapOf(Pair("lvlRank", 1),
                             Pair("lvlVari", 1),
                             Pair("staRank", 30),
                             Pair("staVari", 30),
                             Pair("conRank", 15),
                             Pair("conVari", 15),
                             Pair("conBon", 0))
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun whenItemMakesStatLowerThanZero_ShouldReturnOne() {
        val jaspar = heroes.getCertainHero("jaspar")
        val itemMock = mock<InventoryItem>()
        whenever(itemMock.getAttributeOfStatItemId(StatItemId.DEXTERITY)).thenReturn(-200)
        jaspar.forceSetInventoryItemFor(InventoryGroup.SHIELD, itemMock)

        assertThat(jaspar.getCalculatedTotalStatOf(StatItemId.DEXTERITY)).isEqualTo(1)
        assertThat(jaspar.getExtraStatForVisualOf(jaspar.getStatById(StatItemId.DEXTERITY))).isEqualTo(-14)
    }

    @Test
    fun whenItemMakesSkillLowerThanZero_ShouldReturnZero() {
        val faeron = heroes.getCertainHero("faeron")
        val itemMock = mock<InventoryItem>()
        whenever(itemMock.getAttributeOfSkillItemId(SkillItemId.STEALTH)).thenReturn(-200)
        faeron.forceSetInventoryItemFor(InventoryGroup.SHIELD, itemMock)

        assertThat(faeron.getCalculatedTotalSkillOf(SkillItemId.STEALTH)).isZero
        assertThat(faeron.getExtraSkillForVisualOf(faeron.getSkillById(SkillItemId.STEALTH))).isEqualTo(-10)
    }


    @Test
    fun whenHeroGainsXp_ShouldGainXp() {
        val mozes = party.getCertainHero("mozes")
        val sb = StringBuilder()
        assertThat(mozes.totalXp).isEqualTo(5)
        assertThat(mozes.xpToInvest).isZero
        assertThat(mozes.getLevel()).isEqualTo(1)
        assertThat(sb).isBlank
        mozes.gainXp(10, sb)
        assertThat(mozes.totalXp).isEqualTo(15)
        assertThat(mozes.xpToInvest).isEqualTo(10)
        assertThat(mozes.getLevel()).isEqualTo(1)
        assertThat(sb.toString().trim()).isBlank
    }

    @Test
    fun whenHeroGainsEnoughXp_ShouldGainLevel() {
        val mozes = party.getCertainHero("mozes")
        val sb = StringBuilder()
        assertThat(mozes.totalXp).isEqualTo(5)
        assertThat(mozes.xpToInvest).isZero
        assertThat(mozes.getLevel()).isEqualTo(1)
        assertThat(sb).isBlank
        mozes.gainXp(20, sb)
        assertThat(mozes.totalXp).isEqualTo(25)
        assertThat(mozes.xpToInvest).isEqualTo(20)
        assertThat(mozes.getLevel()).isEqualTo(2)
        assertThat(sb.toString().trim()).isEqualTo("Mozes gained a level!")
    }

    @Test
    fun `When armor of the same type is complete, should give a protection bonus`() {
        val mozes = party.getCertainHero("mozes")
        val helmet = InventoryDatabase.createInventoryItem("basic_light_helmet")
        val shoulders = InventoryDatabase.createInventoryItem("basic_light_shoulders")
        val chest = InventoryDatabase.createInventoryItem("basic_light_chest")
        val cloak = InventoryDatabase.createInventoryItem("basic_light_cloak")
        val bracers = InventoryDatabase.createInventoryItem("basic_light_bracers")
        val gloves = InventoryDatabase.createInventoryItem("basic_light_gloves")
        val belt = InventoryDatabase.createInventoryItem("basic_light_belt")
        val pants = InventoryDatabase.createInventoryItem("basic_light_pants")
        val boots = InventoryDatabase.createInventoryItem("basic_light_boots")

        mozes.clearInventory()

        assertThat(mozes.getSumOfEquipmentOfCalc(CalcAttributeId.PROTECTION)).isEqualTo(0)
        assertThat(mozes.getPossibleExtraProtection()).isEqualTo(0)

        mozes.forceSetInventoryItemFor(InventoryGroup.HELMET, helmet)
        mozes.forceSetInventoryItemFor(InventoryGroup.SHOULDERS, shoulders)
        mozes.forceSetInventoryItemFor(InventoryGroup.CHEST, chest)
        mozes.forceSetInventoryItemFor(InventoryGroup.CLOAK, cloak)
        mozes.forceSetInventoryItemFor(InventoryGroup.BRACERS, bracers)
        mozes.forceSetInventoryItemFor(InventoryGroup.GLOVES, gloves)
        mozes.forceSetInventoryItemFor(InventoryGroup.BELT, belt)
        mozes.forceSetInventoryItemFor(InventoryGroup.PANTS, pants)

        assertThat(mozes.getSumOfEquipmentOfCalc(CalcAttributeId.PROTECTION)).isEqualTo(6)
        assertThat(mozes.getPossibleExtraProtection()).isEqualTo(0)

        mozes.forceSetInventoryItemFor(InventoryGroup.BOOTS, boots)

        assertThat(mozes.getSumOfEquipmentOfCalc(CalcAttributeId.PROTECTION)).isEqualTo(7)
        assertThat(mozes.getPossibleExtraProtection()).isEqualTo(1)
    }

    @Test
    fun `When enhancer item is used to equip otherwise impossible item, should not be able to dequip enhancer item`() {
        val mozes = party.getCertainHero("mozes")
        val sword = InventoryDatabase.createInventoryItem("basic_longsword")
        val strengthEnhancer = InventoryDatabase.createInventoryItem("epic_gauntlets_of_might")

        assertThat(mozes.getStatById(StatItemId.STRENGTH).rank).isEqualTo(15)
        assertThat(mozes.getCalculatedTotalStatOf(StatItemId.STRENGTH)).isEqualTo(15)
        assertThat(sword.getMinimalAttributeOfStatItemId(StatItemId.STRENGTH)).isEqualTo(17)

        mozes.forceSetInventoryItemFor(InventoryGroup.GLOVES, strengthEnhancer)

        assertThat(mozes.getStatById(StatItemId.STRENGTH).rank).isEqualTo(15)
        assertThat(mozes.getCalculatedTotalStatOf(StatItemId.STRENGTH)).isEqualTo(18)
        assertThat(sword.getMinimalAttributeOfStatItemId(StatItemId.STRENGTH)).isEqualTo(17)

        assertThat(mozes.createMessageIfNotAbleToDequip(strengthEnhancer)).isNull()

        mozes.forceSetInventoryItemFor(InventoryGroup.WEAPON, sword)

        assertThat(mozes.createMessageIfNotAbleToDequip(strengthEnhancer)).isEqualToIgnoringWhitespace("""
            Cannot unequip the Gauntlets of Might.
            The Basic Longsword depends on it.""")
    }

}
