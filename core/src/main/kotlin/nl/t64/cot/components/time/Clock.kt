package nl.t64.cot.components.time

import java.time.Duration
import java.time.LocalDateTime
import kotlin.math.floor


private const val START_OF_DAY = 27000L         // 7:30
private const val TWELVE_HOURS = 43200f * 2f    // 12 * 60 * 60 (* 2)
private const val HOUR = 7200f                  // 2 minute in realtime
private const val HALF_HOUR = 3600f             // 1 minute in realtime
private const val QUARTER = 1800f
private const val FIVE_MINUTES = 600f
private const val RATE_OF_TIME = 60f            // 60: 1 hour -> 1 minute, 30: 1 hour -> 2 minutes, etc.

class Clock {

    private var countdown: Float = 0f
    private var hasStarted: Boolean = false
    private val startOfDay: LocalDateTime = LocalDateTime.MIN.plusSeconds(START_OF_DAY)
    private val passedSeconds: Float get() = TWELVE_HOURS - countdown
    private val currentTime: LocalDateTime get() = startOfDay.plusSeconds(passedSeconds.toLong())

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

    fun fiveMinutesBack() {
        countdown += FIVE_MINUTES
    }

    fun fiveMinutesForward() {
        countdown -= FIVE_MINUTES
    }

    fun halfHourBack() {
        countdown += HALF_HOUR
    }

    fun halfHourForward() {
        countdown -= HALF_HOUR
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
        return hasStarted && countdown <= HOUR
    }

    fun isFinished(): Boolean {
        return countdown <= -60f
    }

    fun possibleAddSomeExtraLoadingTime() {
        if (hasStarted && countdown < QUARTER) {
            countdown = QUARTER
        }
    }

    fun stop() {
        hasStarted = false
    }

    fun setTimeOfDay(time: String) {
        val newTimeInSeconds: Long = Duration.between(startOfDay, time.toGameTime()).toSeconds()
        countdown = TWELVE_HOURS - newTimeInSeconds
    }

    fun getPercentageOfCurrentTimeBetween(startTime: String, endTime: String): Float {
        return getPercentageOfCurrentTimeBetween(startTime.toGameTime(), endTime.toGameTime())
    }

    fun isCurrentTimeBefore(time: String): Boolean {
        return currentTime.isBefore(time.toGameTime())
    }

    fun isCurrentTimeInBetween(startTime: String, endTime: String): Boolean {
        return isCurrentTimeInBetween(startTime.toGameTime(), endTime.toGameTime())
    }

    fun isCurrentTimeAfter(time: String): Boolean {
        return currentTime.isNowOrAfter(time.toGameTime());
    }

    fun isCurrentTimeAt(time: String): Boolean {
        val startTime = time.toGameTime()
        val endTime = time.toGameTime().plusMinutes(1L)
        return isCurrentTimeInBetween(startTime, endTime)
    }

    fun getPercentageOfDay(): Float {
        return 1f - (passedSeconds / TWELVE_HOURS)
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

    private fun getPercentageOfCurrentTimeBetween(startTime: LocalDateTime, endTime: LocalDateTime): Float {
        val currentSeconds: Long = Duration.between(startTime, currentTime).toSeconds()
        val targetSeconds: Long = Duration.between(startTime, endTime).toSeconds()
        return currentSeconds * 100f / targetSeconds
    }

    private fun isCurrentTimeInBetween(startTime: LocalDateTime, endTime: LocalDateTime): Boolean {
        return currentTime.isNowOrAfter(startTime) && currentTime.isBefore(endTime)
    }

    private fun LocalDateTime.isNowOrAfter(time: LocalDateTime): Boolean {
        return isEqual(time) || isAfter(time)
    }

    private fun String.toGameTime(): LocalDateTime {
        return removePrefix("0").split(":").let {
            toGameTime(it[0].toLong(), it[1].toLong())
        }
    }

    private fun toGameTime(hours: Long, minutes: Long): LocalDateTime {
        val givenTime: LocalDateTime = LocalDateTime.MIN.plusHours(hours).plusMinutes(minutes)
        val deltaInSeconds: Long = Duration.between(startOfDay, givenTime).toSeconds()
        return startOfDay.plusSeconds(deltaInSeconds * 2L)
    }

}
