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
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.groups.Tuple
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever


internal class HeroTest : GameTest() {

    @Test
    fun whenImpossibleItemIsForceSet_ShouldOverwriteExistingItem() {
        val mozes = party.getCertainHero("mozes")
        assertThat(mozes.getInventoryItem(InventoryGroup.WEAPON))
            .hasFieldOrPropertyWithValue("id", "basic_dagger")

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
        val galen = heroes.getCertainHero("galen")
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

        message = galen.createMessageIfNotAbleToEquip(bow)
        assertThat(message).contains("Cannot equip the Basic Shortbow.\nFirst unequip the Basic Medium Shield.")

        galen.clearInventoryItemFor(InventoryGroup.SHIELD)
        galen.forceSetInventoryItemFor(InventoryGroup.WEAPON, bow)
        message = galen.createMessageIfNotAbleToEquip(shield)
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

        assertThat(mozes.id).isEqualTo("mozes")
        assertThat(mozes.name).isEqualTo("Mozes")
        assertThat(mozes.school).isEqualTo(SchoolType.UNKNOWN)
        assertThat(mozes.hasSameIdAs(mozes)).isTrue
        assertThat(mozes.hasSameIdAs(luana)).isFalse
        assertThat(mozes.isPlayer).isTrue
        assertThat(mozes.xpDeltaBetweenLevels).isEqualTo(20)
        assertThat(mozes.getAllStats()).extracting("id")
            .containsExactly(StatItemId.INTELLIGENCE,
                             StatItemId.WILLPOWER,
                             StatItemId.STRENGTH,
                             StatItemId.DEXTERITY,
                             StatItemId.CONSTITUTION,
                             StatItemId.STAMINA,
                             StatItemId.SPEED)
        assertThat(mozes.getStatById(StatItemId.INTELLIGENCE).getXpCostForNextRank()).isEqualTo(15)
        assertThat(mozes.getSkillById(SkillItemId.STEALTH).getXpCostForNextRank(trainerStealth, 0)).isEqualTo(24)
        assertThat(mozes.getSkillById(SkillItemId.STEALTH).getGoldCostForNextRank(trainerStealth)).isEqualTo(8)

        assertThat(iellwen.getExtraSkillForVisualOf(iellwen.getSkillById(SkillItemId.STEALTH))).isEqualTo(-3)

        assertThat(mozes.getInventoryItem(InventoryGroup.WEAPON))
            .hasFieldOrPropertyWithValue("id", "basic_dagger")
        assertThat(mozes.getInventoryItem(InventoryGroup.SHIELD)).isNull()
        assertThat(mozes.getInventoryItem(InventoryGroup.HELMET)).isNull()
        assertThat(mozes.getInventoryItem(InventoryGroup.CHEST))
            .hasFieldOrPropertyWithValue("id", "basic_light_chest")
        assertThat(mozes.getInventoryItem(InventoryGroup.PANTS))
            .hasFieldOrPropertyWithValue("id", "basic_light_pants")
        assertThat(mozes.getInventoryItem(InventoryGroup.BOOTS))
            .hasFieldOrPropertyWithValue("id", "basic_light_boots")

        assertThat(mozes.getAllSkillsAboveZero()).extracting("id")
            .containsExactly(SkillItemId.STEALTH,
                             SkillItemId.TROUBADOUR,
                             SkillItemId.WARRIOR,
                             SkillItemId.WIZARD,
                             SkillItemId.HAFTED,
                             SkillItemId.MISSILE,
                             SkillItemId.SHIELD,
                             SkillItemId.SWORD)

        assertThat(luana.getInventoryItem(InventoryGroup.WEAPON))
            .hasFieldOrPropertyWithValue("id", "basic_dart")
        assertThat(luana.getInventoryItem(InventoryGroup.SHIELD)).isNull()
        assertThat(luana.getInventoryItem(InventoryGroup.HELMET)).isNull()
        assertThat(luana.getInventoryItem(InventoryGroup.CHEST))
            .hasFieldOrPropertyWithValue("id", "basic_light_chest")
        assertThat(mozes.getInventoryItem(InventoryGroup.PANTS))
            .hasFieldOrPropertyWithValue("id", "basic_light_pants")
        assertThat(mozes.getInventoryItem(InventoryGroup.BOOTS))
            .hasFieldOrPropertyWithValue("id", "basic_light_boots")

        assertThat(luana.getAllSkillsAboveZero()).extracting("id")
            .containsExactly(SkillItemId.MECHANIC,
                             SkillItemId.STEALTH,
                             SkillItemId.THIEF,
                             SkillItemId.SWORD,
                             SkillItemId.THROWN)

        assertThat(valter.getAllSpells()).extracting("id").containsExactly("dragon_flames")

        assertThat(luthais.getAllSpells()).extracting("id", "rank").contains(Tuple.tuple("fireball", 8))
        val scholar = luthais.getCalculatedTotalSkillOf(SkillItemId.SCHOLAR)
        assertThat(luthais.getSkillById(SkillItemId.WIZARD).getXpCostForNextRank(trainerWizard, scholar)).isZero

        assertThat(StatItemId.INTELLIGENCE.title).isEqualTo("Intelligence")
        assertThat(SkillItemId.ALCHEMIST.title).isEqualTo("Alchemist")
        assertThat(CalcAttributeId.ACTION_POINTS.title).isEqualTo("Action Points")
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
    fun `When hero gains enough xp, should gain level which also restores hp`() {
        val mozes = party.getCertainHero("mozes")
        mozes.takeDamage(45)
        val sb = StringBuilder()
        assertThat(mozes.totalXp).isEqualTo(5)
        assertThat(mozes.xpToInvest).isZero
        assertThat(mozes.getLevel()).isEqualTo(1)
        assertThat(mozes.getCurrentHp()).isEqualTo(1)
        assertThat(sb).isBlank
        mozes.gainXp(20, sb)
        assertThat(mozes.totalXp).isEqualTo(25)
        assertThat(mozes.xpToInvest).isEqualTo(20)
        assertThat(mozes.getLevel()).isEqualTo(2)
        assertThat(mozes.getCurrentHp()).isEqualTo(47)
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
        val sword = InventoryDatabase.createInventoryItem("basic_shortsword")
        val strengthEnhancer = InventoryDatabase.createInventoryItem("epic_gauntlets_of_might")

        assertThat(mozes.getStatById(StatItemId.STRENGTH).rank).isEqualTo(10)
        assertThat(mozes.getCalculatedTotalStatOf(StatItemId.STRENGTH)).isEqualTo(10)
        assertThat(sword.getMinimalAttributeOfStatItemId(StatItemId.STRENGTH)).isEqualTo(12)

        mozes.forceSetInventoryItemFor(InventoryGroup.GLOVES, strengthEnhancer)

        assertThat(mozes.getStatById(StatItemId.STRENGTH).rank).isEqualTo(10)
        assertThat(mozes.getCalculatedTotalStatOf(StatItemId.STRENGTH)).isEqualTo(13)
        assertThat(sword.getMinimalAttributeOfStatItemId(StatItemId.STRENGTH)).isEqualTo(12)

        assertThat(mozes.createMessageIfNotAbleToDequip(strengthEnhancer)).isNull()

        mozes.forceSetInventoryItemFor(InventoryGroup.WEAPON, sword)

        assertThat(mozes.createMessageIfNotAbleToDequip(strengthEnhancer))
            .isEqualToIgnoringWhitespace("""
                Cannot unequip the Gauntlets of Might.
                The Basic Shortsword depends on it.""")
    }

    @Test
    fun `When hero takes damage, should lose hp from right stats and when zero die`() {
        val mozes = party.getCertainHero("mozes")

        assertThat(mozes.getCurrentHp()).isEqualTo(46)
        assertThat(mozes.getAllHpStats()["lvlRank"]).isEqualTo(1)
        assertThat(mozes.getAllHpStats()["staRank"]).isEqualTo(30)
        assertThat(mozes.getAllHpStats()["conRank"]).isEqualTo(15)
        assertThat(mozes.getAllHpStats()["lvlVari"]).isEqualTo(1)
        assertThat(mozes.getAllHpStats()["staVari"]).isEqualTo(30)
        assertThat(mozes.getAllHpStats()["conVari"]).isEqualTo(15)
        assertThat(mozes.isAlive).isTrue

        mozes.takeDamage(10)

        assertThat(mozes.getCurrentHp()).isEqualTo(36)
        assertThat(mozes.getAllHpStats()["lvlRank"]).isEqualTo(1)
        assertThat(mozes.getAllHpStats()["staRank"]).isEqualTo(30)
        assertThat(mozes.getAllHpStats()["conRank"]).isEqualTo(15)
        assertThat(mozes.getAllHpStats()["lvlVari"]).isEqualTo(0)
        assertThat(mozes.getAllHpStats()["staVari"]).isEqualTo(21)
        assertThat(mozes.getAllHpStats()["conVari"]).isEqualTo(15)
        assertThat(mozes.isAlive).isTrue

        mozes.takeDamage(25)

        assertThat(mozes.getCurrentHp()).isEqualTo(11)
        assertThat(mozes.getAllHpStats()["lvlRank"]).isEqualTo(1)
        assertThat(mozes.getAllHpStats()["staRank"]).isEqualTo(30)
        assertThat(mozes.getAllHpStats()["conRank"]).isEqualTo(15)
        assertThat(mozes.getAllHpStats()["lvlVari"]).isEqualTo(0)
        assertThat(mozes.getAllHpStats()["staVari"]).isEqualTo(0)
        assertThat(mozes.getAllHpStats()["conVari"]).isEqualTo(11)
        assertThat(mozes.isAlive).isTrue

        mozes.takeDamage(15)

        assertThat(mozes.getCurrentHp()).isEqualTo(0)
        assertThat(mozes.getAllHpStats()["lvlRank"]).isEqualTo(1)
        assertThat(mozes.getAllHpStats()["staRank"]).isEqualTo(30)
        assertThat(mozes.getAllHpStats()["conRank"]).isEqualTo(15)
        assertThat(mozes.getAllHpStats()["lvlVari"]).isEqualTo(0)
        assertThat(mozes.getAllHpStats()["staVari"]).isEqualTo(0)
        assertThat(mozes.getAllHpStats()["conVari"]).isEqualTo(0)
        assertThat(mozes.isAlive).isFalse
    }

    @Test
    fun `When hero recovers part hp, should restore from right stats but not make alive`() {
        val mozes = party.getCertainHero("mozes")
        mozes.takeDamage(45)

        assertThat(mozes.getCurrentHp()).isEqualTo(1)
        assertThat(mozes.getAllHpStats()["lvlRank"]).isEqualTo(1)
        assertThat(mozes.getAllHpStats()["staRank"]).isEqualTo(30)
        assertThat(mozes.getAllHpStats()["conRank"]).isEqualTo(15)
        assertThat(mozes.getAllHpStats()["lvlVari"]).isEqualTo(0)
        assertThat(mozes.getAllHpStats()["staVari"]).isEqualTo(0)
        assertThat(mozes.getAllHpStats()["conVari"]).isEqualTo(1)

        mozes.recoverPartHp(20)

        assertThat(mozes.getCurrentHp()).isEqualTo(21)
        assertThat(mozes.getAllHpStats()["lvlRank"]).isEqualTo(1)
        assertThat(mozes.getAllHpStats()["staRank"]).isEqualTo(30)
        assertThat(mozes.getAllHpStats()["conRank"]).isEqualTo(15)
        assertThat(mozes.getAllHpStats()["lvlVari"]).isEqualTo(0)
        assertThat(mozes.getAllHpStats()["staVari"]).isEqualTo(6)
        assertThat(mozes.getAllHpStats()["conVari"]).isEqualTo(15)

        mozes.recoverPartHp(23)

        assertThat(mozes.getCurrentHp()).isEqualTo(44)
        assertThat(mozes.getAllHpStats()["lvlRank"]).isEqualTo(1)
        assertThat(mozes.getAllHpStats()["staRank"]).isEqualTo(30)
        assertThat(mozes.getAllHpStats()["conRank"]).isEqualTo(15)
        assertThat(mozes.getAllHpStats()["lvlVari"]).isEqualTo(0)
        assertThat(mozes.getAllHpStats()["staVari"]).isEqualTo(29)
        assertThat(mozes.getAllHpStats()["conVari"]).isEqualTo(15)

        mozes.recoverPartHp(500)

        assertThat(mozes.getCurrentHp()).isEqualTo(46)
        assertThat(mozes.getAllHpStats()["lvlRank"]).isEqualTo(1)
        assertThat(mozes.getAllHpStats()["staRank"]).isEqualTo(30)
        assertThat(mozes.getAllHpStats()["conRank"]).isEqualTo(15)
        assertThat(mozes.getAllHpStats()["lvlVari"]).isEqualTo(1)
        assertThat(mozes.getAllHpStats()["staVari"]).isEqualTo(30)
        assertThat(mozes.getAllHpStats()["conVari"]).isEqualTo(15)

        assertThat(mozes.isAlive).isTrue
        mozes.takeDamage(50)
        assertThat(mozes.isAlive).isFalse
        mozes.recoverPartHp(10)
        assertThat(mozes.getCurrentHp()).isEqualTo(10)
        assertThat(mozes.isAlive).isFalse
    }

    @Test
    fun `When hero recovers full hp, should restore full hp but not make alive`() {
        val mozes = party.getCertainHero("mozes")
        val luana = heroes.getCertainHero("luana")
        mozes.takeDamage(45)
        luana.takeDamage(100)

        assertThat(mozes.getCurrentHp()).isEqualTo(1)
        assertThat(mozes.isAlive).isTrue
        assertThat(luana.getCurrentHp()).isEqualTo(0)
        assertThat(luana.isAlive).isFalse

        mozes.recoverFullHp()
        luana.recoverFullHp()

        assertThat(mozes.getCurrentHp()).isEqualTo(46)
        assertThat(mozes.isAlive).isTrue
        assertThat(luana.getCurrentHp()).isEqualTo(32)
        assertThat(luana.isAlive).isFalse
    }

    @Test
    fun `When hero recovers full stamina, should restore full stamina but not make alive`() {
        val mozes = party.getCertainHero("mozes")
        val luana = heroes.getCertainHero("luana")
        mozes.takeDamage(45)
        luana.takeDamage(100)

        assertThat(mozes.getCurrentHp()).isEqualTo(1)
        assertThat(mozes.isAlive).isTrue
        assertThat(luana.getCurrentHp()).isEqualTo(0)
        assertThat(luana.isAlive).isFalse

        mozes.recoverFullStamina()
        luana.recoverFullStamina()

        assertThat(mozes.getCurrentHp()).isEqualTo(31)
        assertThat(mozes.isAlive).isTrue
        assertThat(luana.getCurrentHp()).isEqualTo(20)
        assertThat(luana.isAlive).isFalse
    }

    @Test
    fun `When hero has enough XP for something, should return true or false`() {
        val mozes = party.getCertainHero("mozes")
        assertThat(mozes.hasEnoughXpFor(1)).isFalse
        mozes.gainXp(1, StringBuilder())
        assertThat(mozes.hasEnoughXpFor(1)).isTrue
    }

}
