package nl.t64.cot.screens.world.mapobjects

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.ParticleEffect
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.math.Vector2
import nl.t64.cot.Utils.resourceManager


private const val TORCH_SCALE = 0.3f
private const val TORCH_Y_OFFSET = -9f
private const val PRE_WARM_TIMES = 60

class GameMapParticle(rectObject: RectangleMapObject) : GameMapObject(rectObject.rectangle) {

    private val name: String = rectObject.name
    private val center: Vector2 = rectObject.getCenter()
    private val particle: ParticleEffect = createParticle()

    fun update(dt: Float) {
        particle.update(dt)
    }

    fun render(batch: Batch) {
        particle.draw(batch)
    }

    fun dispose() {
        particle.dispose()
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
