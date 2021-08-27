package nl.t64.cot.sfx

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2


private const val SHAKE_RADIUS = 30f
private const val DIMINISH_THRESHOLD = 2f
private const val DIMINISH_FACTOR = 0.9f

class ShakeCamera(x: Float = 0f, y: Float = 0f, aShakeRadius: Float = SHAKE_RADIUS) {

    constructor(x: Float, y: Float) : this(x, y, SHAKE_RADIUS)
    constructor(shakeRadius: Float) : this(0f, 0f, shakeRadius)

    private val originalPosition: Vector2 = Vector2(x, y)
    private val currentPosition: Vector2 = Vector2()
    private val offset: Vector2 = Vector2()
    private val originalShakeRadius: Float = aShakeRadius

    private var shakeRadius: Float = aShakeRadius
    private var randomAngle = 0f

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

    fun getNewShakePosition(): Vector2 {
        computeCameraOffset()
        computeCurrentPosition()
        diminishShake()
        return currentPosition
    }

    private fun computeCameraOffset() {
        offset.x = MathUtils.cosDeg(randomAngle) * shakeRadius
        offset.y = MathUtils.sinDeg(randomAngle) * shakeRadius
    }

    private fun computeCurrentPosition() {
        currentPosition.set(offset.add(originalPosition))
    }

    private fun diminishShake() {
        if (shakeRadius > DIMINISH_THRESHOLD) {
            continueShake()
        } else {
            resetShake()
        }
    }

    private fun continueShake() {
        isShaking = true
        shakeRadius *= DIMINISH_FACTOR
        seedRandomAngle()
    }

    private fun resetShake() {
        isShaking = false
        shakeRadius = originalShakeRadius
        seedRandomAngle()
        currentPosition.set(originalPosition)
    }

    private fun seedRandomAngle() {
        randomAngle = MathUtils.random(1, 360).toFloat()
    }

}
