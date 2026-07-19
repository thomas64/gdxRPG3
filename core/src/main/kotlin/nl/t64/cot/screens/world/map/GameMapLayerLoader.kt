package nl.t64.cot.screens.world.map

import com.badlogic.gdx.maps.MapLayer
import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.objects.TextureMapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import ktx.tiled.type


internal class GameMapLayerLoader(
    private val tiledMap: TiledMap
) {

    fun loadAllRectanglesFromLayer(layerName: String): List<RectangleMapObject> {
        val layer: MapLayer = tiledMap.layers.get(layerName) ?: return emptyList()
        return layer.objects
            .map { it as RectangleMapObject }
    }

    fun <T> loadAllRectanglesAndTransform(layerName: String,
                                          transform: (RectangleMapObject) -> T
    ): List<T> {
        val layer: MapLayer = tiledMap.layers.get(layerName) ?: return emptyList()
        return layer.objects
            .map { it as RectangleMapObject }
            .map(transform)
    }

    fun <T> loadAllObjectsAndTransform(layerName: String,
                                       transform: (MapObject) -> T
    ): List<T> {
        val layer: MapLayer = tiledMap.layers.get(layerName) ?: return emptyList()
        return layer.objects.map(transform)
    }

    fun <T> loadAllTexturesAndTransform(layerName: String,
                                        transform: (TextureMapObject) -> T
    ): List<T> {
        val layer: MapLayer = tiledMap.layers.get(layerName) ?: return emptyList()
        return layer.objects
            .map { it as TextureMapObject }
            .map(transform)
    }

    fun loadWhereNameStartsWith(layerName: String,
                                namePrefix: String
    ): List<RectangleMapObject> {
        val layer: MapLayer = tiledMap.layers.get(layerName) ?: return emptyList()
        return layer.objects
            .map { it as RectangleMapObject }
            .filter { it.name.startsWith(namePrefix) }
    }

    fun <T> loadWhereNameStartsWithAndTransform(layerName: String,
                                                namePrefix: String,
                                                transform: (RectangleMapObject) -> T
    ): List<T> {
        val layer: MapLayer = tiledMap.layers.get(layerName) ?: return emptyList()
        return layer.objects
            .map { it as RectangleMapObject }
            .filter { it.name.startsWith(namePrefix) }
            .map(transform)
    }

    fun <T> loadWhereNameEqualsAndTransform(layerName: String,
                                            name: String,
                                            transform: (RectangleMapObject) -> T
    ): List<T> {
        val layer: MapLayer = tiledMap.layers.get(layerName) ?: return emptyList()
        return layer.objects
            .map { it as RectangleMapObject }
            .filter { it.name.equals(name, ignoreCase = true) }
            .map(transform)
    }

    fun <T> loadWhereTypeEqualsAndTransform(layerName: String,
                                            type: String,
                                            transform: (RectangleMapObject) -> T
    ): List<T> {
        val layer: MapLayer = tiledMap.layers.get(layerName) ?: return emptyList()
        return layer.objects
            .map { it as RectangleMapObject }
            .filter { it.type.equals(type, ignoreCase = true) }
            .map(transform)
    }

    fun <T> loadWithCustomFilterAndTransform(layerName: String,
                                             filter: (RectangleMapObject) -> Boolean,
                                             transform: (RectangleMapObject) -> T
    ): List<T> {
        val layer: MapLayer = tiledMap.layers.get(layerName) ?: return emptyList()
        return layer.objects
            .map { it as RectangleMapObject }
            .filter(filter)
            .map(transform)
    }

}
