package nl.t64.cot.screens.world.mapobjects

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import nl.t64.cot.Utils
import nl.t64.cot.screens.world.Camera


private const val BACKGROUND_REGION_MULTIPLIER = 2

class GameMapParallaxBackground(id: String) {

    private val sprite: Sprite

    init {
        val texture = Utils.createLightmap(id).apply {
            setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat)
        }
        val region = TextureRegion(texture).apply {
            regionWidth = texture.width * BACKGROUND_REGION_MULTIPLIER
        }
        sprite = Sprite(region)
    }

    fun render(batch: Batch, camera: Camera) {
        val cameraWidth = camera.zoomedCameraWidth
        val cameraHeight = camera.zoomedCameraHeight
        val halfWidth = cameraWidth / 2f
        val halfHeight = cameraHeight / 2f

        sprite.setSize(cameraWidth, cameraHeight)
        sprite.setPosition(-halfWidth, -halfHeight)
        sprite.draw(batch)
    }

}
