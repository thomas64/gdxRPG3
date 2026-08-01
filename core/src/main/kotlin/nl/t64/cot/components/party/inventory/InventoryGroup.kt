package nl.t64.cot.components.party.inventory

import nl.t64.cot.components.party.SuperEnum


enum class InventoryGroup(override val title: String) : SuperEnum {

    EVERYTHING(""),
    SHOP_ITEM(""),
    LOOT_ITEM(""),

    WEAPON("Weapon"),
    SHIELD("Shield"),
    ACCESSORY("Accessory"),

    HELMET("Helmet"),
    NECKLACE("Necklace"),
    SHOULDERS("Shoulders"),
    CHEST("Chest"),
    CLOAK("Cloak"),
    BRACERS("Bracers"),
    GLOVES("Gloves"),
    RING("Ring"),
    BELT("Belt"),
    PANTS("Leggings"),
    BOOTS("Boots"),

    EMPTY(""),

    POTION("Potion"),
    ITEM("Item"),
    RESOURCE("Resource");

    fun hasImpactOnPrtSpdStl(): Boolean {
        return this in listOf(SHIELD, HELMET, SHOULDERS, CHEST, CLOAK, BRACERS, GLOVES, BELT, PANTS, BOOTS)
    }

    fun isPartArmorOfSet(): Boolean {
        return this in listOf(HELMET, SHOULDERS, CHEST, CLOAK, BRACERS, GLOVES, BELT, PANTS, BOOTS)
    }

    fun isStackable(): Boolean {
        return this in listOf(POTION, RESOURCE)
    }

    fun getDefaultShopAmount(): Int {
        return when (this) {
            RESOURCE -> 20
            POTION -> 10
            else -> 1
        }
    }

}
