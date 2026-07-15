package nl.t64.cot.components.battle

import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe
import kotlin.math.abs


class BattleField(
    participants: List<Participant>,
    private val currentParticipant: () -> Participant
) {

    private val grid: BattleGrid = BattleGrid(participants)
    private val enemyAi: EnemyAi = EnemyAi(grid, currentParticipant)

    val heroSpaces: MutableList<Participant?> get() = grid.heroSpaces
    val enemySpaces: MutableList<Participant?> get() = grid.enemySpaces
    val startingSpace: Int get() = grid.startingSpace

    fun resetStartingSpace() {
        grid.resetStartingSpace()
    }

    fun setStartingSpace() {
        grid.setStartingSpace(getSpaceIndexOfCurrentParticipant())
    }

    fun cancelMovement() {
        if (grid.startingSpace == -1) return
        grid.moveHeroToSpace(currentParticipant.invoke(), grid.startingSpace)
    }

    fun removeDeadParticipants() {
        grid.removeDeadParticipants()
    }

    fun createEnemyCountMap(): Map<String, Int> {
        return enemySpaces.filterNotNull()
            .groupingBy { it.character.id }
            .eachCount()
    }

    fun isParticipantNextToOpponent(participant: Participant): Boolean {
        val spaceIndex: Int = grid.getSpaceIndexOf(participant)
        return if (participant.isHero) {
            grid.isHeroSpaceNextToEnemy(spaceIndex)
        } else {
            grid.isEnemySpaceNextToHero(spaceIndex)
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

    fun getPenaltyApForHero(): Int {
        return if (isHeroStartingSpaceNextToEnemy()) currentParticipant.invoke().getPenaltyAp() else 0
    }

    fun getSpaceIndexOfCurrentParticipant(): Int {
        return grid.getSpaceIndexOf(currentParticipant.invoke())
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

    fun getTargetableEnemiesInRangeOfActingHero(maxRange: Int): List<Participant> {
        val range: List<Int> = grid.getRange(getSpaceIndexOfCurrentParticipant(), 0, -1, (1..maxRange).toList())
        return enemySpaces.filterNotNull()
            .filter { enemySpaces.indexOf(it) in range }
    }

    fun getTargetableAlliesInRangeOfActingHero(range: Int): List<Participant> {
        val casterIndex: Int = getSpaceIndexOfCurrentParticipant()
        return heroSpaces.filterNotNull()
            .filter { it != currentParticipant.invoke() }
            .filter { abs(heroSpaces.indexOf(it) - casterIndex) <= range }
    }

    fun getWeaponRangeOfActingHero(): List<Int> {
        val ranges: List<Int> = currentParticipant.invoke().getWeaponRanges()
        return grid.getRange(getSpaceIndexOfCurrentParticipant(), 0, -1, ranges)
    }

    fun getWeaponRangeOfActingEnemy(): List<Int> {
        return enemyAi.getWeaponRange()
    }

    fun possibleSwitchWeaponOfActingEnemy() {
        enemyAi.possibleSwitchWeapon()
    }

    fun possibleGetHeroTargetAndMoveEnemy(): Participant? {
        return enemyAi.possibleGetHeroTargetAndMove()
    }

    private fun moveHero(allSpacesInTheChosenDirection: IntProgression) {
        allSpacesInTheChosenDirection
            .firstOrNull { heroSpaces[it] == null }
            ?.let {
                grid.moveHeroToSpace(currentParticipant.invoke(), it)
                playSe(AudioEvent.SE_MENU_CURSOR)
            } ?: playSe(AudioEvent.SE_MENU_ERROR)
    }

    private fun isHeroStartingSpaceNextToEnemy(): Boolean {
        val actingHeroIndex: Int = getSpaceIndexOfCurrentParticipant()
        val effectiveStartingSpace: Int = grid.getEffectiveStartingSpace(actingHeroIndex)
        return grid.isHeroSpaceNextToEnemy(effectiveStartingSpace)
    }

    private fun isActingHeroNextToAlly(ally: Participant): Boolean {
        val actingHeroIndex: Int = getSpaceIndexOfCurrentParticipant()
        val allyIndex: Int = grid.getSpaceIndexOf(ally)
        return actingHeroIndex - 1 == allyIndex || actingHeroIndex + 1 == allyIndex
    }

    private fun Participant.isEnemyInRangeOfActingHero(): Boolean {
        val enemySpace: Int = enemySpaces.indexOf(this)
        val range: List<Int> = getWeaponRangeOfActingHero()
        return enemySpace in range
    }

}
