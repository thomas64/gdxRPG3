package nl.t64.cot.screens.world

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.badlogic.gdx.utils.viewport.Viewport
import nl.t64.cot.screens.world.mapobjects.GameMapCameraBlocker
import nl.t64.cot.sfx.ShakeCamera
import kotlin.math.max
import kotlin.math.roundToInt


private const val LERP_FACTOR: Float = 0.1f

class Camera : OrthographicCamera() {

    val viewport: Viewport = ScreenViewport(this)
    private val shakeCam: ShakeCamera = ShakeCamera()
    private var mapWidth: Float = 0f
    private var mapHeight: Float = 0f
    private var cameraBlockers: List<GameMapCameraBlocker> = emptyList()
    private var originalPositionX: Float = 0f
    private var originalPositionY: Float = 0f

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
        super.update()
    }

    fun setPositionRoundedForPixelPerfectRendering() {
        originalPositionX = position.x
        originalPositionY = position.y
        position.x = (position.x * 3f).roundToInt() / 3f
        position.y = (position.y * 3f).roundToInt() / 3f
        super.update()
    }

    fun restoreOriginalPositionForSmoothMovement() {
        position.x = originalPositionX
        position.y = originalPositionY
        super.update()
    }

    fun setNewMapSize(mapWidth: Float, mapHeight: Float) {
        this.mapWidth = mapWidth
        this.mapHeight = mapHeight
    }

    fun setCameraBlockers(blockers: List<GameMapCameraBlocker>) {
        cameraBlockers = blockers
    }

    private fun Vector2.toOffsetPositionForMapEdges(): Vector2 {
        // Calculate visible half-sizes of camera on both axis
        val halfCameraWidth: Float = zoomedCameraWidth / 2 - getHorizontalSpaceBetweenCameraAndMapEdge()
        val halfCameraHeight: Float = zoomedCameraHeight / 2 - getVerticalSpaceBetweenCameraAndMapEdge()

        // Calculate boundaries: how far left/right/up/down camera center can go while staying on map
        val minXofCenterOfCameraPosition: Float = halfCameraWidth
        val maxXofCenterOfCameraPosition: Float = mapWidth - halfCameraWidth
        val minYofCenterOfCameraPosition: Float = halfCameraHeight
        val maxYofCenterOfCameraPosition: Float = mapHeight - halfCameraHeight

        // Start with map boundaries as the allowed range
        var finalMinX: Float = minXofCenterOfCameraPosition
        var finalMaxX: Float = maxXofCenterOfCameraPosition
        var finalMinY: Float = minYofCenterOfCameraPosition
        var finalMaxY: Float = maxYofCenterOfCameraPosition

        // Apply blockers to ranges
        cameraBlockers.forEach { blocker ->
            when (blocker.axis) {
                GameMapCameraBlocker.Axis.HORIZONTAL -> {
                    val range: ClosedFloatingPointRange<Float> = minXofCenterOfCameraPosition..maxXofCenterOfCameraPosition
                    val (min, max) = blocker.calculateRestrictedAxisRange(currentRange = finalMinX..finalMaxX,
                                                                          edgeRange = range,
                                                                          playerPosition = this,
                                                                          halfCameraWidth = halfCameraWidth,
                                                                          halfCameraHeight = halfCameraHeight)
                    finalMinX = min
                    finalMaxX = max
                }
                GameMapCameraBlocker.Axis.VERTICAL -> {
                    val range: ClosedFloatingPointRange<Float> = minYofCenterOfCameraPosition..maxYofCenterOfCameraPosition
                    val (min, max) = blocker.calculateRestrictedAxisRange(currentRange = finalMinY..finalMaxY,
                                                                          edgeRange = range,
                                                                          playerPosition = this,
                                                                          halfCameraWidth = halfCameraWidth,
                                                                          halfCameraHeight = halfCameraHeight)
                    finalMinY = min
                    finalMaxY = max
                }
            }
        }

        // Validate final ranges: if blockers created invalid ranges (min > max), reset to map boundaries
        if (finalMinX > finalMaxX) {
            finalMinX = minXofCenterOfCameraPosition
            finalMaxX = maxXofCenterOfCameraPosition
        }
        if (finalMinY > finalMaxY) {
            finalMinY = minYofCenterOfCameraPosition
            finalMaxY = maxYofCenterOfCameraPosition
        }

        // Clamp camera position within final allowed ranges
        return Vector2(MathUtils.clamp(this.x, finalMinX, finalMaxX),
                       MathUtils.clamp(this.y, finalMinY, finalMaxY))
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
