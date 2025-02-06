package nl.t64.cot.components.battle

import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe
import kotlin.math.abs


private const val BATTLE_FIELD_SIZE = 20

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

    fun moveHeroRight(currentHero: Participant) {
        val currentIndex: Int = currentHero.getCurrentSpaceIndex()
        val upperBound: Int = minOf(startingSpace + 1 + currentHero.currentAP, BATTLE_FIELD_SIZE)
        val allSpacesFromHere: IntProgression = currentIndex + 1 until upperBound
        currentHero.moveHero(allSpacesFromHere)
    }

    fun moveHeroLeft(currentHero: Participant) {
        val currentIndex: Int = currentHero.getCurrentSpaceIndex()
        val lowerBound: Int = maxOf(startingSpace - currentHero.currentAP, 0)
        val allSpacesFromHere: IntProgression = currentIndex - 1 downTo lowerBound
        currentHero.moveHero(allSpacesFromHere)
    }

    fun possibleGetHeroTargetAndMoveEnemy(currentEnemy: Participant): Participant? {
        val currentEnemyRangeIndices: List<Int> = currentEnemy.getRangeOfEnemy()
        val nearestHeroIndices: List<Int> = getOccupiedHeroIndicesSortedByNearest(currentEnemyRangeIndices)

        // if hero is already in range, don't move enemy.
        val nearestHeroIndex: Int = nearestHeroIndices.first()
        if (nearestHeroIndex in currentEnemyRangeIndices) {
            return heroSpaces[nearestHeroIndex]
        }

        // if hero is not in range, move enemy to nearest space where hero is in range.
        currentEnemy.getNearestSpaceToMoveToForAnAttack(nearestHeroIndices)
            ?.let { currentEnemy.moveEnemyToIndex(it) }
            // or don't move enemy if no such space is available.
            ?: return null

        // return hero that is now in range.
        val indexOfTargetedHero: Int = nearestHeroIndices.first { it in currentEnemy.getRangeOfEnemy() }
        return heroSpaces[indexOfTargetedHero]
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

    private fun getOccupiedHeroIndicesSortedByNearest(enemyRangeIndices: List<Int>): List<Int> {
        return heroSpaces.filterNotNull()
            .map { hero -> hero.getCurrentSpaceIndex() }
            .sortedBy { heroIndex -> enemyRangeIndices.minOf { rangeIndex -> abs(rangeIndex - heroIndex) } }
    }

    private fun Participant.getNearestSpaceToMoveToForAnAttack(heroIndices: List<Int>): Int? {
        val currentEnemyIndex: Int = this.getCurrentSpaceIndex()
        val enemyWeaponRanges: List<Int> = this.getWeaponRanges()
        return enemySpaces.indices
            .filter { enemySpaces[it] == null }
            .filter { heroIndices.isAnyHeroInWeaponRange(it, enemyWeaponRanges) }
            .minByOrNull { abs(it - currentEnemyIndex) }
    }

    private fun List<Int>.isAnyHeroInWeaponRange(enemyIndex: Int, enemyWeaponRanges: List<Int>): Boolean {
        val heroIndices: List<Int> = this
        return enemyWeaponRanges.any { range ->
            heroIndices.any { heroIndex ->
                enemyIndex - range + 1 == heroIndex || enemyIndex + range == heroIndex
            }
        }
    }

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
