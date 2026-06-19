package nl.t64.cot.screens.world.entity

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2


interface OverheadMarker {

    fun update(dt: Float)
    fun render(batch: Batch, position: Vector2)

}
