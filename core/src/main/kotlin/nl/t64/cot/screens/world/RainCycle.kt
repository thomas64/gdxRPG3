package nl.t64.cot.screens.world

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.MathUtils
import nl.t64.cot.screens.world.mapobjects.AmbientLight


private const val RAINING_THRESHOLD = 0.5f

private val KEYFRAMES: List<RainKeyframe> = listOf(
    RainKeyframe("12:50", 0f),
    RainKeyframe("13:00", 1f),
    RainKeyframe("14:00", 1f),
    RainKeyframe("14:10", 0f)
)

private class RainKeyframe(
    override val time: String,
    val strength: Float
) : ClockKeyframe

object RainCycle {

    private val overcastTint: Color = Color()

    /** Vanaf halverwege het aanzwellen tot halverwege het wegebben, zodat niemand over regen praat terwijl het nog droog is. */
    fun isRaining(): Boolean {
        return getRainStrength() >= RAINING_THRESHOLD
    }

    fun getRainStrength(): Float {
        val segment: ClockSegment<RainKeyframe> = KEYFRAMES.getCurrentSegment()
        return MathUtils.lerp(segment.from.strength, segment.to.strength, segment.progress)
    }

    /** Wit zonder regen, naar de RAIN-kleur bij volle regen. Bedoeld om het basislicht mee te vermenigvuldigen. */
    fun getOvercastTint(): Color {
        return overcastTint.set(Color.WHITE).lerp(AmbientLight.RAIN.color, getRainStrength())
    }

}
