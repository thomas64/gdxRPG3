package nl.t64.cot.components.party.inventory

import nl.t64.cot.components.party.HeroItem
import nl.t64.cot.components.party.SuperEnum
import nl.t64.cot.components.party.stats.StatItemId


enum class InventoryMinimal(override val title: String) : SuperEnum {

    SKILL("Skill") {
        override fun createMessageIfHeroHasNotEnoughFor(item: InventoryItem, hero: HeroItem): String? {
            return createSkillMessage(item, hero)
        }
    },
    MIN_INTELLIGENCE("Min. Intelligence") {
        override fun createMessageIfHeroHasNotEnoughFor(item: InventoryItem, hero: HeroItem): String? {
            return createMinimalMessage(item, StatItemId.INTELLIGENCE, hero)
        }
    },
    MIN_WILLPOWER("Min. Willpower") {
        override fun createMessageIfHeroHasNotEnoughFor(item: InventoryItem, hero: HeroItem): String? {
            return createMinimalMessage(item, StatItemId.WILLPOWER, hero)
        }
    },
    MIN_STRENGTH("Min. Strength") {
        override fun createMessageIfHeroHasNotEnoughFor(item: InventoryItem, hero: HeroItem): String? {
            return createMinimalMessage(item, StatItemId.STRENGTH, hero)
        }
    },
    MIN_DEXTERITY("Min. Dexterity") {
        override fun createMessageIfHeroHasNotEnoughFor(item: InventoryItem, hero: HeroItem): String? {
            return createMinimalMessage(item, StatItemId.DEXTERITY, hero)
        }
    };

    abstract fun createMessageIfHeroHasNotEnoughFor(item: InventoryItem, hero: HeroItem): String?

    fun createSkillMessage(item: InventoryItem, hero: HeroItem): String? {
        return item.skill
            ?.takeIf { hero.getSkillById(it).rank <= 0 }
            ?.let { "${hero.name} needs the ${it.title} skill\nto equip that ${item.name}." }
    }

    fun createMinimalMessage(item: InventoryItem, statItemId: StatItemId, hero: HeroItem): String? {
        return item.getMinimalAttributeOfStatItemId(statItemId)
            .takeIf { hero.getCalculatedTotalStatOf(statItemId) < it }
            ?.let { "${hero.name} needs $it ${statItemId.title}\nto equip that ${item.name}." }
    }

}
