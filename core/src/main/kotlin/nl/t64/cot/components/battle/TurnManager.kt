package nl.t64.cot.components.battle

import nl.t64.cot.components.party.HeroItem


class TurnManager(
    private val heroes: List<HeroItem>,
    private val enemies: List<EnemyItem>
) {
    val participants: MutableList<Participant> = createParticipants()
    val currentParticipant: Participant get() = participants.first()
    val troubadourEffects = TroubadourEffectHandler(participants)

    init {
        participants.increaseTurnCounters()
        participants.sort()
    }

    private class Sim(
        val participant: Participant,
        var counter: Int,
        val rate: Int
    )

    fun getOnlyAllies(): List<Participant> {
        return getOnlyHeroes().filterNot { it == currentParticipant }
    }

    fun getOnlyHeroes(): List<Participant> {
        return participants.filter { it.isHero }
    }

    fun getOnlyEnemies(): List<Participant> {
        return participants.filterNot { it.isHero }
    }

    fun simulateForecast(count: Int): List<Participant> {
        val sim: MutableList<Sim> = participants
            .map { Sim(it, it.turnCounter, it.tickRate) }
            .toMutableList()
        val forecast: MutableList<Participant> = mutableListOf()

        forecast.add(sim.first().participant)
        repeat(count - 1) {
            advanceSim(sim)
            forecast.add(sim.first().participant)
        }
        return forecast
    }

    // Mirrors the turn-order logic of setNextTurn (reset first, tick, sort) on throwaway copies.
    // Keep in sync with setNextTurn, otherwise the forecast no longer matches the real order.
    private fun advanceSim(sim: MutableList<Sim>) {
        if (sim.size == 1) return

        sim[0].counter -= TURN_THRESHOLD
        while (sim.none { it.counter >= TURN_THRESHOLD }) {
            sim.forEach { it.counter += it.rate }
        }
        sim.sortWith(compareByDescending<Sim> { it.counter }.thenBy { it.participant.character.name })
    }

    // Turn-order logic (reset acted participant, tick, sort) is mirrored by advanceSim for the forecast.
    // Keep both in sync.
    fun setNextTurn() {
        val actedParticipant: Participant = currentParticipant
        removeKilledParticipants()
        if (participants.size == 1) return
        actedParticipant.resetTurnCounter()
        participants.increaseTurnCounters()
        participants.sort()
        currentParticipant.refreshActionPoints()
        troubadourEffects.possibleApply()
    }

    fun removeKilledParticipants() {
        val deadHeroes = getOnlyHeroes().filter { it.character.isDead }
        if (deadHeroes.any { it.isPerforming }) troubadourEffects.removeFromAllParticipants()
        deadHeroes.forEach { it.resetAllTemporaryBattleEffects() }
        participants.removeIf { it.character.isDead }
    }

    fun delayTurn() {
        val delayingParticipant: Participant = currentParticipant
        val others: List<Participant> = participants.filterNot { it == delayingParticipant }
        delayingParticipant.delayTurn()
        others.increaseTurnCounters()
        delayingParticipant.raiseTurnCounterAboveMaxOf(others)
        participants.sort()
        participants.swapFirstAndSecond()
        currentParticipant.refreshActionPoints()
    }

    fun stagger(target: Participant) {
        target.staggerChance /= 2f
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

    fun getParticipant(name: String): Participant {
        return participants.first { it.character.name == name }
    }

    fun getCurrentApOf(character: Character): Int {
        return participants.firstOrNull { it.character == character }?.currentAP ?: 0
    }

    fun resetTemporaryBonusesAfterBattle() {
        getOnlyHeroes().forEach { it.resetAllTemporaryBattleEffects() }
    }

    private fun Participant.raiseTurnCounterAboveMaxOf(group: List<Participant>) {
        this.turnCounter = this.turnCounter.coerceAtLeast(group.maxOf { it.turnCounter } + 1)
    }

    private fun List<Participant>.increaseTurnCounters() {
        while (this.none { it.isTurnCounterAtMax() }) {
            this.forEach { it.updateTurnCounter() }
        }
    }

    private fun MutableList<Participant>.sort() {
        val comparator: Comparator<Participant> = compareByDescending<Participant> { it.turnCounter }
            .thenBy { it.character.name }
        this.sortWith(comparator)
    }

    private fun Participant.moveToBottom() {
        participants.remove(this)
        participants.add(this)
    }

    private fun createParticipants(): MutableList<Participant> {
        val heroParticipants = heroes.map { Participant(it) }
        val enemyParticipants = enemies.map { Participant(it) }
        return (heroParticipants + enemyParticipants).toMutableList()
    }

    private fun MutableList<Participant>.swapFirstAndSecond() {
        val first = 0
        val second = 1
        val temp = this[first]
        this[first] = this[second]
        this[second] = temp
    }

}
