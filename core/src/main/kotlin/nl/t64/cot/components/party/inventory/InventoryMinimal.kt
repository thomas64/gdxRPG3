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
        return item.skill?.let {
            when {
                hero.getSkillById(it).rank <= 0 -> """
                    ${hero.name} needs the ${it.title} skill
                    to equip that ${item.name}.""".trimIndent()
                else -> null
            }
        }
    }

    fun createMinimalMessage(item: InventoryItem, statItemId: StatItemId, hero: HeroItem): String? {
        val minimalAttribute: Int = item.getMinimalAttributeOfStatItemId(statItemId)
        return when {
            hero.getCalculatedTotalStatOf(statItemId) < minimalAttribute -> """
                ${hero.name} needs $minimalAttribute ${statItemId.title}
                to equip that ${item.name}.""".trimIndent()
            else -> null
        }
    }

}
