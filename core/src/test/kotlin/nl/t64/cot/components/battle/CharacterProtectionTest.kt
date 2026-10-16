package nl.t64.cot.components.battle

import nl.t64.cot.components.party.inventory.EquipContainer
import nl.t64.cot.components.party.inventory.InventoryGroup
import nl.t64.cot.components.party.inventory.InventoryItem
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


/**
 * 'Armor' is the sum of the armor points of all the equipment.
 * That sum goes through the curve of [ArmorReductionCalculator] and becomes 'Protection' in %.
 * The protection of some equipment, potions and spells is already in %,
 * so it is added after the curve, up to the max protection.
 */
internal class CharacterProtectionTest {

    @Test
    fun `when no armor and no bonus, should have no protection`() {
        val character: Character = createCharacter()

        assertThat(character.getArmorOfEquipment()).isEqualTo(0)
        assertThat(character.getCalculatedTotalProtection()).isEqualTo(0)
    }

    @Test
    fun `when wearing armor, should convert armor to protection through the curve`() {
        val character: Character = createCharacter(InventoryGroup.CHEST to 28)

        assertThat(character.getArmorOfEquipment()).isEqualTo(28)
        assertThat(character.getProtectionFromArmor()).isEqualTo(30)
        assertThat(character.getCalculatedTotalProtection()).isEqualTo(30)
    }

    @Test
    fun `when wearing multiple pieces, should sum armor before the curve`() {
        val character: Character = createCharacter(InventoryGroup.CHEST to 21,
                                                   InventoryGroup.HELMET to 7)

        // 21 armor alone gives 27% and 7 armor alone gives 20%, but together they are 28 armor, so 30%.
        assertThat(character.getArmorOfEquipment()).isEqualTo(28)
        assertThat(character.getCalculatedTotalProtection()).isEqualTo(30)
    }

    @Test
    fun `when having bonus without armor, should add the bonus as protection`() {
        val character: Character = createCharacter()
        character.bonus.protectionFromPotion = 15

        assertThat(character.getProtectionFromArmor()).isEqualTo(0)
        assertThat(character.getCalculatedTotalProtection()).isEqualTo(15)
    }

    @Test
    fun `when having bonus of potion and spell, should add both to the protection from armor`() {
        val character: Character = createCharacter(InventoryGroup.CHEST to 28)
        character.bonus.protectionFromPotion = 10
        character.bonus.protectionFromSpell = 8

        assertThat(character.getProtectionFromArmor()).isEqualTo(30)
        assertThat(character.getCalculatedTotalProtection()).isEqualTo(48)
    }

    @Test
    fun `when wearing any armor, should gain the same protection from the same bonus`() {
        val noArmor: Character = createCharacter()
        val lightArmor: Character = createCharacter(InventoryGroup.CHEST to 28)
        val mediumArmor: Character = createCharacter(InventoryGroup.CHEST to 72)
        listOf(noArmor, lightArmor, mediumArmor).forEach { it.bonus.protectionFromPotion = 15 }

        assertThat(noArmor.getCalculatedTotalProtection()).isEqualTo(15)     // 0  -> 15%
        assertThat(lightArmor.getCalculatedTotalProtection()).isEqualTo(45)  // 30 -> 45%
        assertThat(mediumArmor.getCalculatedTotalProtection()).isEqualTo(70) // 55 -> 70%
    }

    @Test
    fun `when protection from armor plus bonus is very high, should not pass max protection`() {
        val character: Character = createCharacter(InventoryGroup.CHEST to 116)
        character.bonus.protectionFromSpell = 20

        assertThat(character.getProtectionFromArmor()).isEqualTo(80)
        assertThat(character.getCalculatedTotalProtection()).isEqualTo(MAX_PROTECTION_PERCENT)
    }

    @Test
    fun `when wearing equipment with protection, should add it as protection without the curve`() {
        val character: Character = createCharacter()
        character.wearProtectionItem(InventoryGroup.RING, 5)

        assertThat(character.getArmorOfEquipment()).isEqualTo(0)
        assertThat(character.getProtectionFromArmor()).isEqualTo(0)
        assertThat(character.getCalculatedTotalProtection()).isEqualTo(5)
    }

    @Test
    fun `when wearing armor and equipment with protection, should add both`() {
        val character: Character = createCharacter(InventoryGroup.CHEST to 28)
        character.wearProtectionItem(InventoryGroup.RING, 5)
        character.wearProtectionItem(InventoryGroup.NECKLACE, 5)

        assertThat(character.getArmorOfEquipment()).isEqualTo(28)
        assertThat(character.getProtectionFromArmor()).isEqualTo(30)
        assertThat(character.getCalculatedTotalProtection()).isEqualTo(40)
    }

    @Test
    fun `when armor, equipment with protection and bonus are very high, should not pass max protection`() {
        val character: Character = createCharacter(InventoryGroup.CHEST to 116)
        character.wearProtectionItem(InventoryGroup.RING, 10)
        character.bonus.protectionFromPotion = 10

        assertThat(character.getProtectionFromArmor()).isEqualTo(80)
        assertThat(character.getCalculatedTotalProtection()).isEqualTo(MAX_PROTECTION_PERCENT)
    }

    private fun createCharacter(vararg armorPerGroup: Pair<InventoryGroup, Int>): Character {
        val equipment = EquipContainer()
        armorPerGroup.forEach { (group: InventoryGroup, armor: Int) ->
            equipment.forceSetInventoryItem(group, InventoryItem(group = group, armor = armor))
        }
        return object : Character(inventory = equipment) {}
    }

    private fun Character.wearProtectionItem(group: InventoryGroup, protection: Int) {
        this.forceSetInventoryItemFor(group, InventoryItem(group = group, protection = protection))
    }

}
