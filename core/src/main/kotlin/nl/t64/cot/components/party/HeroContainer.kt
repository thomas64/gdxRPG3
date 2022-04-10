package nl.t64.cot.components.party

import nl.t64.cot.ConfigDataLoader


class HeroContainer {

    private val heroes: MutableMap<String, HeroItem> = ConfigDataLoader.createHeroes()
    val size: Int get() = heroes.size

    fun addHero(hero: HeroItem) {
        heroes[hero.id] = hero
    }

    fun removeHero(heroId: String) {
        heroes.remove(heroId)
    }

    fun getCertainHero(heroId: String): HeroItem {
        return heroes[heroId]!!
    }

    fun contains(heroId: String): Boolean {
        return heroes.containsKey(heroId)
    }

}
