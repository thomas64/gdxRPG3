package nl.t64.cot.screens.world

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.maps.objects.TextureMapObject
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.ScreenUtils
import nl.t64.cot.Utils.createLightmap
import nl.t64.cot.Utils.mapManager
import nl.t64.cot.constants.Constant
import nl.t64.cot.screens.world.mapobjects.GameMapQuestTexture


private val UNDER_LAYERS = intArrayOf(0, 1, 2, 3, 4, 5)
private val OVER_LAYERS = intArrayOf(6, 7, 8)
private const val SCROLL_SPEED = 10f
private const val LIGHTMAP_ID = "light_object"

class TextureMapObjectRenderer(private val camera: Camera) : OrthogonalTiledMapRenderer(null) {

    private val frameBuffer = FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.width, Gdx.graphics.height, false)
    private var scrollerX = 0f
    private var scrollerY = 0f

    fun renderMap() {
        renderWithoutPlayerLight {}
    }

    fun renderAll(playerPosition: Vector2, renderEntities: () -> Unit) {
        mapManager.getLightmapPlayer()?.let {
            renderWithPlayerLight(playerPosition, it, renderEntities)
        } ?: renderWithoutPlayerLight(renderEntities)
    }

    private fun renderWithPlayerLight(playerPosition: Vector2, sprite: Sprite, renderEntities: () -> Unit) {
        frameBuffer.begin()
        ScreenUtils.clear(Color.BLACK)
        renderLightmapPlayer(playerPosition, sprite)
        frameBuffer.end()
        renderMapLayers(renderEntities)
        renderFrameBuffer()
    }

    private fun renderWithoutPlayerLight(renderEntities: () -> Unit) {
        ScreenUtils.clear(Color.BLACK)
        renderMapLayers(renderEntities)
    }

    fun updateCamera() {
        setView(camera)
    }

    private fun renderFrameBuffer() {
        batch.setBlendFunction(GL20.GL_ZERO, GL20.GL_SRC_COLOR)
        batch.projectionMatrix = batch.projectionMatrix.idt()

        batch.begin()
        batch.draw(frameBuffer.colorBufferTexture, -1f, 1f, 2f, -2f)
        batch.end()
    }

    private fun renderLightmapPlayer(playerPosition: Vector2, lightmapPlayer: Sprite) {
        batch.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE)

        batch.begin()
        val x = playerPosition.x + Constant.HALF_TILE_SIZE - lightmapPlayer.width / 2f
        val y = playerPosition.y + Constant.HALF_TILE_SIZE - lightmapPlayer.height / 2f
        lightmapPlayer.setPosition(x, y)
        lightmapPlayer.draw(batch)
        renderOtherMapLights()
        batch.end()
    }

    private fun renderOtherMapLights() {
        val darkness = createLightmap(LIGHTMAP_ID)
        mapManager.getGameMapLights().forEach {
            if (it.id == "blue") {
                batch.color = Color.ROYAL
            }
            val x = it.center.x - darkness.width / 2f
            val y = it.center.y - darkness.height / 2f
            batch.draw(darkness, x, y)
            batch.color = Color.WHITE
        }
    }

    private fun renderMapLayers(renderEntities: () -> Unit) {
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)

        renderBackground()
        batch.projectionMatrix = camera.combined
        render(UNDER_LAYERS)
        batch.begin()
        renderQuestTextures(mapManager.getLowerMapQuestTextures())
        renderEntities.invoke()
        renderQuestTextures(mapManager.getUpperMapQuestTextures())
        batch.end()
        render(OVER_LAYERS)
        renderLightmap()
    }

    private fun renderQuestTextures(gameMapQuestTextures: List<GameMapQuestTexture>) {
        gameMapQuestTextures
            .filter { it.isVisible }
            .map { it.texture }
            .forEach { renderObject(it) }
    }

    private fun renderObject(mapObject: TextureMapObject) {
        batch.draw(mapObject.textureRegion, mapObject.x, mapObject.y)
    }

    private fun renderBackground() {
        batch.begin()
        batch.projectionMatrix = camera.projection
        mapManager.getParallaxBackground(camera).draw(batch)
        batch.end()
    }

    private fun renderLightmap() {
        batch.begin()
        renderLightmapCamera()
        renderLightmapMap()
        batch.end()
    }

    private fun renderLightmapCamera() {
        batch.projectionMatrix = camera.projection
        batch.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE)
        mapManager.getLightmapCamera(camera).forEach { it.draw(batch) }
    }

    private fun renderLightmapMap() {
        batch.projectionMatrix = camera.combined
        val lightmap = mapManager.getLightmapMap()
        scrollerX += Gdx.graphics.deltaTime * SCROLL_SPEED
        scrollerY += Gdx.graphics.deltaTime * SCROLL_SPEED
        if (scrollerX > lightmap.width / LIGHTMAP_REGION_MULTIPLIER) {
            scrollerX = 0f
        }
        if (scrollerY > lightmap.height / LIGHTMAP_REGION_MULTIPLIER) {
            scrollerY = 0f
        }
        scrollDefinedLightmaps(lightmap)
        if (lightmap.texture.toString().contains("fog")) {
            batch.setBlendFunction(GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        }
        lightmap.draw(batch)
    }

    private fun scrollDefinedLightmaps(lightmap: Sprite) {
        val textureName = lightmap.texture.toString()
        if (textureName.contains("forest")) {
            lightmap.x = -scrollerX
            lightmap.y = -scrollerY
        } else if (textureName.contains("bubbles")
            || textureName.contains("fog")
        ) {
            lightmap.x = -camera.getHorizontalSpaceBetweenCameraAndMapEdge()
            lightmap.y = -scrollerY
        }
    }

}
