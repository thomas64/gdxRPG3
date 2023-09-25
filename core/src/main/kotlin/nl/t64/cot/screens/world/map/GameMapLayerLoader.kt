package nl.t64.cot.screens.world.map

import com.badlogic.gdx.maps.MapLayer
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.objects.TextureMapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import ktx.tiled.type


internal class GameMapLayerLoader(private val tiledMap: TiledMap) {

    private val noFilter: (RectangleMapObject) -> Boolean = { true }
    private val noMapping: (RectangleMapObject) -> RectangleMapObject = { it }

    fun wholeLayer(layerName: String): List<RectangleMapObject> {
        return getMapLayer(layerName)?.let { getMapObjectsFrom(it, noFilter, noMapping) } ?: emptyList()
    }

    fun <T> wholeLayer(layerName: String, mapper: (RectangleMapObject) -> T): List<T> {
        return getMapLayer(layerName)?.let { getMapObjectsFrom(it, noFilter, mapper) } ?: emptyList()
    }

    fun nameStartsWith(layerName: String, match: String): List<RectangleMapObject> {
        val filter: (RectangleMapObject) -> Boolean = { it.name.startsWith(match) }
        return getMapLayer(layerName)?.let { getMapObjectsFrom(it, filter, noMapping) } ?: emptyList()
    }

    fun <T> nameStartsWith(layerName: String, match: String, mapper: (RectangleMapObject) -> T): List<T> {
        val filter: (RectangleMapObject) -> Boolean = { it.name.startsWith(match) }
        return getMapLayer(layerName)?.let { getMapObjectsFrom(it, filter, mapper) } ?: emptyList()
    }

    fun <T> nameEqualsIgnoreCase(layerName: String, match: String, mapper: (RectangleMapObject) -> T): List<T> {
        val filter: (RectangleMapObject) -> Boolean = { it.name.equals(match, true) }
        return getMapLayer(layerName)?.let { getMapObjectsFrom(it, filter, mapper) } ?: emptyList()
    }

    fun <T> typeEqualsIgnoreCase(layerName: String, match: String, mapper: (RectangleMapObject) -> T): List<T> {
        val filter: (RectangleMapObject) -> Boolean = { it.type.equals(match, true) }
        return getMapLayer(layerName)?.let { getMapObjectsFrom(it, filter, mapper) } ?: emptyList()
    }

    fun <T> partOfLayer(layerName: String, filter: (RectangleMapObject) -> Boolean, mapper: (RectangleMapObject) -> T): List<T> {
        return getMapLayer(layerName)?.let { getMapObjectsFrom(it, filter, mapper) } ?: emptyList()
    }

    fun <T> wholeTextureLayer(layerName: String, mapper: (TextureMapObject) -> T): List<T> {
        return getMapLayer(layerName)?.let { getTextureObjectsFrom(it, mapper) } ?: emptyList()
    }

    private fun <T> getMapObjectsFrom(mapLayer: MapLayer, filter: (RectangleMapObject) -> Boolean, mapper: (RectangleMapObject) -> T): List<T> {
        return mapLayer.objects.map { it as RectangleMapObject }.filter(filter).map(mapper)
    }

    private fun <T> getTextureObjectsFrom(mapLayer: MapLayer, mapper: (TextureMapObject) -> T): List<T> {
        return mapLayer.objects.map { it as TextureMapObject }.map(mapper)
    }

    private fun getMapLayer(layerName: String): MapLayer? {
        return tiledMap.layers.get(layerName)
    }

}
