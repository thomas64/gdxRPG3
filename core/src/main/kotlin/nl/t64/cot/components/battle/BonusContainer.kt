package nl.t64.cot.components.battle

import nl.t64.cot.components.party.inventory.InventoryItem
import nl.t64.cot.components.party.skills.SkillItemId
import nl.t64.cot.components.party.stats.StatItemId


class BonusContainer {

    var protectionFromSpell: Int = 0
    var protectionFromPotion: Int = 0

    var intelligenceFromSpell: Int = 0
    var intelligenceFromPotion: Int = 0

    var dexterityFromSpell: Int = 0
    var dexterityFromPotion: Int = 0

    var strengthFromSpell: Int = 0
    var strengthFromPotion: Int = 0

    var speedFromSpell: Int = 0
    var speedFromPotion: Int = 0

    var willpowerFromSpell: Int = 0
    var willpowerFromPotion: Int = 0

    var stealthFromPotion: Int = 0

    var hitBonusFromTroubadour: Int = 0
    var hitPenaltyFromTroubadour: Int = 0

    fun reset() {
        protectionFromSpell = 0
        protectionFromPotion = 0
        intelligenceFromSpell = 0
        intelligenceFromPotion = 0
        dexterityFromSpell = 0
        dexterityFromPotion = 0
        strengthFromSpell = 0
        strengthFromPotion = 0
        speedFromSpell = 0
        speedFromPotion = 0
        willpowerFromSpell = 0
        willpowerFromPotion = 0
        stealthFromPotion = 0
        hitBonusFromTroubadour = 0
        hitPenaltyFromTroubadour = 0
    }

    fun wouldPotionHaveEffect(potion: InventoryItem): Boolean {
        return potion.protection > protectionFromPotion
            || potion.intelligence > intelligenceFromPotion
            || potion.dexterity > dexterityFromPotion
            || potion.strength > strengthFromPotion
            || potion.speed > speedFromPotion
            || potion.willpower > willpowerFromPotion
            || potion.stealth > stealthFromPotion
    }

    fun applyPotion(potion: InventoryItem) {
        if (potion.protection > protectionFromPotion) {
            protectionFromPotion = potion.protection
        }
        if (potion.intelligence > intelligenceFromPotion) {
            intelligenceFromPotion = potion.intelligence
        }
        if (potion.dexterity > dexterityFromPotion) {
            dexterityFromPotion = potion.dexterity
        }
        if (potion.strength > strengthFromPotion) {
            strengthFromPotion = potion.strength
        }
        if (potion.speed > speedFromPotion) {
            speedFromPotion = potion.speed
        }
        if (potion.willpower > willpowerFromPotion) {
            willpowerFromPotion = potion.willpower
        }
        if (potion.stealth > stealthFromPotion) {
            stealthFromPotion = potion.stealth
        }
    }

    fun getProtection(): Int {
        return protectionFromSpell + protectionFromPotion
    }

    fun getHitBonus(): Int {
        return hitBonusFromTroubadour - hitPenaltyFromTroubadour
    }

    fun getStatBonus(statItemId: StatItemId): Int {
        return when (statItemId) {
            StatItemId.INTELLIGENCE -> intelligenceFromSpell + intelligenceFromPotion
            StatItemId.DEXTERITY -> dexterityFromSpell + dexterityFromPotion
            StatItemId.STRENGTH -> strengthFromSpell + strengthFromPotion
            StatItemId.SPEED -> speedFromSpell + speedFromPotion
            StatItemId.WILLPOWER -> willpowerFromSpell + willpowerFromPotion
            StatItemId.CONSTITUTION -> 0
            StatItemId.STAMINA -> 0
        }
    }

    fun getSkillBonus(skillItemId: SkillItemId): Int {
        return when (skillItemId) {
            SkillItemId.STEALTH -> stealthFromPotion
            else -> 0
        }
    }

}
