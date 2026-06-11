package nl.t64.cot.screens.world.entity.events

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import nl.t64.cot.screens.world.entity.Direction


class OnBumpEvent(val biggerBoundingBox: Rectangle,
                  val checkRect: Rectangle,
                  val playerPosition: Vector2,
                  val playerDirection: Direction) : Event
