package nl.t64.cot.screens.world.mapobjects

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import nl.t64.cot.screens.world.Camera


private const val LIGHTMAP_REGION_MULTIPLIER = 10
private const val SCROLL_SPEED = 10f

class GameMapLightmapMap(
    private val lightmap: Lightmap
) {
    private val sprite: Sprite

    private var scrollerX = 0f
    private var scrollerY = 0f

    init {
        val texture: Texture = lightmap.texture.apply {
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
        lightmap.blend.applyTo(batch)
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
        when (lightmap.scroll) {
            LightmapScroll.DIAGONAL -> {
                sprite.x = -scrollerX
                sprite.y = -scrollerY
            }
            LightmapScroll.VERTICAL -> {
                sprite.x = -camera.getHorizontalSpaceBetweenCameraAndMapEdge()
                sprite.y = -scrollerY
            }
            LightmapScroll.NONE -> Unit
        }
    }

}
