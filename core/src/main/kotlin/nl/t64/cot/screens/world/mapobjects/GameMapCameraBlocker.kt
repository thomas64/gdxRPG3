package nl.t64.cot.screens.world.mapobjects

import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2


class GameMapCameraBlocker(rectObject: RectangleMapObject) : GameMapObject(rectObject.rectangle) {

    enum class Axis {
        HORIZONTAL {
            override fun getMainMin(rect: Rectangle) = rect.x
            override fun getMainMax(rect: Rectangle) = rect.x + rect.width
            override fun getMainCoordinate(pos: Vector2) = pos.x
            override fun getMainHalfSize(halfCamW: Float, halfCamH: Float) = halfCamW
            override fun getOverlapMin(rect: Rectangle) = rect.y
            override fun getOverlapMax(rect: Rectangle) = rect.y + rect.height
            override fun getOverlapCoordinate(pos: Vector2) = pos.y
            override fun getOverlapHalfSize(halfCamW: Float, halfCamH: Float) = halfCamH
        },
        VERTICAL {
            override fun getMainMin(rect: Rectangle) = rect.y
            override fun getMainMax(rect: Rectangle) = rect.y + rect.height
            override fun getMainCoordinate(pos: Vector2) = pos.y
            override fun getMainHalfSize(halfCamW: Float, halfCamH: Float) = halfCamH
            override fun getOverlapMin(rect: Rectangle) = rect.x
            override fun getOverlapMax(rect: Rectangle) = rect.x + rect.width
            override fun getOverlapCoordinate(pos: Vector2) = pos.x
            override fun getOverlapHalfSize(halfCamW: Float, halfCamH: Float) = halfCamW
        };

        abstract fun getMainMin(rect: Rectangle): Float
        abstract fun getMainMax(rect: Rectangle): Float
        abstract fun getMainCoordinate(pos: Vector2): Float
        abstract fun getMainHalfSize(halfCamW: Float, halfCamH: Float): Float
        abstract fun getOverlapMin(rect: Rectangle): Float
        abstract fun getOverlapMax(rect: Rectangle): Float
        abstract fun getOverlapCoordinate(pos: Vector2): Float
        abstract fun getOverlapHalfSize(halfCamW: Float, halfCamH: Float): Float
    }

    val axis: Axis = createAxis(rectObject)

    // Calculate the restricted range for camera position on one axis (X or Y).
    // Returns: (min, max) position for camera center on this axis after blocking is applied
    fun calculateRestrictedAxisRange(currentRange: ClosedFloatingPointRange<Float>,
                                     edgeRange: ClosedFloatingPointRange<Float>,
                                     playerPosition: Vector2,
                                     halfCameraWidth: Float,
                                     halfCameraHeight: Float
    ): Pair<Float, Float> {
        if (!isCameraBlockerOnScreen(halfCameraWidth, halfCameraHeight, playerPosition)) {
            return Pair(currentRange.start, currentRange.endInclusive)
        }

        val blockerMin: Float = axis.getMainMin(rectangle)
        val blockerMax: Float = axis.getMainMax(rectangle)
        val blockerHalfCameraSize: Float = axis.getMainHalfSize(halfCameraWidth, halfCameraHeight)
        val playerCoordinate: Float = axis.getMainCoordinate(playerPosition)

        var min: Float = currentRange.start
        var max: Float = currentRange.endInclusive

        // Restrict movement based on player position relative to blocker
        if (playerCoordinate < blockerMin) {
            val candidateMax = blockerMin - blockerHalfCameraSize
            if (candidateMax > edgeRange.start) {
                max = minOf(max, candidateMax)
            }
        }
        if (playerCoordinate > blockerMax) {
            val candidateMin = blockerMax + blockerHalfCameraSize
            if (candidateMin < edgeRange.endInclusive) {
                min = maxOf(min, candidateMin)
            }
        }

        return Pair(min, max)
    }

    private fun isCameraBlockerOnScreen(halfCameraWidth: Float,
                                        halfCameraHeight: Float,
                                        playerPosition: Vector2): Boolean {
        val blockerMin: Float = axis.getOverlapMin(rectangle)
        val blockerMax: Float = axis.getOverlapMax(rectangle)
        val blockerHalfCameraSize: Float = axis.getOverlapHalfSize(halfCameraWidth, halfCameraHeight)
        val playerCoordinate: Float = axis.getOverlapCoordinate(playerPosition)

        val cameraMin: Float = playerCoordinate - blockerHalfCameraSize
        val cameraMax: Float = playerCoordinate + blockerHalfCameraSize

        return cameraMax > blockerMin && cameraMin < blockerMax
    }

    private fun createAxis(rectObject: RectangleMapObject): Axis {
        return when {
            rectObject.name.equals("h", true) -> Axis.HORIZONTAL
            rectObject.name.equals("v", true) -> Axis.VERTICAL
            else -> throw IllegalArgumentException("CameraBlocker name '${rectObject.name}' unknown. Use 'h' or 'v'.")
        }
    }

}
