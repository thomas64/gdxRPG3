package nl.t64.cot.screens.world.mapobjects

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.reflect.ClassReflection
import nl.t64.cot.Utils.gameData
import nl.t64.cot.screens.world.atmosphere.LitSchedule
import nl.t64.cot.screens.world.entity.GraphicsDoor
import nl.t64.cot.screens.world.lighting.Lightmap


// A rectangle of this size in Tiled draws the light texture at its own size; other sizes scale it along.
private const val FULL_SIZE_RECTANGLE = 48f

class GameMapLight(rectObject: RectangleMapObject,
                   doors: List<RectangleMapObject>
) : GameMapObject(rectObject.rectangle) {

    private val texture: Texture = Lightmap.LIGHT_OBJECT.texture
    private val color: Color = rectObject.name?.toColor() ?: Color.WHITE
    private val center: Vector2 = rectObject.getCenter()
    private val width: Float = texture.width * rectangle.width / FULL_SIZE_RECTANGLE
    private val height: Float = texture.height * rectangle.height / FULL_SIZE_RECTANGLE
    private val x: Float = center.x - width / 2f
    private val y: Float = center.y - height / 2f
    private val litSchedule = LitSchedule(rectObject.properties)
    // A light whose center lies within a door belongs to that door, and only shines while it is open.
    private val doorId: String? = doors.find { it.rectangle.contains(center) }?.name
    private var brightness: Float = if (isDoorClosed()) 0f else 1f

    fun update(dt: Float) {
        val fadeStep: Float = dt / GraphicsDoor.ANIMATION_DURATION
        val change: Float = if (isDoorClosed()) -fadeStep else fadeStep
        brightness = MathUtils.clamp(brightness + change, 0f, 1f)
    }

    fun render(batch: Batch) {
        if (!litSchedule.isLit() || brightness == 0f) return

        batch.setColor(color.r * brightness, color.g * brightness, color.b * brightness, 1f)
        batch.draw(texture, x, y, width, height)
        batch.color = Color.WHITE
    }

    private fun isDoorClosed(): Boolean {
        return doorId != null && gameData.doors.getDoor(doorId).isClosed
    }

    private fun String.toColor(): Color {
        return ClassReflection.getField(Color::class.java, this.uppercase()).get(Color::class.java) as Color
    }

}
