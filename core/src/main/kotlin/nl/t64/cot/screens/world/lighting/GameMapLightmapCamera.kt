package nl.t64.cot.screens.world.lighting

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import nl.t64.cot.screens.world.Camera
import kotlin.math.min


class GameMapLightmapCamera(
    lightmaps: List<Lightmap>,
    private val mapPixelWidth: Float,
    private val mapPixelHeight: Float,
    private val sunlightStrength: () -> Float
) {
    private val sprites: Map<Lightmap, Sprite> = lightmaps.associateWith { Sprite(it.texture) }

    fun render(batch: Batch, camera: Camera) {
        val cameraWidth = camera.viewportWidth
        val cameraHeight = camera.viewportHeight
        val mapWidth = mapPixelWidth / camera.zoom
        val mapHeight = mapPixelHeight / camera.zoom
        val minWidth = min(cameraWidth, mapWidth)
        val minHeight = min(cameraHeight, mapHeight)
        val halfWidth = minWidth * camera.zoom
        val halfHeight = minHeight * camera.zoom
        val quarterWidth = minWidth * (camera.zoom / 2f)
        val quarterHeight = minHeight * (camera.zoom / 2f)

        sprites.forEach { (lightmap, sprite) ->
            lightmap.blend.applyTo(batch)
            if (lightmap == Lightmap.SUNLIGHT) {
                fadeSunlight(sprite)
            }
            sprite.setSize(halfWidth, halfHeight)
            sprite.setPosition(-quarterWidth, -quarterHeight)
            sprite.draw(batch)
        }
    }

    /** Sunlight wordt additief getekend, waarbij alpha niet meetelt; daarom dimt de kleur hem. */
    private fun fadeSunlight(sprite: Sprite) {
        val strength: Float = sunlightStrength.invoke()
        sprite.setColor(strength, strength, strength, 1f)
    }

}
