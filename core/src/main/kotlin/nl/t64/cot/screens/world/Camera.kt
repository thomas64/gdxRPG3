package nl.t64.cot.screens.world

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.badlogic.gdx.utils.viewport.Viewport
import nl.t64.cot.sfx.ShakeCamera
import kotlin.math.max


private const val LERP_FACTOR: Float = 0.1f

class Camera : OrthographicCamera() {

    val viewport: Viewport = ScreenViewport(this)
    private val shakeCam: ShakeCamera = ShakeCamera()
    private var mapWidth: Float = 0f
    private var mapHeight: Float = 0f

    init {
        viewport.update(Gdx.graphics.width, Gdx.graphics.height)
        reset()
    }

    fun isZoomPossible(): Boolean {
        return mapWidth / zoom > Gdx.graphics.width
            && mapHeight / zoom > Gdx.graphics.height
    }

    fun zoom() {
        val zoomNumberWidth: Float = mapWidth / Gdx.graphics.width
        val zoomNumberHeight: Float = mapHeight / Gdx.graphics.height
        zoom = max(zoomNumberWidth, zoomNumberHeight)
    }

    fun reset() {
        zoom = 0.5f
    }

    fun startShaking() {
        shakeCam.startShaking()
    }

    fun setInitPosition(x: Float, y: Float) {
        setInitPosition(Vector2(x, y))
    }

    fun setInitPosition(playerPosition: Vector2) {
        position.set(playerPosition.toOffsetPositionForMapEdges(), 0f)
    }

    fun setPosition(x: Float, y: Float) {
        setPosition(Vector2(x, y))
    }

    fun setPosition(playerPosition: Vector2) {
        val cameraPosition: Vector2 = playerPosition.toOffsetPositionForMapEdges()
        if (shakeCam.isShaking) {
            val shakeCamPosition: Vector2 = shakeCam.getNewShakePosition().add(cameraPosition)
            position.set(shakeCamPosition, 0f)
        } else {
            position.lerp(Vector3(cameraPosition.x, cameraPosition.y, 0f), LERP_FACTOR)
        }
        update()
    }

    fun setNewMapSize(mapWidth: Float, mapHeight: Float) {
        this.mapWidth = mapWidth
        this.mapHeight = mapHeight
    }

    private fun Vector2.toOffsetPositionForMapEdges(): Vector2 {
        val halfCameraWidth = zoomedCameraWidth / 2 - getHorizontalSpaceBetweenCameraAndMapEdge()
        val halfCameraHeight = zoomedCameraHeight / 2 - getVerticalSpaceBetweenCameraAndMapEdge()
        return Vector2(MathUtils.clamp(this.x, halfCameraWidth, mapWidth - halfCameraWidth),
                       MathUtils.clamp(this.y, halfCameraHeight, mapHeight - halfCameraHeight))
    }

    fun getHorizontalSpaceBetweenCameraAndMapEdge(): Float {
        return if (mapWidth < zoomedCameraWidth) {
            (zoomedCameraWidth - mapWidth) / 2f
        } else 0f
    }

    private fun getVerticalSpaceBetweenCameraAndMapEdge(): Float {
        return if (mapHeight < zoomedCameraHeight) {
            (zoomedCameraHeight - mapHeight) / 2f
        } else 0f
    }

    val zoomedCameraHeight: Float get() = viewportHeight * zoom
    val zoomedCameraWidth: Float get() = viewportWidth * zoom

}
