package nl.t64.cot.screens.world.mapobjects

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.reflect.ClassReflection
import nl.t64.cot.screens.world.atmosphere.LitSchedule
import nl.t64.cot.screens.world.lighting.Lightmap


// A rectangle of this size in Tiled draws the light texture at its own size; other sizes scale it along.
private const val FULL_SIZE_RECTANGLE = 48f

class GameMapLight(rectObject: RectangleMapObject) : GameMapObject(rectObject.rectangle) {

    private val texture: Texture = Lightmap.LIGHT_OBJECT.texture
    private val color: Color = rectObject.name?.toColor() ?: Color.WHITE
    private val center: Vector2 = rectObject.getCenter()
    private val width: Float = texture.width * rectangle.width / FULL_SIZE_RECTANGLE
    private val height: Float = texture.height * rectangle.height / FULL_SIZE_RECTANGLE
    private val x: Float = center.x - width / 2f
    private val y: Float = center.y - height / 2f
    private val litSchedule = LitSchedule(rectObject.properties)

    fun render(batch: Batch) {
        if (!litSchedule.isLit()) return

        batch.color = color
        batch.draw(texture, x, y, width, height)
        batch.color = Color.WHITE
    }

    private fun String.toColor(): Color {
        return ClassReflection.getField(Color::class.java, this.uppercase()).get(Color::class.java) as Color
    }

}
