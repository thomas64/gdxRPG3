package nl.t64.cot.subjects

import com.badlogic.gdx.math.Rectangle
import nl.t64.cot.screens.world.entity.Direction


interface CollisionObserver {

    fun onNotifyCollision(playerBoundingBox: Rectangle, playerDirection: Direction)

}
