package nl.t64.cot.screens.world.entity

import com.badlogic.gdx.math.MathUtils


enum class Direction {
    NORTH,
    SOUTH,
    WEST,
    EAST,

    NONE;

    companion object {
        fun getRandom(): Direction {
            val randomNumber = MathUtils.random(getAllDirectionsWithoutNONE())
            return values()[randomNumber]
        }

        private fun getAllDirectionsWithoutNONE(): Int = values().size - 2
    }

}
