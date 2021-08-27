package nl.t64.cot.screens.world

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.maps.MapLayer
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.objects.TextureMapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import ktx.tiled.property
import ktx.tiled.propertyOrNull
import ktx.tiled.type
import nl.t64.cot.Utils


private const val BACKGROUND_REGION_MULTIPLIER = 2
private const val PARALLAX_BACKGROUND = "parallax_background"
private const val LIGHTMAP_CAMERA_PROPERTY = "lightmap_camera"
private const val LIGHTMAP_MAP_PROPERTY = "lightmap_map"
private const val LIGHTMAP_PLAYER_PROPERTY = "lightmap_player"
private const val DEFAULT_LIGHTMAP = "default"

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

    fun loadParallaxBackground(): TextureRegion {
        val id = tiledMap.property(PARALLAX_BACKGROUND, DEFAULT_LIGHTMAP)
        val texture = Utils.createLightmap(id)
        texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat)
        val region = TextureRegion(texture)
        region.regionWidth = texture.width * BACKGROUND_REGION_MULTIPLIER
        return region
    }

    fun loadLightmapCamera(): List<Texture> {
        val lightmapStrings = tiledMap.property(LIGHTMAP_CAMERA_PROPERTY, DEFAULT_LIGHTMAP)
        return lightmapStrings
            .split(",")
            .map { it.trim() }
            .map { Utils.createLightmap(it) }
    }

    fun loadLightmapMap(): Sprite {
        val id = tiledMap.property(LIGHTMAP_MAP_PROPERTY, DEFAULT_LIGHTMAP)
        val texture = Utils.createLightmap(id)
        texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat)
        val region = TextureRegion(texture)
        region.regionWidth = texture.width * LIGHTMAP_REGION_MULTIPLIER
        region.regionHeight = texture.height * LIGHTMAP_REGION_MULTIPLIER
        return Sprite(region)
    }

    fun loadLightmapPlayer(): Sprite? {
        return tiledMap.propertyOrNull<String>(LIGHTMAP_PLAYER_PROPERTY)?.let { Sprite(Utils.createLightmap(it)) }
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
