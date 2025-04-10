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
        removeKilledParticipants()
        if (participants.size == 1) return
        val nextInLine = participants[1]
        currentParticipant.resetTurnCounter()
        increaseAllTurnCounters()
        sortParticipants()
        nextInLine.moveToTop()
        nextInLine.refreshActionPoints()
    }

    fun removeKilledParticipants() {
        participants.removeIf { it.character.isDead }
    }

    fun delayTurn() {
        currentParticipant.delayTurn()
        participants.swap(0, 1)
        currentParticipant.refreshActionPoints()
    }

    fun stagger(character: Character) {
        val target: Participant = participants.first { it.character == character }
        target.currentAP = 0
        if (participants.size == 2) {
            target.stagger()
        } else if (target == participants.last()) {
            target.setNegativeTurnCounter()
        } else {
            target.turnCounter = 0
            target.moveToBottom()
        }
    }

    private fun increaseAllTurnCounters() {
        while (true) {
            if (participants.any { it.isTurnCounterAtMax() }) break
            participants.forEach { it.updateTurnCounter() }
        }
    }

    private fun sortParticipants() {
        val comparator = compareByDescending<Participant> { it.turnCounter }.thenBy { Random.nextInt() }
        participants.sortWith(comparator)
    }

    private fun Participant.moveToTop() {
        participants.remove(this)
        participants.add(0, this)
    }

    private fun Participant.moveToBottom() {
        participants.remove(this)
        participants.add(this)
    }

    private fun createParticipants(): MutableList<Participant> {
        val heroParticipants = heroes.map { Participant(it) }
        val enemyParticipants = enemies.map { Participant(it) }
        return (heroParticipants + enemyParticipants)
            .sortedByDescending { it.character.getCalculatedTotalStatOf(StatItemId.SPEED) }
            .toMutableList()
    }

    private fun MutableList<Participant>.swap(index1: Int, index2: Int) {
        val temp = this[index1]
        this[index1] = this[index2]
        this[index2] = temp
    }

}
