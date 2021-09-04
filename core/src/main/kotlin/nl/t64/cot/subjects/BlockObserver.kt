package nl.t64.cot.subjects

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import nl.t64.cot.screens.world.entity.EntityState


interface BlockObserver {

    fun getBlockerFor(boundingBox: Rectangle, state: EntityState): Rectangle?
    fun isBlocking(point: Vector2, state: EntityState): Boolean

}
