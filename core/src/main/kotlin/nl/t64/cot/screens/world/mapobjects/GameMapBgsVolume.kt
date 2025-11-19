package nl.t64.cot.screens.world.mapobjects

import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.math.Vector2
import ktx.tiled.type
import nl.t64.cot.audio.AudioEvent
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt


class GameMapBgsVolume(rectObject: RectangleMapObject) : GameMapObject(rectObject.rectangle) {

    private val bgsEvent: AudioEvent = AudioEvent.valueOf(rectObject.name.uppercase())
    private val effectRadius: Float = rectObject.type?.toFloatOrNull() ?: 100f

    fun getVolumeAdjustment(playerPosition: Vector2): Pair<AudioEvent, Float> {
        if (rectangle.contains(playerPosition)) return Pair(bgsEvent, bgsEvent.volume)

        val distance: Float = calculateDistanceToRectangle(playerPosition)
        val adjustment: Float = calculateAdjustment(distance)

        return Pair(bgsEvent, adjustment)
    }

    private fun calculateDistanceToRectangle(point: Vector2): Float {
        val closestX: Float = max(rectangle.x, min(point.x, rectangle.x + rectangle.width))
        val closestY: Float = max(rectangle.y, min(point.y, rectangle.y + rectangle.height))
        val dx: Float = point.x - closestX
        val dy: Float = point.y - closestY
        return sqrt(dx * dx + dy * dy)
    }

    private fun calculateAdjustment(distance: Float): Float {
        if (distance > effectRadius) return -bgsEvent.volume

        val falloff: Float = 1f - (distance / effectRadius)
        return (bgsEvent.volume * falloff) - bgsEvent.volume
    }

}
