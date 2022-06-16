package nl.t64.cot.screens.world.mapobjects

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import nl.t64.cot.Utils
import nl.t64.cot.screens.world.Camera


private const val LIGHTMAP_REGION_MULTIPLIER = 10
private const val SCROLL_SPEED = 10f

class GameMapLightmapMap(id: String) {

    private val texture: Texture
    private val sprite: Sprite

    private var scrollerX = 0f
    private var scrollerY = 0f

    init {
        texture = Utils.createLightmap(id).apply {
            setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat)
        }
        val region = TextureRegion(texture).apply {
            regionWidth = texture.width * LIGHTMAP_REGION_MULTIPLIER
            regionHeight = texture.height * LIGHTMAP_REGION_MULTIPLIER
        }
        sprite = Sprite(region)
    }

    fun render(batch: Batch, camera: Camera) {
        updateScrollers()
        scrollDefinedLightmaps(camera)

        if (texture.toString().contains("fog")) {
            batch.setBlendFunction(GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        }

        sprite.draw(batch)
    }

    private fun updateScrollers() {
        scrollerX += Gdx.graphics.deltaTime * SCROLL_SPEED
        scrollerY += Gdx.graphics.deltaTime * SCROLL_SPEED
        if (scrollerX > sprite.width / LIGHTMAP_REGION_MULTIPLIER) {
            scrollerX = 0f
        }
        if (scrollerY > sprite.height / LIGHTMAP_REGION_MULTIPLIER) {
            scrollerY = 0f
        }
    }

    private fun scrollDefinedLightmaps(camera: Camera) {
        val textureName = texture.toString()
        if (textureName.contains("forest")) {
            sprite.x = -scrollerX
            sprite.y = -scrollerY
        } else if (textureName.contains("bubbles")
            || textureName.contains("fog")
        ) {
            sprite.x = -camera.getHorizontalSpaceBetweenCameraAndMapEdge()
            sprite.y = -scrollerY
        }
    }

}
