package nl.t64.cot.sfx

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import nl.t64.cot.constants.Constant
import kotlin.math.pow


private const val SHAKE_RADIUS = 30f
private const val DIMINISH_THRESHOLD = 2f
private const val DIMINISH_FACTOR = 0.9f
private const val SECONDS_BETWEEN_ANGLES = 1f / Constant.TUNED_AT_FRAMES_PER_SECOND

class ShakeCamera(x: Float = 0f, y: Float = 0f, aShakeRadius: Float = SHAKE_RADIUS) {

    private val originalPosition: Vector2 = Vector2(x, y)
    private val currentPosition: Vector2 = Vector2()
    private val offset: Vector2 = Vector2()
    private val originalShakeRadius: Float = aShakeRadius

    private var shakeRadius: Float = aShakeRadius
    private var randomAngle = 0f
    private var secondsSinceNewAngle = 0f

    var isShaking = false

    init {
        resetShake()
    }

    fun setOriginalPosition(x: Float, y: Float) {
        originalPosition.set(x, y)
    }

    fun startShaking() {
        isShaking = true
    }

    fun getNewShakePosition(dt: Float): Vector2 {
        possibleSeedNewAngle(dt)
        computeCameraOffset()
        computeCurrentPosition()
        diminishShake(dt)
        return currentPosition
    }

    // A new angle every frame would make the shake vibrate faster on a machine that renders more frames. It
    // rattles at a fixed rate now, which is what it happened to do at TUNED_AT_FRAMES_PER_SECOND before.
    private fun possibleSeedNewAngle(dt: Float) {
        secondsSinceNewAngle += dt
        if (secondsSinceNewAngle < SECONDS_BETWEEN_ANGLES) return

        secondsSinceNewAngle = 0f
        seedRandomAngle()
    }

    private fun computeCameraOffset() {
        offset.x = MathUtils.cosDeg(randomAngle) * shakeRadius
        offset.y = MathUtils.sinDeg(randomAngle) * shakeRadius
    }

    private fun computeCurrentPosition() {
        currentPosition.set(offset.add(originalPosition))
    }

    private fun diminishShake(dt: Float) {
        if (shakeRadius > DIMINISH_THRESHOLD) {
            continueShake(dt)
        } else {
            resetShake()
        }
    }

    // Same trick as the angle: DIMINISH_FACTOR was what one frame at TUNED_AT_FRAMES_PER_SECOND took off, so
    // it is raised to the power of the frames that fit in dt. The shake now lasts as long on every machine.
    private fun continueShake(dt: Float) {
        isShaking = true
        shakeRadius *= DIMINISH_FACTOR.pow(dt * Constant.TUNED_AT_FRAMES_PER_SECOND)
    }

    private fun resetShake() {
        isShaking = false
        shakeRadius = originalShakeRadius
        secondsSinceNewAngle = 0f
        seedRandomAngle()
        currentPosition.set(originalPosition)
    }

    private fun seedRandomAngle() {
        randomAngle = MathUtils.random(1, 360).toFloat()
    }

}
