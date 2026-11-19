package nl.t64.cot.screens.world.atmosphere

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.ParticleEffect
import com.badlogic.gdx.graphics.g2d.ParticleEmitter
import com.badlogic.gdx.math.Vector2
import nl.t64.cot.Utils.resourceManager
import nl.t64.cot.screens.world.Camera
import kotlin.math.abs
import kotlin.math.min


private const val RAIN_PARTICLE = "rain"
private const val MILLIS_PER_SECOND = 1000f

class GameMapRain(
    private val mapPixelWidth: Float,
    private val mapPixelHeight: Float
) {
    private val particle: ParticleEffect = resourceManager.getParticleAsset(RAIN_PARTICLE)
    private val emitter: ParticleEmitter = particle.emitters.first()
    private val maxEmission: Float = emitter.emission.highMax
    private val fallDistance: Vector2 = calculateLongestFallDistance()

    fun update(dt: Float) {
        emitter.emission.setHigh(maxEmission * RainCycle.getRainStrength())
        particle.update(dt)
    }

    fun render(batch: Batch, camera: Camera) {
        followCamera(camera)
        fitSpawnAreaToScreen(camera)
        particle.draw(batch)
    }

    fun dispose() {
        particle.dispose()
    }

    /** rain.p is attached: vallende druppels schuiven mee met de camera, zodat ze op het scherm altijd even schuin vallen. */
    private fun followCamera(camera: Camera) {
        particle.setPosition(camera.position.x - fallDistance.x / 2f, camera.position.y - fallDistance.y / 2f)
    }

    private fun fitSpawnAreaToScreen(camera: Camera) {
        val screenWidth: Float = min(camera.viewportWidth * camera.zoom, mapPixelWidth)
        val screenHeight: Float = min(camera.viewportHeight * camera.zoom, mapPixelHeight)
        emitter.spawnWidth.setHigh(screenWidth + abs(fallDistance.x))
        emitter.spawnHeight.setHigh(screenHeight + abs(fallDistance.y))
    }

    private fun calculateLongestFallDistance(): Vector2 {
        val distance: Float = emitter.velocity.highMax * emitter.life.highMax / MILLIS_PER_SECOND
        return Vector2(distance, 0f).setAngleDeg(emitter.angle.highMax)
    }

}
