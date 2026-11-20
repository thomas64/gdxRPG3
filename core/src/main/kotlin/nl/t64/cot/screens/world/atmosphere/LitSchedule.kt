package nl.t64.cot.screens.world.atmosphere

import com.badlogic.gdx.maps.MapProperties
import nl.t64.cot.Utils.gameData


private const val LIT_FROM_PROPERTY = "litFrom"

/** Without a litFrom property or a default, it is lit all day. litFrom = "never" keeps it unlit. */
class LitSchedule(
    properties: MapProperties,
    defaultLitFrom: String? = null
) {

    private val litFrom: String? = properties.get(LIT_FROM_PROPERTY, String::class.java) ?: defaultLitFrom

    fun isLit(): Boolean {
        return when (litFrom) {
            null -> true
            NEVER -> false
            else -> gameData.clock.isCurrentTimeAfter(litFrom)
        }
    }

    companion object {
        const val NEVER = "never"
    }

}
