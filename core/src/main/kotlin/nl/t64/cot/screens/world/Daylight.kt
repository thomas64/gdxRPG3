package nl.t64.cot.screens.world

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.MathUtils
import nl.t64.cot.screens.world.mapobjects.AmbientLight


private val KEYFRAMES: List<DaylightKeyframe> = listOf(
    DaylightKeyframe("07:29", AmbientLight.DAWN, 0f),
    DaylightKeyframe("07:45", AmbientLight.DAWN, 0f),
    DaylightKeyframe("09:00", AmbientLight.DAY, 1f),
    DaylightKeyframe("17:30", AmbientLight.DAY, 1f),
    DaylightKeyframe("18:30", AmbientLight.SUNSET, 0.5f),
    DaylightKeyframe("18:45", AmbientLight.SUNSET, 0f),
    DaylightKeyframe("19:15", AmbientLight.DUSK, 0f),
    DaylightKeyframe("19:31", AmbientLight.DUSK, 0f) // not necessary, because the last keyframe is always used as fallback.
)

private class DaylightKeyframe(
    override val time: String,
    val light: AmbientLight,
    val sunlightStrength: Float
) : ClockKeyframe

object Daylight {

    private val tint: Color = Color()

    fun getTint(): Color {
        val segment: ClockSegment<DaylightKeyframe> = KEYFRAMES.getCurrentSegment()
        return tint.set(segment.from.light.color).lerp(segment.to.light.color, segment.progress)
    }

    fun getSunlightStrength(): Float {
        val segment: ClockSegment<DaylightKeyframe> = KEYFRAMES.getCurrentSegment()
        return MathUtils.lerp(segment.from.sunlightStrength, segment.to.sunlightStrength, segment.progress)
    }

}
