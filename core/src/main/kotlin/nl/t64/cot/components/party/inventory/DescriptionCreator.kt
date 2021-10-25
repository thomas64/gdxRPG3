package nl.t64.cot.components.party.inventory

import nl.t64.cot.components.party.CalcAttributeId
import nl.t64.cot.components.party.HeroItem
import nl.t64.cot.components.party.skills.SkillItemId
import nl.t64.cot.components.party.stats.StatItemId
import nl.t64.cot.constants.Constant


class DescriptionCreator(
    private val inventoryItem: InventoryItem,
    private val partySumOfMerchantSkill: Int
) {

    private val descriptionList: MutableList<InventoryDescription> = ArrayList()

    fun createItemDescription(): List<InventoryDescription> {
        return createDescriptionList(
            { key: Any, value: Any -> InventoryDescription(key, value) },
            emptySet(), emptySet(), emptySet(), emptySet(), null
        )
    }

    fun createItemDescriptionComparingToHero(hero: HeroItem): List<InventoryDescription> {
        return createDescriptionList(
            { key: Any, value: Any -> InventoryDescription(key, value, inventoryItem, hero) },
            emptySet(), emptySet(), emptySet(), emptySet(), null
        )
    }

    fun createItemDescriptionComparingToItem(otherItem: InventoryItem): List<InventoryDescription> {
        return createDescriptionList(
            { key: Any, value: Any -> InventoryDescription(key, value, inventoryItem, otherItem) },
            inventoryItem.getMinimalsOtherItemHasAndYouDont(otherItem),
            inventoryItem.getCalcsOtherItemHasAndYouDont(otherItem),
            inventoryItem.getStatsOtherItemHasAndYouDont(otherItem),
            inventoryItem.getSkillsOtherItemHasAndYouDont(otherItem),
            otherItem
        )
    }

    private fun createDescriptionList(
        createDescription: (Any, Any) -> InventoryDescription,
        minimalsYouDontHave: Set<InventoryMinimal>,
        calcsYouDontHave: Set<CalcAttributeId>,
        statsYouDontHave: Set<StatItemId>,
        skillsYouDontHave: Set<SkillItemId>,
        otherItem: InventoryItem?
    ): List<InventoryDescription> {
        descriptionList.add(createDescription.invoke(inventoryItem.group, inventoryItem.name))
        addHandiness(createDescription)
        addPrices(createDescription)
        addMinimals(createDescription, minimalsYouDontHave)
        addCalcs(createDescription, calcsYouDontHave)
        addStats(createDescription, statsYouDontHave)
        addSkills(createDescription, skillsYouDontHave)
        addPossibleEmptyLines(createDescription, otherItem)
        return createFilter()
    }

    private fun addHandiness(createDescription: (Any, Any) -> InventoryDescription) {
        if (inventoryItem.group == InventoryGroup.WEAPON) {
            val handinessTitle = if (inventoryItem.isTwoHanded) "(Two-handed)" else "(One-handed)"
            descriptionList.add(createDescription.invoke(handinessTitle, ""))
        }
    }

    private fun addPrices(createDescription: (Any, Any) -> InventoryDescription) {
        when {
            inventoryItem.amount == 1 -> {
                descriptionList.add(createDescription.invoke(Constant.DESCRIPTION_KEY_BUY, inventoryItem.getBuyPriceTotal(partySumOfMerchantSkill)))
                descriptionList.add(createDescription.invoke(Constant.DESCRIPTION_KEY_SELL, inventoryItem.getSellValueTotal(partySumOfMerchantSkill)))
            }
            inventoryItem.amount > 1 -> {
                descriptionList.add(createDescription.invoke(Constant.DESCRIPTION_KEY_BUY_PIECE, inventoryItem.getBuyPricePiece(partySumOfMerchantSkill)))
                descriptionList.add(createDescription.invoke(Constant.DESCRIPTION_KEY_BUY_TOTAL, inventoryItem.getBuyPriceTotal(partySumOfMerchantSkill)))
                descriptionList.add(createDescription.invoke(Constant.DESCRIPTION_KEY_SELL_PIECE, inventoryItem.getSellValuePiece(partySumOfMerchantSkill)))
                descriptionList.add(createDescription.invoke(Constant.DESCRIPTION_KEY_SELL_TOTAL, inventoryItem.getSellValueTotal(partySumOfMerchantSkill)))
            }
            else -> {
                throw IllegalStateException("Amount cannot be below 1.")
            }
        }
    }

    private fun addMinimals(
        createDescription: (Any, Any) -> InventoryDescription, minimalsYouDontHave: Set<InventoryMinimal>
    ) {
        InventoryMinimal.values().forEach {
            if (minimalsYouDontHave.contains(it)) {
                descriptionList.add(createDescription.invoke("", ""))
            } else {
                descriptionList.add(createDescription.invoke(it, inventoryItem.getAttributeOfMinimal(it)))
            }
        }
    }

    private fun addCalcs(
        createDescription: (Any, Any) -> InventoryDescription, calcsYouDontHave: Set<CalcAttributeId>
    ) {
        CalcAttributeId.values().forEach {
            val value = if (calcsYouDontHave.contains(it)) "0" else inventoryItem.getAttributeOfCalcAttributeId(it)
            descriptionList.add(createDescription.invoke(it, value))
        }
    }

    private fun addStats(
        createDescription: (Any, Any) -> InventoryDescription, statsYouDontHave: Set<StatItemId>
    ) {
        StatItemId.values().forEach {
            val value = if (statsYouDontHave.contains(it)) "0" else inventoryItem.getAttributeOfStatItemId(it)
            descriptionList.add(createDescription.invoke(it, value))
        }
    }

    private fun addSkills(
        createDescription: (Any, Any) -> InventoryDescription, skillsYouDontHave: Set<SkillItemId>
    ) {
        SkillItemId.values().forEach {
            val value = if (skillsYouDontHave.contains(it)) "0" else inventoryItem.getAttributeOfSkillItemId(it)
            descriptionList.add(createDescription.invoke(it, value))
        }
    }

    private fun addPossibleEmptyLines(
        createDescription: (Any, Any) -> InventoryDescription, otherItem: InventoryItem?
    ) {
        val otherItemDescriptionSize = otherItem?.description?.size ?: 0
        (0 until otherItemDescriptionSize)
            .map { createDescription.invoke("", "") }
            .forEach { descriptionList.add(it) }
    }

    private fun createFilter(): List<InventoryDescription> {
        return descriptionList.filter { mustBeAdded(it) }
    }

    private fun mustBeAdded(description: InventoryDescription): Boolean {
        if (description.key == Constant.DESCRIPTION_KEY_BUY_TOTAL
            || description.key == Constant.DESCRIPTION_KEY_SELL_TOTAL
            || description.key == Constant.DESCRIPTION_KEY_BUY_PIECE
            || description.key == Constant.DESCRIPTION_KEY_SELL_PIECE
            || description.key == Constant.DESCRIPTION_KEY_BUY
            || description.key == Constant.DESCRIPTION_KEY_SELL
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
        if ((description.key == CalcAttributeId.PROTECTION
                    || description.key == SkillItemId.STEALTH)
            && inventoryItem.group.hasImpactOnPrtStl()
        ) {
            return true
        }
        if (description.key == CalcAttributeId.WEIGHT
            && (inventoryItem.group == InventoryGroup.SHIELD
                    || inventoryItem.group == InventoryGroup.WEAPON
                    || inventoryItem.group == InventoryGroup.RESOURCE
                    || inventoryItem.group == InventoryGroup.POTION
                    || inventoryItem.group == InventoryGroup.ITEM
                    || inventoryItem.group == InventoryGroup.RING
                    || inventoryItem.group == InventoryGroup.NECKLACE)
        ) {
            return false
        }
        if (description.key == CalcAttributeId.WEIGHT) {
            return true
        }
        if (description.value == 0) {
            return false
        }
        return true
    }

}
