package nl.t64.cot.screens.world

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.ScreenUtils
import nl.t64.cot.Utils.mapManager
import nl.t64.cot.constants.Constant


private val BOTTOM_LAYERS = intArrayOf(0, 1, 2, 3)
private val UNDER_LAYERS = intArrayOf(4, 5)
private val OVER_LAYERS = intArrayOf(6, 7, 8)

class WorldRenderer(private val camera: Camera) : OrthogonalTiledMapRenderer(null) {

    private val frameBuffer = FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.width, Gdx.graphics.height, false)

    fun updateCamera() {
        setView(camera)
    }

    fun renderMapWithoutEntities() {
        renderWithoutPlayerLight {}
    }

    fun renderAll(playerPosition: Vector2, renderEntities: (Batch) -> Unit) {
        mapManager.getLightmapPlayer()
            ?.let { renderWithPlayerLight(playerPosition, it, renderEntities) }
            ?: renderWithoutPlayerLight(renderEntities)
    }

    private fun renderWithPlayerLight(playerPosition: Vector2,
                                      lightmapPlayer: Sprite,
                                      renderEntities: (Batch) -> Unit) {
        frameBuffer.begin()
        ScreenUtils.clear(Color.BLACK)
        renderLightmapPlayer(playerPosition, lightmapPlayer)
        frameBuffer.end()
        renderMapLayers(renderEntities)
        renderFrameBuffer()
    }

    private fun renderWithoutPlayerLight(renderEntities: (Batch) -> Unit) {
        ScreenUtils.clear(Color.BLACK)
        renderMapLayers(renderEntities)
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
        mapManager.getGameMapLights().forEach { it.render(batch) }
        batch.end()
    }

    private fun renderMapLayers(renderEntities: (Batch) -> Unit) {
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)

        renderPossibleBackground()
        batch.projectionMatrix = camera.combined
        render(BOTTOM_LAYERS)
        renderLowerTextures()
        render(UNDER_LAYERS)
        renderDynamicMapEntities(renderEntities)
        render(OVER_LAYERS)
        renderParticles()
        renderLightmap()
    }

    private fun renderPossibleBackground() {
        mapManager.getParallaxBackground()?.let {
            batch.begin()
            batch.projectionMatrix = camera.projection
            it.render(batch, camera)
            batch.end()
        }
    }

    private fun renderLowerTextures() {
        batch.begin()
        mapManager.getLowerConditionTextures().forEach { it.render(batch) }
        batch.end()
    }

    private fun renderDynamicMapEntities(renderEntities: (Batch) -> Unit) {
        batch.begin()
        renderEntities.invoke(batch)
        mapManager.getUpperConditionTextures().forEach { it.render(batch) }
        batch.end()
    }

    private fun renderParticles() {
        batch.begin()
        mapManager.getParticleEffects().forEach { it.render(batch) }
        batch.end()
    }

    private fun renderLightmap() {
        batch.begin()
        batch.setBlendFunction(GL20.GL_ONE, GL20.GL_ONE)
        renderLightmapCamera()
        renderLightmapMap()
        batch.end()
    }

    private fun renderLightmapCamera() {
        batch.projectionMatrix = camera.projection
        mapManager.getLightmapCamera().render(batch, camera)
    }

    private fun renderLightmapMap() {
        batch.projectionMatrix = camera.combined
        mapManager.getLightmapMap().render(batch, camera)
    }

}
