package nl.t64.cot.screens.world

import com.badlogic.gdx.maps.MapLayer
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.objects.TextureMapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import ktx.tiled.type


internal class GameMapLayerLoader(private val tiledMap: TiledMap) {

    fun <T> loadLayer(layerName: String
    ): List<T> {
        return loadLayer(layerName,
                         { it as T })
    }

    fun <T> loadLayer(layerName: String,
                      mapper: (RectangleMapObject) -> T
    ): List<T> {
        return loadLayer(layerName,
                         { true },
                         mapper)
    }

    fun <T> startsWith(layerName: String,
                       match: String
    ): List<T> {
        return loadLayer(layerName,
                         { it.name.startsWith(match) },
                         { it as T })
    }

    fun <T> startsWith(layerName: String,
                       match: String,
                       mapper: (RectangleMapObject) -> T
    ): List<T> {
        return loadLayer(layerName,
                         { it.name.startsWith(match) },
                         mapper)
    }

    fun <T> equalsIgnoreCase(layerName: String,
                             match: String,
                             mapper: (RectangleMapObject) -> T
    ): List<T> {
        return loadLayer(layerName,
                         { it.type.equals(match, true) },
                         mapper)
    }

    fun <T> loadLayer(layerName: String,
                      filter: (RectangleMapObject) -> Boolean,
                      mapper: (RectangleMapObject) -> T
    ): List<T> {
        return getMapLayer(layerName)?.let { createRectObjectsList(it, filter, mapper) } ?: emptyList()
    }

    fun <T> loadTextureLayer(
        layerName: String,
        mapper: (TextureMapObject) -> T
    ): List<T> {
        return getMapLayer(layerName)?.let { createTextureObjectsList(it, mapper) } ?: emptyList()
    }

    private fun <T> createRectObjectsList(mapLayer: MapLayer,
                                          filter: (RectangleMapObject) -> Boolean,
                                          mapper: (RectangleMapObject) -> T
    ): List<T> {
        return mapLayer.objects
            .map { it as RectangleMapObject }
            .filter(filter)
            .map(mapper)
    }

    private fun <T> createTextureObjectsList(mapLayer: MapLayer,
                                             mapper: (TextureMapObject) -> T
    ): List<T> {
        return mapLayer.objects
            .map { it as TextureMapObject }
            .map(mapper)
    }

    private fun getMapLayer(layerName: String): MapLayer? {
        return tiledMap.layers.get(layerName)
    }

}
