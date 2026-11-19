package nl.t64.cot.screens.world.atmosphere

import nl.t64.cot.Utils.gameData
import nl.t64.cot.components.time.Clock


interface ClockKeyframe {
    val time: String
}

class ClockSegment<K : ClockKeyframe>(val from: K, val to: K, val progress: Float)

/**
 * Het tijdvak tussen twee opeenvolgende keyframes waar de klok nu in zit, met hoe ver hij daarin is (0 tot 1).
 * Na het laatste keyframe blijft het laatste keyframe gelden.
 */
fun <K : ClockKeyframe> List<K>.getCurrentSegment(): ClockSegment<K> {
    val clock: Clock = gameData.clock
    return this.zipWithNext()
        .firstOrNull { (from, to) -> clock.isCurrentTimeInBetween(from.time, to.time) }
        ?.let { (from, to) -> ClockSegment(from, to, clock.getPercentageOfCurrentTimeBetween(from.time, to.time) / 100f) }
        ?: ClockSegment(this.last(), this.last(), 0f)
}
