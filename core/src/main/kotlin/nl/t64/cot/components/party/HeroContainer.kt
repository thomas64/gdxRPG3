package nl.t64.cot.components.party

import nl.t64.cot.constants.Constant
import nl.t64.cot.resources.ConfigDataLoader


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

    fun hasAnyoneBeenRecruited(): Boolean {
        return heroes
            .filterKeys { it != Constant.PLAYER_ID }
            .values.any { it.hasBeenRecruited }
    }

}
