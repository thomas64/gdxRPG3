package nl.t64.cot.components.party

import nl.t64.cot.GameTest
import nl.t64.cot.ProfileManager
import nl.t64.cot.Utils.gameData
import nl.t64.cot.components.party.inventory.*
import nl.t64.cot.components.party.skills.SkillDatabase
import nl.t64.cot.components.party.skills.SkillItemId
import nl.t64.cot.components.party.stats.StatDatabase
import nl.t64.cot.components.party.stats.StatItemId
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever


private const val GOLD = "gold"
private const val HERB = "herb"
private const val POTION = "healing_potion"
private const val BASIC_MACE = "basic_mace"
private const val BASIC_LIGHT_CHEST = "basic_light_chest"

internal class InventoryTest : GameTest() {

    private lateinit var inventory: InventoryContainer

    @BeforeEach
    private fun beforeEach() {
        gameData.onNotifyCreateProfile(ProfileManager())
        inventory = InventoryContainer(66)
        val mace = InventoryDatabase.createInventoryItem(BASIC_MACE)
        inventory.autoSetItem(mace)
        val gold = InventoryDatabase.createInventoryItem(GOLD)
        inventory.autoSetItem(gold)
        inventory.sort()
    }

    @Test
    fun whenResourceItemIsCreated_ShouldHaveVariables() {
        val gold = InventoryDatabase.createInventoryItem(GOLD)
        assertThat(gold.name).isEqualTo("Gold")
        assertThat(gold.sort).isEqualTo(90090)
        assertThat(gold.group).isEqualTo(InventoryGroup.RESOURCE)
        assertThat(gold.description).isEqualTo(
            listOf("Gold can be used to pay for goods or services."))
    }

    @Test
    fun whenPotionItemIsCreated_ShouldHaveVariables() {
        val potion = InventoryDatabase.createInventoryItem(POTION)
        assertThat(potion.name).isEqualTo("Healing Potion")
        assertThat(potion.sort).isEqualTo(80005)
        assertThat(potion.group).isEqualTo(InventoryGroup.POTION)
        assertThat(potion.description).isEqualTo(
            listOf("Restores a fifth of the drinker's lost Constitution and Stamina.",
                   "Creating a Healing Potion requires 3 Herbs",
                   "and an Alchemist rank of at least 1."))
    }

    @Test
    fun whenCopyOfItemIsCreated_ShouldBeSameItemButNotSameObject() {
        val potion = InventoryDatabase.createInventoryItem(POTION)
        val copyPotion = potion.createCopy(potion.amount)
        assertThat(potion.hasSameIdAs(copyPotion)).isTrue
        assertThat(potion).isNotSameAs(copyPotion)
    }

    @Test
    fun whenResourceItemIsCreatedWithAmount_ShouldHaveCorrectAmountWithoutSet() {
        val spice = InventoryDatabase.createInventoryItem("spice")
        val gold = InventoryDatabase.createInventoryItem(GOLD, 5)
        val herb = InventoryDatabase.createInventoryItemForShop(HERB)
        val potion = InventoryDatabase.createInventoryItemForShop(POTION)
        assertThat(spice.amount).isEqualTo(1)
        assertThat(gold.amount).isEqualTo(5)
        assertThat(herb.amount).isEqualTo(100)
        assertThat(potion.amount).isEqualTo(20)
    }

    @Test
    fun whenInventoryItemIsCreated_ShouldHaveTradeValue() {
        val basicMace = InventoryDatabase.createInventoryItem(BASIC_MACE)
        assertThat(basicMace.getBuyPriceTotal(0)).isEqualTo(15)
        assertThat(basicMace.getSellValueTotal(0)).isEqualTo(5)
        assertThat(basicMace.getBuyPriceTotal(30)).isEqualTo(11)
        assertThat(basicMace.getSellValueTotal(30)).isEqualTo(6)
        assertThat(basicMace.getBuyPriceTotal(60)).isEqualTo(6)
        assertThat(basicMace.getSellValueTotal(60)).isEqualTo(6)
        assertThat(basicMace.getBuyPriceTotal(100)).isEqualTo(1)
        assertThat(basicMace.getSellValueTotal(100)).isEqualTo(7)
        val fineMace = InventoryDatabase.createInventoryItem("fine_mace")
        assertThat(fineMace.getBuyPriceTotal(0)).isEqualTo(30)
        assertThat(fineMace.getSellValueTotal(0)).isEqualTo(10)
        val specialistMace = InventoryDatabase.createInventoryItem("specialist_mace")
        assertThat(specialistMace.getBuyPriceTotal(0)).isEqualTo(45)
        assertThat(specialistMace.getSellValueTotal(0)).isEqualTo(15)
        val masterworkMace = InventoryDatabase.createInventoryItem("masterwork_mace")
        assertThat(masterworkMace.getBuyPriceTotal(0)).isEqualTo(60)
        assertThat(masterworkMace.getSellValueTotal(0)).isEqualTo(20)
        val potion = InventoryDatabase.createInventoryItem(POTION)
        assertThat(potion.getBuyPricePiece(0)).isEqualTo(4)
        assertThat(potion.getBuyPriceTotal(0)).isEqualTo(4)
        assertThat(potion.getSellValuePiece(0)).isEqualTo(1)
        assertThat(potion.getSellValueTotal(0)).isEqualTo(1)
        potion.amount = 20
        assertThat(potion.getBuyPricePiece(0)).isEqualTo(4)
        assertThat(potion.getBuyPriceTotal(0)).isEqualTo(80)
        assertThat(potion.getSellValuePiece(0)).isEqualTo(1)
        assertThat(potion.getSellValueTotal(0)).isEqualTo(20)
    }

    @Test
    fun whenDataIsCreated_ShouldContainItems() {
        assertThat(inventory.getNumberOfFilledSlots()).isEqualTo(2)

        assertThat(inventory.contains(BASIC_MACE)).isTrue
        assertThat(inventory.getAmountOfItemAt(0)).isEqualTo(1)
        assertThat(inventory.getItemAt(0)).hasFieldOrPropertyWithValue("id", BASIC_MACE)

        assertThat(inventory.contains(GOLD)).isTrue
        assertThat(inventory.getAmountOfItemAt(inventory.getLastIndex())).isEqualTo(1)
        assertThat(inventory.getItemAt(inventory.getLastIndex())).hasFieldOrPropertyWithValue("id", GOLD)

        assertThat(inventory.getAllContent()).hasSize(2).containsKeys("basic_mace", "gold").containsValues(1, 1)
    }

    @Test
    fun whenDataIsCreated_ShouldContainItems2() {
        val map = mapOf(Pair(BASIC_MACE, 1), Pair(GOLD, 1))
        assertThat(inventory.contains(map)).isTrue
        assertThat(inventory.contains(emptyMap())).isFalse
    }

    @Test
    fun whenResourceItemIsAdded_ShouldBeAddedToStartOfInventory() {
        val herb = InventoryDatabase.createInventoryItem(HERB)
        assertThat(inventory.contains(HERB)).isFalse
        assertThat(inventory.getAmountOfItemAt(1)).isZero
        inventory.autoSetItem(herb)
        assertThat(inventory.contains(HERB)).isTrue
        assertThat(inventory.getAmountOfItemAt(1)).isEqualTo(1)
        assertThat(inventory.getItemAt(1)).isSameAs(herb)
    }

    @Test
    fun whenEquipmentItemIsAdded_ShouldBeAddedToStartOfInventory() {
        val chest = InventoryDatabase.createInventoryItem(BASIC_LIGHT_CHEST)
        assertThat(inventory.contains(BASIC_LIGHT_CHEST)).isFalse
        inventory.autoSetItem(chest)
        assertThat(inventory.contains(BASIC_LIGHT_CHEST)).isTrue
        assertThat(inventory.getAmountOfItemAt(1)).isEqualTo(1)
        assertThat(inventory.getItemAt(1)).isSameAs(chest)
    }

    @Test
    fun whenInventoryDoesNotHoldEnoughOfResourceWhenRemoving_ShouldThrowException() {
        assertThatIllegalStateException().isThrownBy { inventory.autoRemoveItem(GOLD, 2) }
        assertThatIllegalStateException().isThrownBy { inventory.autoRemoveItem(HERB, 1) }
    }

    @Test
    fun whenLargeQuantityResourceItemIsRemoved_ShouldRemoveFromMultipleSlots() {
        val gold1 = InventoryDatabase.createInventoryItem(GOLD)
        val gold2 = InventoryDatabase.createInventoryItem(GOLD)
        val gold3 = InventoryDatabase.createInventoryItem(GOLD)
        val gold4 = InventoryDatabase.createInventoryItem(GOLD)
        gold1.amount = 15
        gold2.amount = 25
        gold3.amount = 35
        gold4.amount = 45
        inventory.forceSetItemAt(1, gold1)
        inventory.forceSetItemAt(2, gold2)
        inventory.forceSetItemAt(3, gold3)
        inventory.forceSetItemAt(4, null)
        inventory.forceSetItemAt(5, gold4)
        assertThat(inventory.getTotalOfItem(GOLD)).isEqualTo(121)

        inventory.autoRemoveItem(GOLD, 46)
        assertThat(inventory.getTotalOfItem(GOLD)).isEqualTo(75)
    }

    @Test
    fun whenInventoryIsFull_ShouldAcceptSameResourceButRejectNewResource() {
        val gold = InventoryDatabase.createInventoryItem(GOLD)
        (2 until inventory.getSize()).forEach { inventory.forceSetItemAt(it, gold) }
        assertThat(inventory.hasRoomForResource(GOLD)).isTrue
        assertThat(inventory.hasRoomForResource(HERB)).isTrue
        assertThat(inventory.hasEmptySlot()).isTrue
        inventory.forceSetItemAt(1, gold)
        assertThat(inventory.hasRoomForResource(GOLD)).isTrue
        assertThat(inventory.hasRoomForResource(HERB)).isFalse
        assertThat(inventory.hasEmptySlot()).isFalse
    }

    @Test
    fun whenItemIsForceSet_ShouldOverwriteExistingItem() {
        val chest = InventoryDatabase.createInventoryItem(BASIC_LIGHT_CHEST)
        assertThat(inventory.contains(BASIC_MACE)).isTrue
        assertThat(inventory.contains(BASIC_LIGHT_CHEST)).isFalse

        inventory.forceSetItemAt(0, chest)

        assertThat(inventory.contains(BASIC_MACE)).isFalse
        assertThat(inventory.contains(BASIC_LIGHT_CHEST)).isTrue
    }

    @Test
    fun whenSameResourceItemIsAdded_ShouldIncreaseAmount() {
        val gold = InventoryDatabase.createInventoryItem(GOLD)
        assertThat(inventory.getAmountOfItemAt(inventory.getLastIndex())).isEqualTo(1)
        inventory.autoSetItem(gold)
        assertThat(inventory.getAmountOfItemAt(inventory.getLastIndex())).isEqualTo(2)
    }

    @Test
    fun whenSameEquipmentItemIsAdded_ShouldNotIncreaseAmount() {
        val basicMace = InventoryDatabase.createInventoryItem(BASIC_MACE)
        assertThat(inventory.getAmountOfItemAt(0)).isEqualTo(1)
        assertThat(inventory.getAmountOfItemAt(1)).isZero
        inventory.autoSetItem(basicMace)
        assertThat(inventory.getAmountOfItemAt(0)).isEqualTo(1)
        assertThat(inventory.getAmountOfItemAt(1)).isEqualTo(1)
    }

    @Test
    fun whenSameResourceItemOfMultipleIsAdded_ShouldIncreaseTheFirstAmount() {
        val gold1 = InventoryDatabase.createInventoryItem(GOLD)
        val gold2 = InventoryDatabase.createInventoryItem(GOLD)
        val gold3 = InventoryDatabase.createInventoryItem(GOLD)
        inventory.forceSetItemAt(0, gold1)
        inventory.forceSetItemAt(1, gold2)
        inventory.forceSetItemAt(2, gold3)
        assertThat(inventory.getAmountOfItemAt(0)).isEqualTo(1)
        assertThat(inventory.getAmountOfItemAt(1)).isEqualTo(1)
        assertThat(inventory.getAmountOfItemAt(2)).isEqualTo(1)

        val moreGold = InventoryDatabase.createInventoryItem(GOLD)
        inventory.autoSetItem(moreGold)
        assertThat(inventory.getAmountOfItemAt(0)).isEqualTo(2)
        assertThat(inventory.getAmountOfItemAt(1)).isEqualTo(1)
        assertThat(inventory.getAmountOfItemAt(2)).isEqualTo(1)
    }

    @Test
    fun whenInventoryIsIncreasedAndDecreased_ShouldIncreaseAndDecreaseAsSuch() {
        assertThat(inventory.getAmountOfItemAt(inventory.getLastIndex())).isEqualTo(1)
        inventory.incrementAmountAt(inventory.getLastIndex(), 5)
        assertThat(inventory.getAmountOfItemAt(inventory.getLastIndex())).isEqualTo(6)
        inventory.decrementAmountAt(inventory.getLastIndex(), 3)
        assertThat(inventory.getAmountOfItemAt(inventory.getLastIndex())).isEqualTo(3)
        assertThatIllegalStateException().isThrownBy { inventory.decrementAmountAt(inventory.getLastIndex(), 3) }
    }

    @Test
    fun whenSearchingForSlotWithItem_ShouldReturnIndexOrNot() {
        assertThat(inventory.findFirstSlotIndexWithItem(GOLD)).isEqualTo(65)
        assertThat(inventory.findFirstSlotIndexWithItem(HERB)).isNull()
    }

    @Test
    fun whenCheckingForAmountOfResourceIsEnough_ShouldTrueOrFalse() {
        assertThat(inventory.hasEnoughOfItem(GOLD, 2)).isFalse
        assertThat(inventory.hasEnoughOfItem(GOLD, 1)).isTrue
    }

    @Test
    fun whenCheckingSomeLeftoverMethodsInInventory_ShouldSucceed() {
        assertThat(inventory.getAllFilledSlots()).hasSize(2)
        assertThat(inventory.isEmpty()).isFalse
        inventory.clearItemAt(0)
        inventory.clearItemAt(inventory.getLastIndex())
        assertThat(inventory.isEmpty()).isTrue
        inventory.forceSetItemAt(8, InventoryDatabase.createInventoryItem(BASIC_MACE))
        assertThat(inventory.findFirstFilledSlotIndex()).isEqualTo(8)
    }

    @Test
    fun whenInventoryIsSorted_ShouldBeSortedAndMerged() {
        val gold1 = InventoryDatabase.createInventoryItem(GOLD)
        val gold2 = InventoryDatabase.createInventoryItem(GOLD)
        val gold3 = InventoryDatabase.createInventoryItem(GOLD)
        val herb1 = InventoryDatabase.createInventoryItem(HERB)
        val herb2 = InventoryDatabase.createInventoryItem(HERB)
        val herb3 = InventoryDatabase.createInventoryItem(HERB)
        herb1.amount = 80
        herb2.amount = 80
        val chest = InventoryDatabase.createInventoryItem(BASIC_LIGHT_CHEST)

        inventory.forceSetItemAt(0, gold1)
        inventory.forceSetItemAt(20, gold2)
        inventory.forceSetItemAt(65, gold3)
        inventory.forceSetItemAt(10, herb1)
        inventory.forceSetItemAt(40, herb2)
        inventory.forceSetItemAt(50, herb3)
        inventory.forceSetItemAt(36, chest)

        inventory.sort()
        inventory.sort()

        assertThat(inventory.getAmountOfItemAt(0)).isEqualTo(1)
        assertThat(inventory.getItemAt(0)).isSameAs(chest)
        assertThat(inventory.getAmountOfItemAt(inventory.getLastIndex())).isEqualTo(3)
        assertThat(inventory.getItemAt(inventory.getLastIndex())).isSameAs(gold1)
        assertThat(inventory.getAmountOfItemAt(inventory.getLastIndex() - 1)).isEqualTo(161)
        assertThat(inventory.getItemAt(inventory.getLastIndex() - 1)).isSameAs(herb1)
    }

    @Disabled
    @Test
    fun whenInventoryIsFull_ShouldNotThrowError() {
        val gold = InventoryDatabase.createInventoryItem(GOLD)
        val herb = InventoryDatabase.createInventoryItem(HERB)
        val mace = InventoryDatabase.createInventoryItem(BASIC_MACE)
        (0 until inventory.getSize()).forEach { inventory.forceSetItemAt(it, gold) }
        inventory.autoSetItem(herb)
        inventory.autoSetItem(mace)
    }

    @Test
    fun whenEmptyIndexIsIncreased_ShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy { inventory.incrementAmountAt(2, 1) }
    }

    @Test
    fun whenInventoryItemWeapon_ShouldCreateDescription() {
        val weapon = InventoryDatabase.createInventoryItem(BASIC_MACE)
        val description = DescriptionCreator(weapon, 0).createItemDescription()
        assertThat(description[0].key).isEqualTo(InventoryGroup.WEAPON)
        assertThat(description[0].value).isEqualTo("Basic Mace")
        assertThat(description[1].key).isEqualTo("(One-handed)")
        assertThat(description[1].value).isEqualTo("")
        assertThat(description[2].key).isEqualTo("Price")
        assertThat(description[2].value).isEqualTo(15)
        assertThat(description[3].key).isEqualTo("Sell value")
        assertThat(description[3].value).isEqualTo(5)
        assertThat(description[4].key).isEqualTo(InventoryMinimal.SKILL)
        assertThat(description[4].value).isEqualTo(SkillItemId.HAFTED)
        assertThat(description[5].key).isEqualTo(InventoryMinimal.MIN_STRENGTH)
        assertThat(description[5].value).isEqualTo(15)
        assertThat(description[6].key).isEqualTo(CalcAttributeId.BASE_HIT)
        assertThat(description[6].value).isEqualTo(65)
        assertThat(description[7].key).isEqualTo(CalcAttributeId.DAMAGE)
        assertThat(description[7].value).isEqualTo(20)
        assertThatExceptionOfType(IndexOutOfBoundsException::class.java).isThrownBy { description[8] }
    }

    @Test
    fun whenInventoryItemWeaponTwoHanded_ShouldCreateDescription() {
        val weapon = InventoryDatabase.createInventoryItem("basic_shortbow")
        val description = DescriptionCreator(weapon, 0).createItemDescription()
        assertThat(description[0].key).isEqualTo(InventoryGroup.WEAPON)
        assertThat(description[0].value).isEqualTo("Basic Shortbow")
        assertThat(description[1].key).isEqualTo("(Two-handed)")
        assertThat(description[1].value).isEqualTo("")
    }

    @Test
    fun whenInventoryItemShield_ShouldCreateDescription() {
        val weapon = InventoryDatabase.createInventoryItem("basic_light_shield")
        val description = DescriptionCreator(weapon, 0).createItemDescription()
        assertThat(description[0].key).isEqualTo(InventoryGroup.SHIELD)
        assertThat(description[0].value).isEqualTo("Basic Light Shield")
        assertThat(description[1].key).isEqualTo("Price")
        assertThat(description[1].value).isEqualTo(8)
        assertThat(description[2].key).isEqualTo("Sell value")
        assertThat(description[2].value).isEqualTo(2)
        assertThat(description[3].key).isEqualTo(InventoryMinimal.SKILL)
        assertThat(description[3].value).isEqualTo(SkillItemId.SHIELD)
        assertThat(description[4].key).isEqualTo(InventoryMinimal.MIN_STRENGTH)
        assertThat(description[4].value).isEqualTo(14)
        assertThat(description[5].key).isEqualTo(CalcAttributeId.PROTECTION)
        assertThat(description[5].value).isEqualTo(1)
        assertThat(description[6].key).isEqualTo(CalcAttributeId.DEFENSE)
        assertThat(description[6].value).isEqualTo(5)
        assertThat(description[7].key).isEqualTo(StatItemId.SPEED)
        assertThat(description[7].value).isEqualTo(0)
        assertThat(description[8].key).isEqualTo(SkillItemId.STEALTH)
        assertThat(description[8].value).isEqualTo(-5)
        assertThatExceptionOfType(IndexOutOfBoundsException::class.java).isThrownBy { description[9] }
    }

    @Test
    fun whenInventoryItemChest_ShouldCreateDescription() {
        val weapon = InventoryDatabase.createInventoryItem(BASIC_LIGHT_CHEST)
        val description = DescriptionCreator(weapon, 0).createItemDescription()
        assertThat(description[0].key).isEqualTo(InventoryGroup.CHEST)
        assertThat(description[0].value).isEqualTo("Basic Light Chest")
        assertThat(description[1].key).isEqualTo("Price")
        assertThat(description[1].value).isEqualTo(10)
        assertThat(description[2].key).isEqualTo("Sell value")
        assertThat(description[2].value).isEqualTo(3)
        assertThat(description[3].key).isEqualTo(InventoryMinimal.MIN_DEXTERITY)
        assertThat(description[3].value).isEqualTo(10)
        assertThat(description[4].key).isEqualTo(CalcAttributeId.PROTECTION)
        assertThat(description[4].value).isEqualTo(1)
        assertThat(description[5].key).isEqualTo(StatItemId.SPEED)
        assertThat(description[5].value).isEqualTo(0)
        assertThat(description[6].key).isEqualTo(SkillItemId.STEALTH)
        assertThat(description[6].value).isEqualTo(0)
        assertThatExceptionOfType(IndexOutOfBoundsException::class.java).isThrownBy { description[7] }
    }

    @Test
    fun whenResourceItemHerb_ShouldCreateDescription() {
        val potion = InventoryDatabase.createInventoryItem(POTION, 10)
        val description = DescriptionCreator(potion, 0).createItemDescription()
        assertThat(description[0].key).isEqualTo(InventoryGroup.POTION)
        assertThat(description[0].value).isEqualTo("Healing Potion")
        assertThat(description[1].key).isEqualTo("Price per piece")
        assertThat(description[1].value).isEqualTo(4)
        assertThat(description[2].key).isEqualTo("Total price")
        assertThat(description[2].value).isEqualTo(40)
        assertThat(description[3].key).isEqualTo("Sell value per piece")
        assertThat(description[3].value).isEqualTo(1)
        assertThat(description[4].key).isEqualTo("Total sell value")
        assertThat(description[4].value).isEqualTo(10)
        assertThatExceptionOfType(IndexOutOfBoundsException::class.java).isThrownBy { description[5] }
    }

    @Test
    fun whenAmountOfItemIsZero_ShouldThrowException() {
        val potion = InventoryDatabase.createInventoryItem(POTION, 0)
        val creator = DescriptionCreator(potion, 0)
        assertThatIllegalStateException().isThrownBy { creator.createItemDescription() }
    }

    @Test
    fun whenItemIsComparedToHero_ShouldReturnSpecificThreeStates() {
        val weapon = InventoryDatabase.createInventoryItem(BASIC_MACE)
        val heroMock = mock<HeroItem>()
        val haftedSkill = SkillDatabase.createSkillItem("hafted", 1)
        whenever(heroMock.getSkillById(SkillItemId.HAFTED)).thenReturn(haftedSkill)
        val strengthStat = StatDatabase.createStatItem("strength", 10)
        whenever(heroMock.getStatById(StatItemId.STRENGTH)).thenReturn(strengthStat)
        val description = DescriptionCreator(weapon, 0).createItemDescriptionComparingToHero(heroMock)
        assertThat(description[4].key).isEqualTo(InventoryMinimal.SKILL)
        assertThat(description[4].compare).isEqualTo(ThreeState.SAME)
        assertThat(description[5].key).isEqualTo(InventoryMinimal.MIN_STRENGTH)
        assertThat(description[5].compare).isEqualTo(ThreeState.LESS)
    }

    @Test
    fun whenWeaponIsComparedToOtherWeapon_ShouldReturnSpecificThreeStates() {
        val mace = InventoryDatabase.createInventoryItem(BASIC_MACE)
        val sword = InventoryDatabase.createInventoryItem("basic_shortsword")
        val description = DescriptionCreator(mace, 0).createItemDescriptionComparingToItem(sword)
        assertThat(description[6].key).isEqualTo(CalcAttributeId.BASE_HIT)
        assertThat(description[6].compare).isEqualTo(ThreeState.MORE)
        assertThat(description[7].key).isEqualTo(CalcAttributeId.DAMAGE)
        assertThat(description[7].compare).isEqualTo(ThreeState.MORE)
    }

    @Test
    fun whenWeaponIsComparedToOtherTypeWeapon_ShouldReturnEmptyMinimalLines() {
        val horn = InventoryDatabase.createInventoryItem("legendary_horn_of_kynon")
        val spellbook = InventoryDatabase.createInventoryItem("legendary_spellbook")
        val description = DescriptionCreator(horn, 0).createItemDescriptionComparingToItem(spellbook)
        assertThat(description[0].key).isEqualTo(InventoryGroup.ACCESSORY)
        assertThat(description[1].key).isEqualTo("Price")
        assertThat(description[2].key).isEqualTo("Sell value")
        assertThat(description[3].key).isEqualTo(InventoryMinimal.MIN_INTELLIGENCE)
        assertThat(description[4].key).isEqualTo("")
        assertThat(description[5].key).isEqualTo(SkillItemId.TROUBADOUR)
        assertThat(description[6].key).isEqualTo("")
    }

    @Test
    fun whenShieldIsComparedToOtherShield_ShouldReturnSpecificThreeStates() {
        val light = InventoryDatabase.createInventoryItem("basic_light_shield")
        val medium = InventoryDatabase.createInventoryItem("basic_medium_shield")
        val description1 = DescriptionCreator(light, 0).createItemDescriptionComparingToItem(medium)
        assertThat(description1[7].key).isEqualTo(StatItemId.SPEED)
        assertThat(description1[7].compare).isEqualTo(ThreeState.MORE)
        assertThat(description1[8].key).isEqualTo(SkillItemId.STEALTH)
        assertThat(description1[8].compare).isEqualTo(ThreeState.MORE)
        val description2 = DescriptionCreator(medium, 0).createItemDescriptionComparingToItem(light)
        assertThat(description2[7].key).isEqualTo(StatItemId.SPEED)
        assertThat(description2[7].compare).isEqualTo(ThreeState.LESS)
        assertThat(description2[8].key).isEqualTo(SkillItemId.STEALTH)
        assertThat(description2[8].compare).isEqualTo(ThreeState.LESS)
    }

}
