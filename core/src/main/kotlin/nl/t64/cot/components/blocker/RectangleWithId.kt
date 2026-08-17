package nl.t64.cot.components.blocker

import com.badlogic.gdx.math.Rectangle


data class RectangleWithId(
    val id: String = "",
    val rectangle: Rectangle = Rectangle()
)
