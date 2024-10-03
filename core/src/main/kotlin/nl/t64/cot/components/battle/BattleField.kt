package nl.t64.cot.components.battle

import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe
import nl.t64.cot.components.party.inventory.InventoryGroup
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
        startingSpace = getCurrentSpace(currentParticipant)
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
        val currentIndex: Int = getCurrentSpace(currentHero)
        val upperBound: Int = minOf(startingSpace + 1 + currentHero.currentAP, 20)
        (currentIndex + 1 until upperBound)
            .firstOrNull { heroSpaces[it] == null }
            ?.let {
                currentHero.moveHeroToSpace(it)
                playSe(AudioEvent.SE_MENU_CURSOR)
            } ?: playSe(AudioEvent.SE_MENU_ERROR)
    }

    fun moveHeroLeft(currentHero: Participant) {
        val currentIndex: Int = getCurrentSpace(currentHero)
        val lowerBound: Int = maxOf(startingSpace - currentHero.currentAP, 0)
        (currentIndex - 1 downTo lowerBound)
            .firstOrNull { heroSpaces[it] == null }
            ?.let {
                currentHero.moveHeroToSpace(it)
                playSe(AudioEvent.SE_MENU_CURSOR)
            } ?: playSe(AudioEvent.SE_MENU_ERROR)
    }

    fun moveEnemyAndPossibleHeroTargetInRange(currentEnemy: Participant): Participant? {
        val currentEnemyAttackPoints: List<Int> = currentEnemy.getRangeOfEnemy()
        val nearestHeroSpace: Int = getNearestHeroIndexFrom(currentEnemyAttackPoints)
        if (nearestHeroSpace in currentEnemyAttackPoints) {
            return heroSpaces[nearestHeroSpace]
        }

        val currentEnemySpace: Int = getCurrentSpace(currentEnemy)
        val ranges: List<Int> = currentEnemy.character.getInventoryItem(InventoryGroup.WEAPON)?.getWeaponRange().orEmpty()
        val nearestSpaceToAttack: Int = enemySpaces.indices
            .filter { enemySpaces[it] == null }
            .filter { ranges.any { range -> it - range + 1 == nearestHeroSpace || it + range == nearestHeroSpace } }
            .minByOrNull { abs(it - currentEnemySpace) }
            ?: return null // todo: get second closest hero

        val direction: Int = if (currentEnemySpace > nearestSpaceToAttack) -1 else 1
        var newSpace: Int = currentEnemySpace + direction
        while (newSpace in 0 until BATTLE_FIELD_SIZE) {
            if (enemySpaces[newSpace] == null) {
                currentEnemy.moveEnemyToSpace(newSpace)
                newSpace += direction
                Thread.sleep(500L)
                val updatedAttackPoints: List<Int> = currentEnemy.getRangeOfEnemy()
                if (nearestHeroSpace in updatedAttackPoints) {
                    break
                }
            } else {
                newSpace += direction
            }
        }

        return heroSpaces[nearestHeroSpace]
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
        val currentSpace: Int = getCurrentSpace(this)
        return this.character.getInventoryItem(InventoryGroup.WEAPON)
            ?.getWeaponRange()
            ?.map { listOf(currentSpace - it + offsetLeft, currentSpace + it + offSetRight) }
            ?.flatten()
            ?.filter { it in 0..BATTLE_FIELD_SIZE }
            ?.distinct()
            .orEmpty()
    }

    private fun getNearestHeroIndexFrom(attackPoints: List<Int>): Int {
        return heroSpaces.filterNotNull()
            .map { getCurrentSpace(it) }
            .minBy { heroSpace -> attackPoints.minOf { attackPoint -> abs(attackPoint - heroSpace) } }
    }

    private fun Participant.moveHeroToSpace(newSpace: Int) {
        heroSpaces[heroSpaces.indexOf(this)] = null
        heroSpaces[newSpace] = this
    }

    private fun Participant.moveEnemyToSpace(newSpace: Int) {
        enemySpaces[enemySpaces.indexOf(this)] = null
        enemySpaces[newSpace] = this
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
