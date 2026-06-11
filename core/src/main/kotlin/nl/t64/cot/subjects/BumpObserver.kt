package nl.t64.cot.subjects

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import nl.t64.cot.screens.world.entity.Direction


interface BumpObserver {

    fun onNotifyBump(biggerBoundingBox: Rectangle, checkRect: Rectangle, playerPosition: Vector2, playerDirection: Direction)

}
