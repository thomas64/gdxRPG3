package nl.t64.cot.screens.world.map

import com.badlogic.gdx.math.Vector2
import nl.t64.cot.Utils.mapManager


fun Vector2.isOutsideMap(): Boolean {
    return mapManager.currentMap.isOutsideMap(this)
}
