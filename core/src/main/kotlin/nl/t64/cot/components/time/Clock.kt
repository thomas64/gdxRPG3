package nl.t64.cot.components.time

import com.badlogic.gdx.math.MathUtils


private const val TWELVE_HOURS = 43200f // 12 * 60 * 60
private const val RATE_OF_TIME = 30f    // 60: 1 hour -> 1 minute, 30: 1 hour -> 2 minutes, 1: 1 hour -> 60 minutes

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

    fun pause() {
        hasStarted = false
    }

    fun resume() {
        hasStarted = true
    }

    fun update(dt: Float) {
        countdown -= (dt * RATE_OF_TIME)
    }

    fun isWarning(): Boolean {
        return countdown <= 3600f
    }

    fun isFinished(): Boolean {
        return countdown <= -1f
    }

    fun getCountdownFormatted(): String {
        return String.format("%02d:%02d", getHours(), getMinutes())
    }

    private fun getMinutes(): Int {
        return MathUtils.floor(countdown / 60f % 60f)
    }

    private fun getHours(): Int {
        return MathUtils.floor(countdown / 3600f % 24f)
    }

}
