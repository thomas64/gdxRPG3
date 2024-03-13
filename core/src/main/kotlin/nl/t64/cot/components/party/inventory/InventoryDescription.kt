package nl.t64.cot.components.party.inventory

import nl.t64.cot.components.party.CalcAttributeId
import nl.t64.cot.components.party.HeroItem
import nl.t64.cot.components.party.skills.SkillItemId
import nl.t64.cot.components.party.stats.StatItemId


class InventoryDescription {

    val key: Any        // Any can be SuperEnum or String.
    val value: Any      // Any can be Integer or String.
    val compare: AttributeState

    constructor(key: Any, value: Any) {
        this.key = key
        this.value = value
        this.compare = AttributeState.SAME
    }

    constructor(key: Any, value: Any, item: InventoryItem, hero: HeroItem) {
        this.key = key
        this.value = value
        this.compare = isEnough(item, hero)
    }

    constructor(key: Any, value: Any, item1: InventoryItem, item2: InventoryItem, hero: HeroItem) {
        this.key = key
        this.value = value
        this.compare = compare(item1, item2, hero)
    }

    private fun isEnough(item: InventoryItem, hero: HeroItem): AttributeState {
        return when {
            isHeroNotEnoughForItem(item, hero) -> AttributeState.CANNOT_USE
            else -> AttributeState.SAME
        }
    }

    private fun compare(item1: InventoryItem, item2: InventoryItem, hero: HeroItem): AttributeState {
        return when {
            isHeroNotEnoughForItem(item1, hero) -> AttributeState.CANNOT_USE
            value is Int -> compareInt(item1, item2)
            value == "0" -> AttributeState.LESS
            else -> AttributeState.SAME
        }
    }

    private fun isHeroNotEnoughForItem(item: InventoryItem, hero: HeroItem): Boolean {
        return key is InventoryMinimal && key.createMessageIfHeroHasNotEnoughFor(item, hero) != null
    }

    private fun compareInt(item1: InventoryItem, item2: InventoryItem): AttributeState {
        return when (key) {
            is StatItemId -> compareStats(item1, item2)
            is SkillItemId -> compareSkills(item1, item2)
            is CalcAttributeId -> compareCalcs(item1, item2)
            else -> AttributeState.SAME
        }
    }

    private fun compareStats(item1: InventoryItem, item2: InventoryItem): AttributeState {
        key as StatItemId
        return when {
            item1.getAttributeOfStatItemId(key) < item2.getAttributeOfStatItemId(key) -> AttributeState.LESS
            item1.getAttributeOfStatItemId(key) > item2.getAttributeOfStatItemId(key) -> AttributeState.MORE
            else -> AttributeState.SAME
        }
    }

    private fun compareSkills(item1: InventoryItem, item2: InventoryItem): AttributeState {
        key as SkillItemId
        return when {
            item1.getAttributeOfSkillItemId(key) < item2.getAttributeOfSkillItemId(key) -> AttributeState.LESS
            item1.getAttributeOfSkillItemId(key) > item2.getAttributeOfSkillItemId(key) -> AttributeState.MORE
            else -> AttributeState.SAME
        }
    }

    private fun compareCalcs(item1: InventoryItem, item2: InventoryItem): AttributeState {
        key as CalcAttributeId
        return when {
            item1.getAttributeOfCalcAttributeId(key) < item2.getAttributeOfCalcAttributeId(key) -> AttributeState.LESS
            item1.getAttributeOfCalcAttributeId(key) > item2.getAttributeOfCalcAttributeId(key) -> AttributeState.MORE
            else -> AttributeState.SAME
        }
    }

}
