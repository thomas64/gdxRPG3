package nl.t64.cot.components.party.inventory

import nl.t64.cot.components.party.CalcAttributeId
import nl.t64.cot.components.party.HeroItem
import nl.t64.cot.components.party.skills.SkillItemId
import nl.t64.cot.components.party.stats.StatItemId
import nl.t64.cot.constants.Constant


class DescriptionCreator(
    private val inventoryItem: InventoryItem,
    private val partySumOfMerchantSkill: Int,
) {
    private val descriptionLines: MutableList<InventoryDescription> = ArrayList()
    private lateinit var createLine: (Any, Any) -> InventoryDescription

    private var minimalsYouDontHave: Set<InventoryMinimal> = emptySet()
    private var calcsYouDontHave: Set<CalcAttributeId> = emptySet()
    private var statsYouDontHave: Set<StatItemId> = emptySet()
    private var skillsYouDontHave: Set<SkillItemId> = emptySet()
    private var otherItem: InventoryItem? = null

    fun createItemDescription(): List<InventoryDescription> {
        createLine = { key: Any, value: Any -> InventoryDescription(key, value) }
        return createDescriptionList()
    }

    fun createItemDescriptionComparingToHero(hero: HeroItem): List<InventoryDescription> {
        createLine = { key: Any, value: Any -> InventoryDescription(key, value, inventoryItem, hero) }
        return createDescriptionList()
    }

    fun createItemDescriptionComparingToItemAndHero(otherItem: InventoryItem,
                                                    hero: HeroItem): List<InventoryDescription> {
        createLine = { key: Any, value: Any -> InventoryDescription(key, value, inventoryItem, otherItem, hero) }
        minimalsYouDontHave = inventoryItem.getMinimalsOtherItemHasAndYouDont(otherItem)
        calcsYouDontHave = inventoryItem.getCalcsOtherItemHasAndYouDont(otherItem)
        statsYouDontHave = inventoryItem.getStatsOtherItemHasAndYouDont(otherItem)
        skillsYouDontHave = inventoryItem.getSkillsOtherItemHasAndYouDont(otherItem)
        this.otherItem = otherItem
        return createDescriptionList()
    }

    private fun createDescriptionList(): List<InventoryDescription> {
        addName()
        addHandiness()
        addPrices()
        addMinimals()
        addCalcs()
        addStats()
        addSkills()
        addPossibleEmptyLines()
        return getFilteredLines()
    }

    private fun addName() {
        descriptionLines.add(createLine(inventoryItem.group, inventoryItem.name))
    }

    private fun addHandiness() {
        if (inventoryItem.group == InventoryGroup.WEAPON) {
            val handinessTitle = if (inventoryItem.isTwoHanded) "(Two-handed)" else "(One-handed)"
            descriptionLines.add(createLine(handinessTitle, ""))
        }
    }

    private fun addPrices() {
        when {
            inventoryItem.amount == 1 -> createLinesWithSinglePrice()
            inventoryItem.amount > 1 -> createLinesWithMultiPrices()
            else -> throw IllegalStateException("Amount cannot be below 1.")
        }
    }

    private fun createLinesWithSinglePrice() {
        listOf(
            createLine(Constant.DESCRIPTION_KEY_BUY, inventoryItem.getBuyPriceTotal(partySumOfMerchantSkill)),
            createLine(Constant.DESCRIPTION_KEY_SELL, inventoryItem.getSellValueTotal(partySumOfMerchantSkill))
        ).forEach { descriptionLines.add(it) }
    }

    private fun createLinesWithMultiPrices() {
        listOf(
            createLine(Constant.DESCRIPTION_KEY_BUY_PIECE, inventoryItem.getBuyPricePiece(partySumOfMerchantSkill)),
            createLine(Constant.DESCRIPTION_KEY_BUY_TOTAL, inventoryItem.getBuyPriceTotal(partySumOfMerchantSkill)),
            createLine(Constant.DESCRIPTION_KEY_SELL_PIECE, inventoryItem.getSellValuePiece(partySumOfMerchantSkill)),
            createLine(Constant.DESCRIPTION_KEY_SELL_TOTAL, inventoryItem.getSellValueTotal(partySumOfMerchantSkill))
        ).forEach { descriptionLines.add(it) }
    }

    private fun addMinimals() {
        InventoryMinimal.entries.forEach {
            if (it in minimalsYouDontHave) {
                descriptionLines.add(createEmptyLine())
            } else {
                descriptionLines.add(createLine(it, inventoryItem.getAttributeOfMinimal(it)))
            }
        }
    }

    private fun addCalcs() {
        CalcAttributeId.entries.forEach {
            val value = if (it in calcsYouDontHave) "0" else inventoryItem.getAttributeOfCalcAttributeId(it)
            descriptionLines.add(createLine(it, value))
        }
    }

    private fun addStats() {
        StatItemId.entries.forEach {
            val value = if (it in statsYouDontHave) "0" else inventoryItem.getAttributeOfStatItemId(it)
            descriptionLines.add(createLine(it, value))
        }
    }

    private fun addSkills() {
        SkillItemId.entries.forEach {
            val value = if (it in skillsYouDontHave) "0" else inventoryItem.getAttributeOfSkillItemId(it)
            descriptionLines.add(createLine(it, value))
        }
    }

    private fun addPossibleEmptyLines() {
        (0 until (otherItem?.description?.size ?: 0))
            .map { createEmptyLine() }
            .forEach { descriptionLines.add(it) }
    }

    private fun createEmptyLine(): InventoryDescription {
        return createLine("", "")
    }

    private fun createLine(key: Any, value: Any): InventoryDescription {
        return createLine.invoke(key, value)
    }

    private fun getFilteredLines(): List<InventoryDescription> {
        return descriptionLines.filter { mustBeAdded(it) }
    }

    private fun mustBeAdded(description: InventoryDescription): Boolean {
        if (description.key in listOf(Constant.DESCRIPTION_KEY_BUY_TOTAL,
                                      Constant.DESCRIPTION_KEY_SELL_TOTAL,
                                      Constant.DESCRIPTION_KEY_BUY_PIECE,
                                      Constant.DESCRIPTION_KEY_SELL_PIECE,
                                      Constant.DESCRIPTION_KEY_BUY,
                                      Constant.DESCRIPTION_KEY_SELL)
        ) {
            return true
        }
        if (description.key is InventoryGroup) {
            return true
        }
        if (description.value is SkillItemId) {
            return true
        }
        if (description.key == CalcAttributeId.TRANSFORMATION) {
            return false
        }
        if (description.key in listOf(CalcAttributeId.PROTECTION,
                                      StatItemId.SPEED,
                                      SkillItemId.STEALTH)
            && inventoryItem.group.hasImpactOnPrtSpdStl()
        ) {
            return true
        }
        if (description.value == 0) {
            return false
        }
        return true
    }

}
