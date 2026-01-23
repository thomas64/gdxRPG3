package nl.t64.cot.screens.world.mapobjects

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.math.Vector2
import ktx.tiled.type
import nl.t64.cot.constants.Constant


class GameMapIcon(rectObject: RectangleMapObject) : GameMapObject(rectObject.rectangle) {

    private val position: Vector2 = rectObject.getCenter()
    private val color: Color = Constant.MINIMAP_ICONS[rectObject.type]!!

    fun renderOnMiniMap(batch: Batch, shapeRenderer: ShapeRenderer) {
        shapeRenderer.color = color
        shapeRenderer.circle(position.x, position.y, Constant.HALF_TILE_SIZE)
    }

}
