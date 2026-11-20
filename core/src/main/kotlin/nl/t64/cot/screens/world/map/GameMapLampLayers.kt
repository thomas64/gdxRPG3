package nl.t64.cot.screens.world.map

import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import nl.t64.cot.screens.world.atmosphere.LitSchedule


/**
 * A pair of tile layers, like under_lit and under_unlit, of which only one is ever rendered.
 * In Tiled they must come after the nine regular tile layers, because those are rendered by index.
 */
class GameMapLampLayers(tiledMap: TiledMap, layerPrefix: String) {

    private val litLayer: TiledMapTileLayer? = tiledMap.layers.get("${layerPrefix}_lit") as TiledMapTileLayer?
    private val unlitLayer: TiledMapTileLayer? = tiledMap.layers.get("${layerPrefix}_unlit") as TiledMapTileLayer?
    private val litSchedule: LitSchedule? = litLayer?.let { LitSchedule(it.properties) }

    /** The unlit layer follows the litFrom time of the lit layer. */
    fun getCurrentLayer(): TiledMapTileLayer? {
        return if (litSchedule?.isLit() == true) litLayer else unlitLayer
    }

}
