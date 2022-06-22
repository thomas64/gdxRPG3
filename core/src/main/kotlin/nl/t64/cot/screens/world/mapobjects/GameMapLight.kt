package nl.t64.cot.screens.world.mapobjects

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.reflect.ClassReflection
import nl.t64.cot.Utils


private const val LIGHTMAP_ID = "light_object"

class GameMapLight(rectObject: RectangleMapObject) {

    private val texture: Texture = Utils.createLightmap(LIGHTMAP_ID)
    private val color: Color = rectObject.name?.toColor() ?: Color.WHITE
    private val center: Vector2 = rectObject.getCenter()
    private val x: Float = center.x - texture.width / 2f
    private val y: Float = center.y - texture.height / 2f

    fun render(batch: Batch) {
        batch.color = color
        batch.draw(texture, x, y)
        batch.color = Color.WHITE
    }

    private fun String.toColor(): Color {
        return ClassReflection.getField(Color::class.java, uppercase()).get(Color::class.java) as Color
    }

    private fun RectangleMapObject.getCenter(): Vector2 {
        return rectangle.getCenter(Vector2(rectangle.x, rectangle.y))
    }

}
