package nl.t64.cot.screens.world.entity

import com.badlogic.gdx.math.MathUtils


enum class Direction {
    NORTH,
    SOUTH,
    WEST,
    EAST,
    NORTH_WEST,
    NORTH_EAST,
    SOUTH_WEST,
    SOUTH_EAST,

    NONE;

    fun isNorth() = this in listOf(NORTH, NORTH_WEST, NORTH_EAST)
    fun isSouth() = this in listOf(SOUTH, SOUTH_WEST, SOUTH_EAST)
    fun isWest() = this in listOf(WEST, NORTH_WEST, SOUTH_WEST)
    fun isEast() = this in listOf(EAST, NORTH_EAST, SOUTH_EAST)

    companion object {
        fun getRandom(): Direction {
            val randomNumber = MathUtils.random(getAllDirectionsWithoutNONE())
            return entries[randomNumber]
        }

        private fun getAllDirectionsWithoutNONE(): Int = entries.size - 2
    }

}
