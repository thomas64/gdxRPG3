package nl.t64.cot.components.battle

import nl.t64.cot.components.party.HeroItem
import nl.t64.cot.components.party.stats.StatItemId
import kotlin.random.Random


class TurnManager(
    private val heroes: List<HeroItem>,
    private val enemies: List<EnemyItem>
) {
    val participants: MutableList<Participant> = createParticipants()
    val currentParticipant: Participant get() = participants.first()

    init {
        increaseAllTurnCounters()
        sortParticipants()
    }

    fun setNextTurn() {
        currentParticipant.resetTurnCounter()
        removeKilledParticipants()
        sortParticipants()
        val newFirst = currentParticipant
        increaseAllTurnCounters()
        sortParticipants()
        participants.remove(newFirst)
        participants.add(0, newFirst)
        newFirst.refreshActionPoints()
    }

    private fun increaseAllTurnCounters() {
        while (true) {
            participants.forEach { it.updateTurnCounter() }
            if (participants.any { it.isTurnCounterAtMax() }) break
        }
    }

    private fun sortParticipants() {
        val comparator = compareByDescending<Participant> { it.turnCounter }.thenBy { Random.nextInt() }
        participants.sortWith(comparator)
    }

    private fun removeKilledParticipants() {
        participants.removeIf { !it.character.isAlive }
    }

    private fun createParticipants(): MutableList<Participant> {
        val heroParticipants = heroes.map { Participant(it) }
        val enemyParticipants = enemies.map { Participant(it) }
        return (heroParticipants + enemyParticipants)
            .sortedByDescending { it.character.getCalculatedTotalStatOf(StatItemId.SPEED) }
            .toMutableList()
    }

}
