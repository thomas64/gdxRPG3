package nl.t64.cot.screens.world

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.MathUtils
import nl.t64.cot.Utils.gameData
import nl.t64.cot.components.time.Clock
import nl.t64.cot.screens.world.mapobjects.AmbientLight


private val KEYFRAMES: List<DaylightKeyframe> = listOf(
    DaylightKeyframe("07:29", AmbientLight.DAWN, 0f),
    DaylightKeyframe("07:45", AmbientLight.DAWN, 0f),
    DaylightKeyframe("09:00", AmbientLight.DAY, 1f),
    DaylightKeyframe("17:00", AmbientLight.DAY, 1f),
    DaylightKeyframe("18:30", AmbientLight.SUNSET, 0f),
    DaylightKeyframe("19:15", AmbientLight.DUSK, 0f),
    DaylightKeyframe("19:31", AmbientLight.DUSK, 0f) // not necessary, because the last keyframe is always used as fallback.
)

private class DaylightKeyframe(val time: String, val light: AmbientLight, val sunlightStrength: Float)

private class DaylightSegment(val from: DaylightKeyframe, val to: DaylightKeyframe, val progress: Float)

object Daylight {

    private val tint: Color = Color()

    fun getTint(): Color {
        val segment: DaylightSegment = getCurrentSegment()
        return tint.set(segment.from.light.color).lerp(segment.to.light.color, segment.progress)
    }

    fun getSunlightStrength(): Float {
        val segment: DaylightSegment = getCurrentSegment()
        return MathUtils.lerp(segment.from.sunlightStrength, segment.to.sunlightStrength, segment.progress)
    }

    private fun getCurrentSegment(): DaylightSegment {
        val clock: Clock = gameData.clock
        return KEYFRAMES.zipWithNext()
            .firstOrNull { (from, to) -> clock.isCurrentTimeInBetween(from.time, to.time) }
            ?.let { (from, to) -> DaylightSegment(from, to, clock.getPercentageOfCurrentTimeBetween(from.time, to.time) / 100f) }
            ?: DaylightSegment(KEYFRAMES.last(), KEYFRAMES.last(), 0f)
    }

}
