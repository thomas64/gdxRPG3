package nl.t64.cot.components.battle

import nl.t64.cot.Utils.preferenceManager
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe
import nl.t64.cot.components.party.skills.SkillItemId
import kotlin.math.abs


private const val BATTLE_FIELD_SIZE = 20

class BattleField(
    participants: List<Participant>,
    private val currentParticipant: () -> Participant
) {

    val heroSpaces: MutableList<Participant?> = MutableList(BATTLE_FIELD_SIZE) { null }
    val enemySpaces: MutableList<Participant?> = MutableList(BATTLE_FIELD_SIZE) { null }
    var startingSpace: Int = -1

    init {
        val heroParticipants: List<Participant> = participants.filter { it.isHero }
        val enemyParticipants: List<Participant> = participants.filter { !it.isHero }

        val orderedHeroes: List<Participant> = heroParticipants
            .sortedByDescending { it.character.getCalculatedTotalSkillOf(SkillItemId.STEALTH) + (Math.random() * 10f) }
        val orderedEnemies: List<Participant> = enemyParticipants
            .sortedBy { it.character.getCalculatedTotalSkillOf(SkillItemId.STEALTH) + (Math.random() * 10f) }

        val heroIndices: List<Int> = (0 until 12).shuffled().take(orderedHeroes.size).sorted()
        val enemyIndices: List<Int> = (8 until BATTLE_FIELD_SIZE).shuffled().take(orderedEnemies.size).sorted()

        orderedHeroes.forEachIndexed { index, participant -> heroSpaces[heroIndices[index]] = participant }
        orderedEnemies.forEachIndexed { index, participant -> enemySpaces[enemyIndices[index]] = participant }
    }

    fun resetStartingSpace() {
        startingSpace = -1
    }

    fun setStartingSpace() {
        startingSpace = getSpaceIndexOfCurrentParticipant()
    }

    fun cancelMovement() {
        if (startingSpace == -1) return
        moveHeroToSpace(startingSpace)
    }

    fun removeDeadParticipants() {
        heroSpaces.removeDeadParticipants()
        enemySpaces.removeDeadParticipants()
    }

    fun createEnemyCountMap(): Map<String, Int> {
        return enemySpaces.filterNotNull()
            .groupingBy { it.character.id }
            .eachCount()
    }

    fun isParticipantNextToOpponent(participant: Participant): Boolean {
        return if (participant.isHero) {
            participant.getSpaceIndex().isHeroSpaceNextToEnemy()
        } else {
            participant.getSpaceIndex().isEnemySpaceNextToHero()
        }
    }

    fun moveHeroRight() {
        val currentIndex: Int = getSpaceIndexOfCurrentParticipant()
        val actionPoints: Int = getModifiedApForHero()
        val upperBound: Int = minOf(startingSpace + 1 + actionPoints, BATTLE_FIELD_SIZE)
        val allSpacesFromHere: IntProgression = currentIndex + 1 until upperBound
        moveHero(allSpacesFromHere)
    }

    fun moveHeroLeft() {
        val currentIndex: Int = getSpaceIndexOfCurrentParticipant()
        val actionPoints: Int = getModifiedApForHero()
        val lowerBound: Int = maxOf(startingSpace - actionPoints, 0)
        val allSpacesFromHere: IntProgression = currentIndex - 1 downTo lowerBound
        moveHero(allSpacesFromHere)
    }

    fun getModifiedApForHero(): Int {
        return currentParticipant.invoke().currentAP - getPenaltyApForHero()
    }

    private fun getModifiedApForEnemy(destinationSpace: Int): Int {
        return currentParticipant.invoke().currentAP - getPenaltyApForEnemy(destinationSpace)
    }

    fun getPenaltyApForHero(): Int {
        return if (isHeroStartingSpaceNextToEnemy()) currentParticipant.invoke().getPenaltyAp() else 0
    }

    private fun getPenaltyApForEnemy(destinationSpace: Int): Int {
        return if (isEnemyStartingSpaceNextToHero(destinationSpace)) currentParticipant.invoke().getPenaltyAp() else 0
    }

    fun getSpaceIndexOfCurrentParticipant(): Int {
        return currentParticipant.invoke().getSpaceIndex()
    }

    fun getTargetableAlliesNextToActingHero(): List<Participant> {
        return heroSpaces.filterNotNull()
            .filter { it != currentParticipant.invoke() }
            .filter { isActingHeroNextToAlly(it) }
    }

    fun getTargetableEnemiesForActingHero(): List<Participant> {
        return enemySpaces.filterNotNull()
            .filter { it.isEnemyInRangeOfActingHero() }
    }

    fun getRangeOfActingHero(): List<Int> {
        return getRange(0, -1)
    }

    private fun getRangeOfActingEnemy(): List<Int> {
        return getRange(1, 0)
    }

    private fun getRange(offsetLeft: Int, offSetRight: Int): List<Int> {
        val currentIndex: Int = getSpaceIndexOfCurrentParticipant()
        return currentParticipant.invoke().getWeaponRanges()
            .map { listOf(currentIndex - it + offsetLeft, currentIndex + it + offSetRight) }
            .flatten()
            .filter { it in 0..BATTLE_FIELD_SIZE }
            .distinct()
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    fun possibleGetHeroTargetAndMoveEnemy(): Participant? {
        setStartingSpace()
        val heroIndicesByPrio: List<Int> = getOccupiedHeroIndicesSortedByPriorityForActingEnemy()
        if (preferenceManager.isDebugModeOn) {
            val actingEnemy: Participant = currentParticipant.invoke()
            println("${actingEnemy.character.name}: At start of turn, AP: ${actingEnemy.currentAP}")
            println("heroIndicesByPrio: $heroIndicesByPrio")
        }

        // if highest prio is already in range, don't move enemy.
        val actingEnemyRangeIndices: List<Int> = getRangeOfActingEnemy()
        val highestPrioHero: Int = heroIndicesByPrio.first()
        if (highestPrioHero in actingEnemyRangeIndices) {
            return heroSpaces[highestPrioHero]
        }

        // move enemy to where its range is closest to its highest prio hero which it can get with its AP.
        getMostPrioSpaceToMoveToForAnAttack(heroIndicesByPrio)?.let {
            takeApForMovingTo(it)
            moveEnemyToIndex(it)
        } ?: return null // or don't move enemy if no such space is available.

        return heroIndicesByPrio
            .firstOrNull { it in getRangeOfActingEnemy() }  // take the first hero that is now in range.
            ?.let { heroSpaces[it] }                        // or null when the AP was not enough to reach the hero.
    }

    private fun getOccupiedHeroIndicesSortedByPriorityForActingEnemy(): List<Int> {
        val actingEnemy: Participant = currentParticipant.invoke()
        return heroSpaces.filterNotNull()
            .sortedBy { hero -> hero.getPriorityFor(actingEnemy, isActingEnemyNextTo(hero)) }
            .map { hero -> hero.getSpaceIndex() }
    }

    private fun getMostPrioSpaceToMoveToForAnAttack(heroIndicesByPrio: List<Int>): Int? {
        return getAllEnemySpacesFromWhereToAttackByPrio(heroIndicesByPrio)
            .onEach { if (isDestinationInRangeOfAP(it)) return it }
            .firstOrNull()
            ?.let { findFarthestReachableSpaceTo(it) }
            ?: getNextBestNearestEmptySpace(heroIndicesByPrio)
    }

    private fun getAllEnemySpacesFromWhereToAttackByPrio(heroIndicesByPrio: List<Int>): List<Int> {
        val currentEnemyIndex: Int = getSpaceIndexOfCurrentParticipant()
        val enemyWeaponRanges: List<Int> = currentParticipant.invoke().getWeaponRanges()

        return enemySpaces.indices
            .filter { it == currentEnemyIndex || enemySpaces[it] == null }
            .flatMap { it.getHeroIndicesInRange(heroIndicesByPrio, enemyWeaponRanges) }
            .groupBy({ it.first }, { it.second })
            .toSortedMap()
            .map { (_, destinationSpaces) -> destinationSpaces.minBy { abs(it - currentEnemyIndex) } }
    }

    private fun Int.getHeroIndicesInRange(heroIndicesByPrio: List<Int>,
                                          enemyWeaponRanges: List<Int>): List<Pair<Int, Int>> {
        val enemyIndex: Int = this
        return heroIndicesByPrio
            .filter { heroIndex -> heroIndex.isHeroInWeaponRangeFrom(enemyIndex, enemyWeaponRanges) }
            .map { heroIndicesByPrio.indexOf(it) to enemyIndex }
    }

    private fun Int.isHeroInWeaponRangeFrom(enemyIndex: Int, enemyWeaponRanges: List<Int>): Boolean {
        val heroIndex: Int = this
        return enemyWeaponRanges.any { range -> heroIndex == enemyIndex - range + 1 || heroIndex == enemyIndex + range }
    }

    private fun isDestinationInRangeOfAP(destinationSpace: Int): Boolean {
        val actionPoints: Int = getModifiedApForEnemy(destinationSpace)
        val currentEnemyIndex: Int = getSpaceIndexOfCurrentParticipant()
        return abs(destinationSpace - currentEnemyIndex) <= actionPoints
    }

    private fun findFarthestReachableSpaceTo(destinationSpace: Int): Int? {
        val currentEnemyIndex: Int = getSpaceIndexOfCurrentParticipant()
        val direction: Int = if (destinationSpace > currentEnemyIndex) 1 else -1
        val actionPoints: Int = getModifiedApForEnemy(destinationSpace)
        return (1..actionPoints)
            .map { currentEnemyIndex + it * direction }
            .lastOrNull { enemySpaces[it] == null }
    }

    private fun getNextBestNearestEmptySpace(heroIndicesByPrio: List<Int>): Int? {
        val currentEnemyIndex: Int = getSpaceIndexOfCurrentParticipant()
        return heroIndicesByPrio
            .flatMap { listOf(it - 1, it) }
            .map { destinationSpace ->
                enemySpaces.indices
                    .filter { it == currentEnemyIndex || enemySpaces[it] == null }
                    .filter { it.isInRangeOfApOfActingEnemy() }
                    .minBy { abs(it - destinationSpace) }
            }
            .minBy { abs(it - currentEnemyIndex) }
            .takeIf { it != currentEnemyIndex }
    }

    private fun Int.isInRangeOfApOfActingEnemy(): Boolean {
        val specificSpace: Int = this
        val actingEnemyIndex: Int = getSpaceIndexOfCurrentParticipant()
        val actingEnemy: Participant = currentParticipant.invoke()

        return specificSpace in (actingEnemyIndex downTo actingEnemyIndex - actingEnemy.currentAP)
            || specificSpace in (actingEnemyIndex until actingEnemyIndex + actingEnemy.currentAP)
    }

    private fun takeApForMovingTo(destinationSpace: Int) {
        val actingEnemy: Participant = currentParticipant.invoke()
        if (preferenceManager.isDebugModeOn) {
            println("${actingEnemy.character.name}: Before moving, AP: ${actingEnemy.currentAP}")
        }

        val currentSpaceIndex: Int = getSpaceIndexOfCurrentParticipant()
        val difference: Int = abs(destinationSpace - currentSpaceIndex) + getPenaltyApForEnemy(destinationSpace)
        actingEnemy.currentAP -= difference

        if (preferenceManager.isDebugModeOn) {
            println("${actingEnemy.character.name}: After moving, AP: ${actingEnemy.currentAP}")
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private fun moveHero(allSpacesInTheChosenDirection: IntProgression) {
        allSpacesInTheChosenDirection
            .firstOrNull { heroSpaces[it] == null }
            ?.let {
                moveHeroToSpace(it)
                playSe(AudioEvent.SE_MENU_CURSOR)
            } ?: playSe(AudioEvent.SE_MENU_ERROR)
    }

    private fun moveEnemyToIndex(destinationSpace: Int) {
        getAllSpacesUntil(destinationSpace)
            .filter { enemySpaces[it] == null }
            .forEach {
                moveEnemyToSpace(it)
                Thread.sleep(500L)
            }
    }

    private fun getAllSpacesUntil(destinationSpace: Int): IntProgression {
        val currentIndex: Int = getSpaceIndexOfCurrentParticipant()
        return if (destinationSpace < currentIndex) {
            currentIndex - 1 downTo maxOf(destinationSpace, 0)
        } else {
            currentIndex + 1..minOf(destinationSpace, BATTLE_FIELD_SIZE)
        }
    }

    private fun moveHeroToSpace(newSpace: Int) {
        val hero: Participant = currentParticipant.invoke()
        heroSpaces[heroSpaces.indexOf(hero)] = null
        heroSpaces[newSpace] = hero
    }

    private fun moveEnemyToSpace(newSpace: Int) {
        val enemy: Participant = currentParticipant.invoke()
        enemySpaces[enemySpaces.indexOf(enemy)] = null
        enemySpaces[newSpace] = enemy
    }

    private fun isActingEnemyNextTo(hero: Participant): Boolean {
        val enemyIndex: Int = getSpaceIndexOfCurrentParticipant()
        val heroIndex: Int = hero.getSpaceIndex()
        return enemyIndex == heroIndex || enemyIndex == heroIndex - 1
    }

    private fun isHeroStartingSpaceNextToEnemy(): Boolean {
        return getEffectiveStartingSpace().isHeroSpaceNextToEnemy()
    }

    private fun isEnemyStartingSpaceNextToHero(destinationSpace: Int): Boolean {
        val effectiveStartingSpace: Int = getEffectiveStartingSpace()
        if (destinationSpace == effectiveStartingSpace) return false
        return effectiveStartingSpace.isEnemySpaceNextToHero()
    }

    private fun getEffectiveStartingSpace(): Int {
        return if (startingSpace == -1) getSpaceIndexOfCurrentParticipant() else startingSpace
    }

    private fun Int.isHeroSpaceNextToEnemy(): Boolean {
        return (this > 0 && enemySpaces[this - 1] != null)
            || enemySpaces[this] != null
    }

    private fun Int.isEnemySpaceNextToHero(): Boolean {
        return heroSpaces[this] != null
            || (this < BATTLE_FIELD_SIZE - 1 && heroSpaces[this + 1] != null)
    }

    private fun isActingHeroNextToAlly(ally: Participant): Boolean {
        val actingHeroIndex: Int = getSpaceIndexOfCurrentParticipant()
        val allyIndex: Int = ally.getSpaceIndex()
        return actingHeroIndex - 1 == allyIndex || actingHeroIndex + 1 == allyIndex
    }

    private fun Participant.getSpaceIndex(): Int {
        return heroSpaces.indexOf(this)
            .takeUnless { it == -1 }
            ?: enemySpaces.indexOf(this)
    }

    private fun Participant.isEnemyInRangeOfActingHero(): Boolean {
        val enemySpace: Int = enemySpaces.indexOf(this)
        val range: List<Int> = getRangeOfActingHero()
        return enemySpace in range
    }

    private fun MutableList<Participant?>.removeDeadParticipants() {
        this.forEachIndexed { index, participant ->
            if (participant?.character?.isAlive == false) {
                this[index] = null
            }
        }
    }

}
