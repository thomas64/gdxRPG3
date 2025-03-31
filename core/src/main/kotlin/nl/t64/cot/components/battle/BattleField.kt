package nl.t64.cot.components.battle

import nl.t64.cot.Utils.preferenceManager
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe
import kotlin.math.abs


private const val BATTLE_FIELD_SIZE = 20
private const val PENALTY_AP: Int = 2

class BattleField(participants: List<Participant>) {

    val heroSpaces: MutableList<Participant?> = MutableList(BATTLE_FIELD_SIZE) { null }
    val enemySpaces: MutableList<Participant?> = MutableList(BATTLE_FIELD_SIZE) { null }
    var startingSpace: Int = -1

    init {
        val heroParticipants = participants.filter { it.isHero }
        val enemyParticipants = participants.filter { !it.isHero }

        val heroIndices: List<Int> = (0 until 8).shuffled()
        val enemyIndices: List<Int> = (12 until 20).shuffled()

        heroParticipants.forEachIndexed { index, participant -> heroSpaces[heroIndices[index]] = participant }
        enemyParticipants.forEachIndexed { index, participant -> enemySpaces[enemyIndices[index]] = participant }
    }

    fun resetStartingSpace() {
        startingSpace = -1
    }

    fun setStartingSpace(currentParticipant: Participant) {
        startingSpace = currentParticipant.getCurrentSpaceIndex()
    }

    fun cancelMovement(currentHero: Participant) {
        if (startingSpace == -1) return
        currentHero.moveHeroToSpace(startingSpace)
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

    fun repositionHeroRight(currentHero: Participant) {
        val currentIndex: Int = currentHero.getCurrentSpaceIndex()
        val allSpacesFromHere: IntProgression = currentIndex + 1 until 8
        currentHero.moveHero(allSpacesFromHere)
    }

    fun repositionHeroLeft(currentHero: Participant) {
        val currentIndex: Int = currentHero.getCurrentSpaceIndex()
        val allSpacesFromHere: IntProgression = currentIndex - 1 downTo 0
        currentHero.moveHero(allSpacesFromHere)
    }

    fun moveHeroRight(currentHero: Participant) {
        val currentIndex: Int = currentHero.getCurrentSpaceIndex()
        val actionPoints: Int = getModifiedApForHero(currentHero)
        val upperBound: Int = minOf(startingSpace + 1 + actionPoints, BATTLE_FIELD_SIZE)
        val allSpacesFromHere: IntProgression = currentIndex + 1 until upperBound
        currentHero.moveHero(allSpacesFromHere)
    }

    fun moveHeroLeft(currentHero: Participant) {
        val currentIndex: Int = currentHero.getCurrentSpaceIndex()
        val actionPoints: Int = getModifiedApForHero(currentHero)
        val lowerBound: Int = maxOf(startingSpace - actionPoints, 0)
        val allSpacesFromHere: IntProgression = currentIndex - 1 downTo lowerBound
        currentHero.moveHero(allSpacesFromHere)
    }

    fun getModifiedApForHero(participant: Participant): Int {
        return participant.currentAP - getPenaltyApForHero()
    }

    private fun getModifiedApForEnemy(participant: Participant, destinationSpace: Int?): Int {
        return participant.currentAP - getPenaltyApForEnemy(destinationSpace)
    }

    fun getPenaltyApForHero(): Int {
        return if (isHeroStartingSpaceNextToEnemy()) PENALTY_AP else 0
    }

    private fun getPenaltyApForEnemy(destinationSpace: Int?): Int {
        if (destinationSpace == startingSpace) return 0
        return if (isEnemyStartingSpaceNextToHero()) PENALTY_AP else 0
    }

    fun getCurrentSpace(participant: Participant): Int {
        return heroSpaces.indexOf(participant)
            .takeUnless { it == -1 }
            ?: enemySpaces.indexOf(participant)
    }

    fun getTargetableEnemiesFor(currentHero: Participant): List<Participant> {
        return enemySpaces.filterNotNull()
            .filter { it.isInRangeOfHero(currentHero) }
    }

    fun getRangeOfHero(currentHero: Participant): List<Int> {
        return currentHero.getRange(0, -1)
    }

    private fun Participant.getRangeOfEnemy(): List<Int> {
        return this.getRange(1, 0)
    }

    private fun Participant.getRange(offsetLeft: Int, offSetRight: Int): List<Int> {
        val currentSpace: Int = this.getCurrentSpaceIndex()
        return this.getWeaponRanges()
            .map { listOf(currentSpace - it + offsetLeft, currentSpace + it + offSetRight) }
            .flatten()
            .filter { it in 0..BATTLE_FIELD_SIZE }
            .distinct()
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    fun possibleGetHeroTargetAndMoveEnemy(currentEnemy: Participant): Participant? {
        setStartingSpace(currentEnemy)
        val heroIndicesByPrio: List<Int> = getOccupiedHeroIndicesSortedByPriorityFor(currentEnemy)
        if (preferenceManager.isInDebugMode) {
            println("heroIndicesByPrio: $heroIndicesByPrio")
        }

        // if hero is already in range, don't move enemy.
        val currentEnemyRangeIndices: List<Int> = currentEnemy.getRangeOfEnemy()
        val highestPrioHero: Int = heroIndicesByPrio.first()
        if (highestPrioHero in currentEnemyRangeIndices) {
            return heroSpaces[highestPrioHero]
        }

        // move enemy to where its range is closest to its highest prio hero which it can get with its AP.
        currentEnemy.getMostPrioSpaceToMoveToForAnAttack(heroIndicesByPrio)?.let {
            currentEnemy.takeApForMovingTo(it)
            currentEnemy.moveEnemyToIndex(it)
        } ?: return null // or don't move enemy if no such space is available.

        return heroIndicesByPrio
            .firstOrNull { it in currentEnemy.getRangeOfEnemy() } // take the first hero that is now in range.
            ?.let { heroSpaces[it] }                    // or null when the AP was not enough to reach the hero.
    }

    private fun getOccupiedHeroIndicesSortedByPriorityFor(currentEnemy: Participant): List<Int> {
        return heroSpaces.filterNotNull()
            .sortedBy { hero -> hero.getPriorityFor(currentEnemy) }
            .map { hero -> hero.getCurrentSpaceIndex() }
    }

    private fun Participant.getMostPrioSpaceToMoveToForAnAttack(heroIndicesByPrio: List<Int>): Int? {
        return this.getAllEnemySpacesFromWhereToAttackByPrio(heroIndicesByPrio)
            .onEach { if (this.isDestinationInRangeOfAP(it)) return it }
            .firstOrNull()
            ?.let { this.findFarthestReachableSpaceTo(it) }
            ?: this.getNextBestNearestEmptySpace(heroIndicesByPrio)
    }

    private fun Participant.getAllEnemySpacesFromWhereToAttackByPrio(heroIndicesByPrio: List<Int>): List<Int> {
        val currentEnemyIndex: Int = this.getCurrentSpaceIndex()
        val enemyWeaponRanges: List<Int> = this.getWeaponRanges()

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

    private fun Participant.isDestinationInRangeOfAP(destinationSpace: Int): Boolean {
        val actionPoints: Int = getModifiedApForEnemy(this, destinationSpace)
        val currentEnemyIndex: Int = this.getCurrentSpaceIndex()
        return abs(destinationSpace - currentEnemyIndex) <= actionPoints
    }

    private fun Participant.findFarthestReachableSpaceTo(destinationSpace: Int): Int? {
        val currentEnemyIndex: Int = this.getCurrentSpaceIndex()
        val direction: Int = if (destinationSpace > currentEnemyIndex) 1 else -1
        val actionPoints: Int = getModifiedApForEnemy(this, destinationSpace)
        return (1..actionPoints)
            .map { currentEnemyIndex + it * direction }
            .lastOrNull { enemySpaces[it] == null }
    }

    private fun Participant.getNextBestNearestEmptySpace(heroIndicesByPrio: List<Int>): Int? {
        val currentEnemyIndex: Int = this.getCurrentSpaceIndex()
        return heroIndicesByPrio
            .flatMap { listOf(it - 1, it) }
            .map { destinationSpace ->
                enemySpaces.indices
                    .filter { it == currentEnemyIndex || enemySpaces[it] == null }
                    .minBy { abs(it - destinationSpace) }
            }
            .minBy { abs(it - currentEnemyIndex) }
            .takeIf { it != currentEnemyIndex }
    }

    private fun Participant.takeApForMovingTo(destinationSpace: Int) {
        if (preferenceManager.isInDebugMode) {
            println("${this.character.name} AP: ${this.currentAP}")
        }

        val difference: Int = abs(destinationSpace - this.getCurrentSpaceIndex()) + getPenaltyApForEnemy(destinationSpace)
        this.currentAP -= difference

        if (preferenceManager.isInDebugMode) {
            println("${this.character.name} AP: ${this.currentAP}")
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private fun Participant.moveHero(allSpacesInTheChosenDirection: IntProgression) {
        allSpacesInTheChosenDirection
            .firstOrNull { heroSpaces[it] == null }
            ?.let {
                this.moveHeroToSpace(it)
                playSe(AudioEvent.SE_MENU_CURSOR)
            } ?: playSe(AudioEvent.SE_MENU_ERROR)
    }

    private fun Participant.moveEnemyToIndex(destinationSpace: Int) {
        this.getAllSpacesUntil(destinationSpace)
            .filter { enemySpaces[it] == null }
            .forEach {
                this.moveEnemyToSpace(it)
                Thread.sleep(500L)
            }
    }

    private fun Participant.getAllSpacesUntil(destinationSpace: Int): IntProgression {
        val currentIndex: Int = this.getCurrentSpaceIndex()
        return if (destinationSpace < currentIndex) {
            currentIndex - 1 downTo maxOf(destinationSpace, 0)
        } else {
            currentIndex + 1..minOf(destinationSpace, BATTLE_FIELD_SIZE)
        }
    }

    private fun Participant.moveHeroToSpace(newSpace: Int) {
        heroSpaces[heroSpaces.indexOf(this)] = null
        heroSpaces[newSpace] = this
    }

    private fun Participant.moveEnemyToSpace(newSpace: Int) {
        enemySpaces[enemySpaces.indexOf(this)] = null
        enemySpaces[newSpace] = this
    }

    private fun Participant.getCurrentSpaceIndex(): Int {
        return getCurrentSpace(this)
    }

    private fun isHeroStartingSpaceNextToEnemy(): Boolean {
        if (startingSpace == -1) return false
        return (startingSpace > 0 && enemySpaces[startingSpace - 1] != null)
            || enemySpaces[startingSpace] != null
    }

    private fun isEnemyStartingSpaceNextToHero(): Boolean {
        if (startingSpace == -1) return false
        return heroSpaces[startingSpace] != null
            || (startingSpace < BATTLE_FIELD_SIZE - 1 && heroSpaces[startingSpace + 1] != null)
    }

    private fun Participant.isInRangeOfHero(currentHero: Participant): Boolean {
        val enemySpace: Int = enemySpaces.indexOf(this)
        val range: List<Int> = getRangeOfHero(currentHero)
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
