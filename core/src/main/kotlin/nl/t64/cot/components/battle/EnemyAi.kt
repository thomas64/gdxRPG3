package nl.t64.cot.components.battle

import nl.t64.cot.Utils.preferenceManager
import kotlin.math.abs


class EnemyAi(
    private val grid: BattleGrid,
    private val currentParticipant: () -> Participant
) {
    private val actingEnemy: Participant get() = currentParticipant.invoke()
    private val actingEnemyIndex: Int get() = grid.getSpaceIndexOf(actingEnemy)

    fun possibleSwitchWeaponOfActingEnemy() {
        if (!actingEnemy.canSwitchWeapon()) return

        if (actingEnemy.hasRangedWeapon()) {
            possibleSwitchToMelee()
        } else {
            possibleSwitchToRanged()
        }
    }

    fun possibleGetHeroTargetAndMove(): Participant? {
        grid.setStartingSpace(actingEnemyIndex)
        val heroIndicesByPrio: List<Int> = getOccupiedHeroIndicesSortedByPriorityForActingEnemy()
        if (preferenceManager.isDebugModeOn) {
            println("${actingEnemy.character.name}: At start of turn, AP: ${actingEnemy.currentAP}")
            println("heroIndicesByPrio: $heroIndicesByPrio")
        }

        return if (actingEnemy.hasRangedWeapon()) {
            heroIndicesByPrio.getRangedHeroTargetAndMove()
        } else {
            heroIndicesByPrio.getMeleeHeroTargetAndMove()
        }
    }

    private fun List<Int>.getMeleeHeroTargetAndMove(): Participant? {
        val heroIndicesByPrio: List<Int> = this
        val highestPrioHero: Int = heroIndicesByPrio.first()
        if (highestPrioHero in getWeaponRangeOfActingEnemy()) {
            return grid.heroSpaces[highestPrioHero]   // already in range: attack without moving.
        }

        val destination: Int = getBestMeleeDestinationFrom(heroIndicesByPrio) ?: return null
        moveEnemyTo(destination)
        return getHighestPrioHeroInRangeFrom(heroIndicesByPrio)
    }

    private fun moveEnemyTo(destination: Int) {
        takeApForMovingTo(destination)
        moveEnemyToIndex(destination)
    }

    private fun possibleSwitchToMelee() {
        val canReachPositionForRangedAttack: Boolean =
            canReachAttackPosition(actingEnemy.getWeaponRanges(),
                                   actingEnemy.currentAP - actingEnemy.getCheapestAbilityAp(),
                                   mustBeOutOfBattleLock = true)
        val canReachPositionForMeleeAttackAfterSwitch: Boolean =
            canReachAttackPosition(actingEnemy.getStashedWeaponRanges(),
                                   actingEnemy.currentAP - SWITCH_WEAPON_AP,
                                   mustBeOutOfBattleLock = false)
        if (canReachPositionForMeleeAttackAfterSwitch
            && (isThreatenedByMeleeReachOfHero() || !canReachPositionForRangedAttack)) {
            actingEnemy.switchWeapon()
        }
    }

    private fun possibleSwitchToRanged() {
        val canReachPositionForRangedAttackAfterSwitch: Boolean =
            canReachAttackPosition(actingEnemy.getStashedWeaponRanges(),
                                   actingEnemy.currentAP - SWITCH_WEAPON_AP,
                                   mustBeOutOfBattleLock = true)
        if (!isThreatenedByMeleeReachOfHero() && canReachPositionForRangedAttackAfterSwitch) {
            actingEnemy.switchWeapon()
        }
    }

    private fun isThreatenedByMeleeReachOfHero(): Boolean {
        return grid.heroSpaces.withIndex().any { (heroIndex, hero) ->
            val heroRanges: List<Int> = hero?.getWeaponRanges().orEmpty()
            heroRanges.contains(1) && heroRanges.any { isThreatenedByHeroAt(heroIndex, it) }
        }
    }

    private fun isThreatenedByHeroAt(heroIndex: Int, heroRange: Int): Boolean {
        return actingEnemyIndex == heroIndex - heroRange || actingEnemyIndex == heroIndex + heroRange - 1
    }

    private fun canReachAttackPosition(weaponRanges: List<Int>,
                                       apBudget: Int,
                                       mustBeOutOfBattleLock: Boolean): Boolean {
        val heroIndices: List<Int> = grid.heroSpaces.indices.filter { grid.heroSpaces[it] != null }
        return grid.enemySpaces.indices
            .filter { it.isVacantOrOwnSpace() }
            .filter { getMoveCostTo(it) <= apBudget }
            .filterNot { mustBeOutOfBattleLock && grid.isEnemySpaceNextToHero(it) }
            .any { space -> heroIndices.any { it.isHeroInWeaponRangeFrom(space, weaponRanges) } }
    }

    private fun getOccupiedHeroIndicesSortedByPriorityForActingEnemy(): List<Int> {
        return grid.heroSpaces.filterNotNull()
            .sortedBy { hero -> hero.getPriorityFor(actingEnemy, isActingEnemyNextTo(hero)) }
            .map { hero -> grid.getSpaceIndexOf(hero) }
    }

    private fun List<Int>.getRangedHeroTargetAndMove(): Participant? {
        val heroIndicesByPrio: List<Int> = this
        val destination: Int = getBestRangedDestinationFrom(heroIndicesByPrio) ?: return null
        if (destination != actingEnemyIndex) {
            moveEnemyTo(destination)
        }
        return getHighestPrioHeroInRangeFrom(heroIndicesByPrio)
    }

    // an archer goes for its highest-prio target that it can still both reach and shoot this turn,
    // but only from a spot where no hero can lock it in melee; only when that is not reachable-with-a-shot
    // does it drop to a nearer target, instead of crossing the field and ending the turn without firing.
    private fun getBestRangedDestinationFrom(heroIndicesByPrio: List<Int>): Int? {
        val budgetForMovingAndShooting: Int = actingEnemy.currentAP - actingEnemy.getCheapestAbilityAp()
        return getAllIndicesFromWhereToAttackByPrio(heroIndicesByPrio, shouldAvoidBattleLock = true)
            .firstOrNull { getMoveCostTo(it) <= budgetForMovingAndShooting }
            ?: getNextBestNearestEmptySpace(heroIndicesByPrio, shouldAvoidBattleLock = true)
    }

    private fun getBestMeleeDestinationFrom(heroIndicesByPrio: List<Int>): Int? {
        return getAllIndicesFromWhereToAttackByPrio(heroIndicesByPrio, shouldAvoidBattleLock = false)
            .onEach { if (it.isReachableThisTurn()) return it }
            .firstOrNull()
            ?.let { findFarthestReachableSpaceTo(it) }
            ?: getNextBestNearestEmptySpace(heroIndicesByPrio, shouldAvoidBattleLock = false)
    }

    private fun getAllIndicesFromWhereToAttackByPrio(heroIndicesByPrio: List<Int>,
                                                     shouldAvoidBattleLock: Boolean): List<Int> {
        val enemyWeaponRanges: List<Int> = actingEnemy.getWeaponRanges()
        return grid.enemySpaces.indices
            .filter { it.isVacantOrOwnSpace() }
            .filterNot { shouldAvoidBattleLock && grid.isEnemySpaceNextToHero(it) }
            .flatMap { it.getHeroIndicesInRange(heroIndicesByPrio, enemyWeaponRanges) }
            .groupBy({ it.first }, { it.second })
            .toSortedMap()
            .map { (_, destinationSpaces) -> destinationSpaces.minBy { abs(it - actingEnemyIndex) } }
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

    private fun findFarthestReachableSpaceTo(destinationIndex: Int): Int? {
        val direction: Int = if (destinationIndex > actingEnemyIndex) 1 else -1
        val actionPoints: Int = getApWithPenaltyTo(destinationIndex)
        return (1..actionPoints)
            .map { actingEnemyIndex + it * direction }
            .lastOrNull { grid.enemySpaces[it] == null }
    }

    private fun getNextBestNearestEmptySpace(heroIndicesByPrio: List<Int>,
                                             shouldAvoidBattleLock: Boolean): Int? {
        return heroIndicesByPrio
            .flatMap { listOf(it - 1, it) }
            .mapNotNull { findNearestReachableSpaceTo(it, shouldAvoidBattleLock) }
            .minByOrNull { abs(it - actingEnemyIndex) }
            ?.takeIf { it != actingEnemyIndex }
    }

    private fun findNearestReachableSpaceTo(targetIndex: Int, shouldAvoidBattleLock: Boolean): Int? {
        return grid.enemySpaces.indices
            .filter { it.isVacantOrOwnSpace() }
            .filter { it.isReachableThisTurn() }
            .filterNot { shouldAvoidBattleLock && grid.isEnemySpaceNextToHero(it) }
            .minByOrNull { abs(it - targetIndex) }
    }

    private fun Int.isVacantOrOwnSpace(): Boolean {
        return this == actingEnemyIndex || grid.enemySpaces[this] == null
    }

    private fun Int.isReachableThisTurn(): Boolean {
        return getMoveCostTo(this) <= actingEnemy.currentAP
    }

    private fun takeApForMovingTo(destinationSpace: Int) {
        if (preferenceManager.isDebugModeOn) {
            println("${actingEnemy.character.name}: Before moving, AP: ${actingEnemy.currentAP}")
        }

        actingEnemy.currentAP -= getMoveCostTo(destinationSpace)

        if (preferenceManager.isDebugModeOn) {
            println("${actingEnemy.character.name}: After moving, AP: ${actingEnemy.currentAP}")
        }
    }

    private fun moveEnemyToIndex(destinationSpace: Int) {
        getAllSpacesUntil(destinationSpace)
            .filter { grid.enemySpaces[it] == null }
            .forEach {
                grid.moveEnemyToSpace(actingEnemy, it)
                Thread.sleep(500L)
            }
    }

    private fun getAllSpacesUntil(destinationSpace: Int): IntProgression {
        return if (destinationSpace < actingEnemyIndex) {
            actingEnemyIndex - 1 downTo maxOf(destinationSpace, 0)
        } else {
            actingEnemyIndex + 1..minOf(destinationSpace, BATTLE_FIELD_SIZE)
        }
    }

    private fun isActingEnemyNextTo(hero: Participant): Boolean {
        val heroIndex: Int = grid.getSpaceIndexOf(hero)
        return actingEnemyIndex == heroIndex || actingEnemyIndex == heroIndex - 1
    }

    private fun getHighestPrioHeroInRangeFrom(heroIndicesByPrio: List<Int>): Participant? {
        return heroIndicesByPrio
            .firstOrNull { it in getWeaponRangeOfActingEnemy() }
            ?.let { grid.heroSpaces[it] }
    }

    private fun getWeaponRangeOfActingEnemy(): List<Int> {
        val ranges: List<Int> = actingEnemy.getWeaponRanges()
        return grid.getRange(actingEnemyIndex, 1, 0, ranges)
    }

    private fun getMoveCostTo(index: Int): Int {
        return abs(index - actingEnemyIndex) + getPenaltyAp(index)
    }

    private fun getApWithPenaltyTo(destinationSpace: Int): Int {
        return actingEnemy.currentAP - getPenaltyAp(destinationSpace)
    }

    private fun getPenaltyAp(destinationSpace: Int): Int {
        return if (isEnemyStartingSpaceNextToHero(destinationSpace)) actingEnemy.getPenaltyAp() else 0
    }

    private fun isEnemyStartingSpaceNextToHero(destinationSpace: Int): Boolean {
        val effectiveStartingSpace: Int = grid.getEffectiveStartingSpace(actingEnemyIndex)
        if (destinationSpace == effectiveStartingSpace) return false
        return grid.isEnemySpaceNextToHero(effectiveStartingSpace)
    }

}
