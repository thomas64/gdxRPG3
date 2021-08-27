package nl.t64.cot.components.party

import nl.t64.cot.components.party.skills.SkillItemId
import nl.t64.cot.constants.Constant


class PartyContainer {

    companion object {
        const val MAXIMUM = 6
    }

    private val party: MutableMap<String, HeroItem> = LinkedHashMap(MAXIMUM)
    private val firstHero: HeroItem get() = getHero(0)
    private val lastHero: HeroItem get() = getHero(lastIndex)
    private val lastIndex: Int get() = size - 1
    val isFull: Boolean get() = size >= MAXIMUM
    val size: Int get() = party.size

    fun gainXp(amount: Int, levelUpMessage: StringBuilder) {
        getAllHeroesAlive().forEach { it.gainXp(amount, levelUpMessage) }
    }

    fun getPreviousHero(hero: HeroItem): HeroItem {
        return when {
            isHeroFirst(hero) -> lastHero
            else -> getHero(getIndex(hero) - 1)
        }
    }

    fun getNextHero(hero: HeroItem): HeroItem {
        return when {
            isHeroLast(hero) -> firstHero
            else -> getHero(getIndex(hero) + 1)
        }
    }

    fun addHero(hero: HeroItem) {
        check(!isFull) { "Party is full." }
        party[hero.id] = hero
        hero.hasBeenRecruited = true
    }

    fun removeHero(heroId: String) {
        require(!isPlayer(heroId)) { "Cannot remove player from party." }
        party.remove(heroId)
    }

    fun isPlayer(heroId: String): Boolean {
        return heroId == Constant.PLAYER_ID
    }

    fun containsExactlyEqualTo(candidateHeroObject: HeroItem): Boolean {
        return getPossibleHero(candidateHeroObject.id) == candidateHeroObject
    }

    fun isHeroLast(hero: HeroItem): Boolean {
        return getIndex(hero) == lastIndex
    }

    private fun isHeroFirst(hero: HeroItem): Boolean {
        return getIndex(hero) == 0
    }

    fun getSumOfSkill(skillItemId: SkillItemId): Int {
        return getAllCalculatedTotalSkillsOf(skillItemId).sum()
    }

    fun getHeroWithHighestSkill(skillItemId: SkillItemId): HeroItem {
        return getAllHeroesAlive().maxByOrNull { it.getCalculatedTotalSkillOf(skillItemId) } ?: firstHero
    }

    fun hasEnoughOfSkill(skillItemId: SkillItemId, rank: Int): Boolean {
        return getBestSkillLevel(skillItemId) >= rank
    }

    fun contains(heroId: String): Boolean {
        return party.containsKey(heroId)
    }

    fun contains(index: Int): Boolean {
        return index <= party.size - 1
    }

    fun getHero(index: Int): HeroItem {
        return getAllHeroes()[index]
    }

    fun getIndex(hero: HeroItem): Int {
        return getAllHeroes().indexOf(hero)
    }

    fun getAllHeroesAlive(): List<HeroItem> = getAllHeroes().filter { it.isAlive }

    fun getAllHeroes(): List<HeroItem> = ArrayList(party.values)

    fun getCertainHero(heroId: String): HeroItem {
        return getPossibleHero(heroId)!!
    }

    private fun getPossibleHero(heroId: String): HeroItem? {
        return party[heroId]
    }

    private fun getBestSkillLevel(skillItemId: SkillItemId): Int {
        return getAllCalculatedTotalSkillsOf(skillItemId).maxOfOrNull { it } ?: 0
    }

    private fun getAllCalculatedTotalSkillsOf(skillItemId: SkillItemId): List<Int> {
        return getAllHeroesAlive().map { it.getCalculatedTotalSkillOf(skillItemId) }
    }

}
