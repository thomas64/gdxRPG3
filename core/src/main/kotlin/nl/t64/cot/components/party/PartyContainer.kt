package nl.t64.cot.components.party

import nl.t64.cot.components.party.skills.SkillItemId
import nl.t64.cot.constants.Constant
import nl.t64.cot.resources.ConfigDataLoader
import java.util.*


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

    fun toProgress(): Map<String, HeroProgress> {
        return party.mapValues { (_, hero) -> hero.toProgress() }
    }

    fun applyProgress(progress: Map<String, HeroProgress>) {
        val heroesFromConfig: Map<String, HeroItem> = ConfigDataLoader.createHeroes()
        party.clear()
        progress.forEach { (heroId, heroProgress) ->
            val heroItem: HeroItem = heroesFromConfig[heroId]!!
            val heroItemWithProgress: HeroItem = heroItem.apply { applyProgress(heroProgress) }
            party[heroId] = heroItemWithProgress
        }
    }

    fun forEachWithInvertedIndex(action: (index: Int, hero: HeroItem) -> Unit) {
        party.entries.forEachIndexed { index, entry ->
            val invertedIndex = MAXIMUM - 1 - index
            action(invertedIndex, entry.value)
        }
    }

    fun forEachIndexToMaximum(action: (index: Int) -> Unit) {
        (0 until MAXIMUM).forEach { action(it) }
    }

    fun gainXp(amount: Int) {
        getAllHeroesAlive().forEach { it.gainXp(amount) }
    }

    fun fullRecover() {
        getAllHeroesAlive().forEach {
            it.recoverFullHp()
            it.recoverFullSp()
        }
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

    fun canMoveHeroLeft(hero: HeroItem): Boolean {
        return getIndex(hero) > 1
    }

    fun canMoveHeroRight(hero: HeroItem): Boolean {
        val index: Int = getIndex(hero)
        return index in 1..<lastIndex
    }

    fun moveHeroLeft(hero: HeroItem) {
        val index: Int = getIndex(hero)
        swapHeroes(index, index - 1)
    }

    fun moveHeroRight(hero: HeroItem) {
        val index: Int = getIndex(hero)
        swapHeroes(index, index + 1)
    }

    private fun swapHeroes(indexA: Int, indexB: Int) {
        require(indexA != 0 && indexB != 0) { "Cannot move the party leader." }
        val allHeroes: MutableList<HeroItem> = getAllHeroes().toMutableList()
        Collections.swap(allHeroes, indexA, indexB)
        party.clear()
        allHeroes.forEach { party[it.id] = it }
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
        return getPossibleHero(candidateHeroObject.id) === candidateHeroObject
    }

    fun isHeroLast(hero: HeroItem): Boolean {
        return getIndex(hero) == lastIndex
    }

    private fun isHeroFirst(hero: HeroItem): Boolean {
        return getIndex(hero) == 0
    }

    fun getAverageXp(): Double {
        return getAllHeroesAlive().map { it.totalXp }.average()
    }

    fun getSumOfSkill(skillItemId: SkillItemId): Int {
        return getAllCalculatedTotalSkillsOf(skillItemId).sum()
    }

    fun getHeroWithHighestSkill(skillItemId: SkillItemId): HeroItem {
        return getAllHeroesAlive().maxByOrNull { it.getCalculatedTotalSkillOf(skillItemId) } ?: firstHero
    }

    fun hasEnoughOfSkill(skillItemId: SkillItemId, rank: Int): Boolean {
        return getBestSkillRank(skillItemId) >= rank
    }

    fun getBestSkillRank(skillItemId: SkillItemId): Int {
        return getAllCalculatedTotalSkillsOf(skillItemId).maxOfOrNull { it } ?: 0
    }

    fun hasItemInEquipment(inventoryItemId: String, amount: Int): Boolean {
        return getAmountOfItemInEquipment(inventoryItemId) >= amount
    }

    fun getAmountOfItemInEquipment(inventoryItemId: String): Int {
        return getAllHeroes().count { it.hasInventoryItem(inventoryItemId) }
    }

    fun contains(heroId: String): Boolean {
        return party.containsKey(heroId)
    }

    fun contains(index: Int): Boolean {
        return index <= party.size - 1
    }

    fun getPlayer(): HeroItem {
        return getHero(0)
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

    private fun getAllCalculatedTotalSkillsOf(skillItemId: SkillItemId): List<Int> {
        return getAllHeroesAlive().map { it.getCalculatedTotalSkillOf(skillItemId) }
    }

}
