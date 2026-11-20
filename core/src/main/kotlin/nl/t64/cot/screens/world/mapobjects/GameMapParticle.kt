package nl.t64.cot.screens.world.mapobjects

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.ParticleEffect
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.math.Vector2
import nl.t64.cot.Utils.resourceManager
import nl.t64.cot.screens.world.atmosphere.LitSchedule


private const val TORCH_SCALE = 0.3f
private const val TORCH_Y_OFFSET = -9f
private const val PRE_WARM_TIMES = 60

class GameMapParticle(rectObject: RectangleMapObject) : GameMapObject(rectObject.rectangle) {

    private val name: String = rectObject.name
    private val center: Vector2 = rectObject.getCenter()
    private val litSchedule = LitSchedule(rectObject.properties)
    private var isLit: Boolean = litSchedule.isLit()
    private val particle: ParticleEffect = createParticle()

    fun update(dt: Float) {
        updateLitState()
        if (isLit) {
            particle.update(dt)
        }
    }

    fun render(batch: Batch) {
        if (isLit) {
            particle.draw(batch)
        }
    }

    fun dispose() {
        particle.dispose()
    }

    private fun updateLitState() {
        val wasLit: Boolean = isLit
        isLit = litSchedule.isLit()
        if (!wasLit && isLit) {
            ignite()
        }
    }

    /** Without pre-warming, so the player sees the flame rise. */
    private fun ignite() {
        particle.reset(false)
    }

    private fun createParticle(): ParticleEffect {
        return resourceManager.getParticleAsset(name).apply {
            when (name) {
                "torch" -> {
                    scaleEffect(TORCH_SCALE)
                    setPosition(center.x, center.y + TORCH_Y_OFFSET)
                    repeat(PRE_WARM_TIMES) { update(Gdx.graphics.deltaTime) }
                }
                else -> throw IllegalArgumentException("RectObject.name: $name is not defined.")
            }
        }
    }

}
