package nl.t64.cot.components.time

import java.time.Duration
import java.time.LocalTime
import kotlin.math.floor


private const val START_OF_DAY = 27000L         // 7:30
private const val TWELVE_HOURS = 43200f * 2f    // 12 * 60 * 60 (* 2)
private const val HOUR = 7200f                  // 2 minute in realtime
private const val HALF_HOUR = 3600f             // 1 minute in realtime
private const val QUARTER = 1800f
private const val RATE_OF_TIME = 60f            // 60: 1 hour -> 1 minute, 30: 1 hour -> 2 minutes, etc.

fun String.toLocalTime(): LocalTime {
    return removePrefix("0").split(":").let {
        GameTime.of(it[0].toInt(), it[1].toInt())
    }
}

object GameTime {
    fun of(hours: Int, minutes: Int): LocalTime {
        val startOfDay: LocalTime = LocalTime.MIN.plusSeconds(START_OF_DAY)
        val deltaInSeconds: Long = Duration.between(startOfDay, LocalTime.of(hours, minutes)).toSeconds()
        return startOfDay.plusSeconds(deltaInSeconds * 2L)
    }
}

class Clock {

    private var countdown: Float = 0f
    private var hasStarted: Boolean = false
    private val passedSeconds: Float get() = TWELVE_HOURS - countdown

    fun start() {
        hasStarted = true
        countdown = TWELVE_HOURS
    }

    fun isRunning(): Boolean {
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
            countdown -= QUARTER
        }
    }

    fun takeHalfHour() {
        if (countdown > HOUR) {
            countdown -= HALF_HOUR
        }
    }

    fun takeHour() {
        if (countdown > (HOUR * 1.5f)) {
            countdown -= HOUR
        }
    }

    fun isWarning(): Boolean {
        return countdown <= HOUR
    }

    fun isFinished(): Boolean {
        return countdown <= -60f
    }

    fun possibleAddSomeExtraLoadingTime() {
        if (countdown < QUARTER) {
            countdown = QUARTER
        }
    }

    private fun stop() {
        hasStarted = false
    }

    fun isCurrentTimeInBetween(start: String, end: String): Boolean {
        val startTime: LocalTime = start.toLocalTime()
        val endTime: LocalTime = end.toLocalTime()
        return isCurrentTimeInBetween(startTime, endTime)
    }

    fun isCurrentTimeInBetween(startTime: LocalTime, endTime: LocalTime): Boolean {
        val currentTime: LocalTime = getTimeOfDay()
        return (currentTime == startTime || currentTime.isAfter(startTime)) && currentTime.isBefore(endTime)
    }

    fun getPercentageOfDay(): Float {
        return 1f - (passedSeconds / TWELVE_HOURS)
    }

    fun getTimeOfDay(): LocalTime {
        val passedSecondsFromStartOfDay = START_OF_DAY + passedSeconds.toLong()
        return LocalTime.MIN.plusSeconds(passedSecondsFromStartOfDay)
    }

    fun getTimeOfDayFormatted(): String {
        val seconds = START_OF_DAY + (passedSeconds / 2f)
        return String.format("%02d:%02d", seconds.toHours(), seconds.toMinutes())
    }

    private fun Float.toHours(): Int {
        return floor(this / 3600f % 24f).toInt()
    }

    private fun Float.toMinutes(): Int {
        return floor(this / 60f % 60f).toInt()
    }

}
