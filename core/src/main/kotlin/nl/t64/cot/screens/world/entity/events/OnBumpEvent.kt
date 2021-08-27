package nl.t64.cot.screens.world.entity.events

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2


class OnBumpEvent(val biggerBoundingBox: Rectangle,
                  val checkRect: Rectangle,
                  val playerPosition: Vector2) : Event
