package nl.t64.cot.components.party.inventory


class BattleWeaponItem(
    val inventoryItem: InventoryItem
) {
    private val durability: Int = inventoryItem.durability
    private val maxDurability: Int = inventoryItem.maxDurability
    val name: String = inventoryItem.name
    val group: InventoryGroup = inventoryItem.group

    override fun toString(): String {
        return when (name) {
            "Unequip current weapon",
            "Unequip current shield",
            "Back" -> name
            else -> String.format("%-25s%7s", name, "($durability/$maxDurability)")
        }
    }

}
