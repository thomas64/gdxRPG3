package nl.t64.cot.screens.world.mapobjects

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import nl.t64.cot.Utils
import nl.t64.cot.screens.world.Camera
import kotlin.math.min


class GameMapLightmapCamera(
    ids: String,
    private val mapPixelWidth: Float,
    private val mapPixelHeight: Float
) {
    private val sprites: List<Sprite> = ids
        .split(",")
        .map { it.trim() }
        .map { Utils.createLightmap(it) }
        .map { Sprite(it) }

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

        sprites
            .onEach { it.setSize(halfWidth, halfHeight) }
            .onEach { it.setPosition(-quarterWidth, -quarterHeight) }
            .forEach { it.draw(batch) }
    }

}
