package nl.t64.cot.components.party.inventory

import nl.t64.cot.components.party.SuperEnum


enum class InventoryGroup(override val title: String) : SuperEnum {

    EVERYTHING(""),
    SHOP_ITEM(""),
    LOOT_ITEM(""),

    WEAPON("Weapon"),
    SHIELD("Shield"),               // 12 prt / 30 def
    ACCESSORY("Accessory"),

    HELMET("Helmet"),               // 12 prt
    NECKLACE("Necklace"),
    SHOULDERS("Shoulders"),         // 12 prt
    CHEST("Chest"),                 // 12 prt
    CLOAK("Cloak"),                 // 2 prt    extra stealth
    BRACERS("Bracers"),             // 12 prt
    GLOVES("Gloves"),               // 12 prt
    RING("Ring"),
    BELT("Belt"),                   // 2 prt    extra speed
    PANTS("Pants"),                 // 12 prt
    BOOTS("Boots"),                 // 12 prt   bij complete set prt omhoog

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
            RESOURCE -> 100
            POTION -> 20
            else -> 1
        }
    }

}
