package nl.t64.cot.components.time

import com.badlogic.gdx.math.MathUtils
import java.time.Duration
import java.time.LocalTime


private const val START_OF_DAY = 27000f         // 7:30
private const val TWELVE_HOURS = 43200f * 2f    // 12 * 60 * 60 (* 2)
private const val HALF_HOUR = 3600f             // 1 minute in realtime
private const val RATE_OF_TIME = 60f            // 60: 1 hour -> 1 minute, 30: 1 hour -> 2 minutes, etc.

object GameTime {
    fun of(hours: Int, minutes: Int): LocalTime {
        val startOfDay: LocalTime = LocalTime.MIN.plusSeconds(START_OF_DAY.toLong())
        val deltaInSeconds: Long = Duration.between(startOfDay, LocalTime.of(hours, minutes)).toSeconds()
        return startOfDay.plusSeconds(deltaInSeconds * 2L)
    }
}

class Clock {

    private var countdown: Float = 0f
    private var hasStarted: Boolean = false

    fun start() {
        hasStarted = true
        countdown = TWELVE_HOURS
    }

    fun hasStarted(): Boolean {
        return hasStarted
    }

    fun reset() {
        countdown = TWELVE_HOURS
    }

    fun update(dt: Float) {
        countdown -= (dt * RATE_OF_TIME)
        if (isFinished()) {
            stop()
        }
    }

    fun takeQuarterHour() {
        if (countdown > HALF_HOUR) {
            countdown -= (HALF_HOUR / 2f)
        }
    }

    fun takeHalfHour() {
        if (countdown > (HALF_HOUR * 2f)) {
            countdown -= HALF_HOUR
        }
    }

    fun takeHour() {
        if (countdown > (HALF_HOUR * 4f)) {
            countdown -= (HALF_HOUR * 2f)
        }
    }

    fun isWarning(): Boolean {
        return countdown <= HALF_HOUR
    }

    fun isFinished(): Boolean {
        return countdown <= -10f
    }

    fun getCountdownFormatted(): String {
        return if (countdown > 0f) {
            val retimedCountdown = countdown / 2f
            String.format("%02d:%02d", getHours(retimedCountdown), getMinutes(retimedCountdown))
        } else {
            "00:00"
        }
    }

    fun getTimeOfDayFormatted(): String {
        val timeOfDayInSeconds = START_OF_DAY + ((TWELVE_HOURS - countdown) / 2f)
        return String.format("%02d:%02d", getHours(timeOfDayInSeconds), getMinutes(timeOfDayInSeconds))
    }

    fun getTimeOfDay(): LocalTime {
        val timeOfDayInSeconds = START_OF_DAY + (TWELVE_HOURS - countdown)
        return LocalTime.MIN.plusSeconds(timeOfDayInSeconds.toLong())
    }

    private fun stop() {
        hasStarted = false
    }

    private fun getMinutes(seconds: Float): Int {
        return MathUtils.floor(seconds / 60f % 60f)
    }

    private fun getHours(seconds: Float): Int {
        return MathUtils.floor(seconds / 3600f % 24f)
    }

}
