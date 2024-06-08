package nl.t64.cot.components.party.inventory


class BattlePotionItem(
    private val inventoryItem: InventoryItem
) {
    private val amount: Int = inventoryItem.amount
    val id: String = inventoryItem.id
    val name: String = inventoryItem.name
    val description: String = createDescription()

    constructor(name: String) : this(InventoryItem(name = name))

    override fun toString(): String {
        return when (name) {
            "Back" -> name
            else -> "$name ($amount)"
        }
    }

    private fun createDescription(): String {
        return inventoryItem.description
            .takeWhile { !it.startsWith("[") }
            .joinToString("\n")
    }

}
