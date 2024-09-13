package nl.t64.cot.components.party.inventory


class BattleWeaponItem(
    val inventoryItem: InventoryItem
) {
    private val durability: Int = inventoryItem.durability
    val name: String = inventoryItem.name

    constructor(name: String) : this(InventoryItem(name = name))

    override fun toString(): String {
        return when (name) {
            "Back" -> name
            else -> "$name ($durability)"
        }
    }

}
