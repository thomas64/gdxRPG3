package nl.t64.cot.components.party.inventory

import nl.t64.cot.components.party.CalcAttributeId
import nl.t64.cot.components.party.HeroItem
import nl.t64.cot.components.party.skills.SkillItemId
import nl.t64.cot.components.party.stats.StatItemId


class InventoryDescription {

    val key: Any        // Any can be SuperEnum or String.
    val value: Any      // Any can be Integer or String.
    val compare: ThreeState

    constructor(key: Any, value: Any) {
        this.key = key
        this.value = value
        this.compare = ThreeState.SAME
    }

    constructor(key: Any, value: Any, item: InventoryItem, hero: HeroItem) {
        this.key = key
        this.value = value
        this.compare = isEnough(item, hero)
    }

    constructor(key: Any, value: Any, item1: InventoryItem, item2: InventoryItem) {
        this.key = key
        this.value = value
        this.compare = compare(item1, item2)
    }

    private fun isEnough(item: InventoryItem, hero: HeroItem): ThreeState {
        return when {
            value == 0 -> ThreeState.SAME
            key is InventoryMinimal && key.createMessageIfHeroHasNotEnoughFor(item, hero) != null -> ThreeState.LESS
            else -> ThreeState.SAME
        }
    }

    private fun compare(item1: InventoryItem, item2: InventoryItem): ThreeState {
        return when (value) {
            is Int -> compareInt(item1, item2)
            "0" -> ThreeState.LESS
            else -> ThreeState.SAME
        }
    }

    private fun compareInt(item1: InventoryItem, item2: InventoryItem): ThreeState {
        return when (key) {
            is StatItemId -> compareStats(item1, item2)
            is SkillItemId -> compareSkills(item1, item2)
            is CalcAttributeId -> compareCalcs(item1, item2)
            else -> ThreeState.SAME
        }
    }

    private fun compareStats(item1: InventoryItem, item2: InventoryItem): ThreeState {
        key as StatItemId
        return when {
            item1.getAttributeOfStatItemId(key) < item2.getAttributeOfStatItemId(key) -> ThreeState.LESS
            item1.getAttributeOfStatItemId(key) > item2.getAttributeOfStatItemId(key) -> ThreeState.MORE
            else -> ThreeState.SAME
        }
    }

    private fun compareSkills(item1: InventoryItem, item2: InventoryItem): ThreeState {
        key as SkillItemId
        return when {
            item1.getAttributeOfSkillItemId(key) < item2.getAttributeOfSkillItemId(key) -> ThreeState.LESS
            item1.getAttributeOfSkillItemId(key) > item2.getAttributeOfSkillItemId(key) -> ThreeState.MORE
            else -> ThreeState.SAME
        }
    }

    private fun compareCalcs(item1: InventoryItem, item2: InventoryItem): ThreeState {
        key as CalcAttributeId
        return when {
            item1.getAttributeOfCalcAttributeId(key) < item2.getAttributeOfCalcAttributeId(key) -> ThreeState.LESS
            item1.getAttributeOfCalcAttributeId(key) > item2.getAttributeOfCalcAttributeId(key) -> ThreeState.MORE
            else -> ThreeState.SAME
        }
    }

}
