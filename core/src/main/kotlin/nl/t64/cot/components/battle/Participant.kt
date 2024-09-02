package nl.t64.cot.components.battle

import nl.t64.cot.components.party.HeroItem
import nl.t64.cot.components.party.stats.StatItemId


class Participant(
    val character: Character
) {
    val isHero: Boolean get() = character is HeroItem
    var turnCounter: Float = 0f

    val maximumAP: Int = character.getCalculatedActionPoints()
    var currentAP: Int = maximumAP


    fun updateTurnCounter() {
        turnCounter += 10f + character.getCalculatedTotalStatOf(StatItemId.SPEED) //* 0.5f
        // if character  sp == 0 -> speed / 3f?
    }

    fun isTurnCounterAtMax(): Boolean {
        return turnCounter >= 200f
    }

    fun resetTurnCounter() {
        turnCounter -= 200f
    }

    fun refreshActionPoints() {
        currentAP = maximumAP
    }

}
