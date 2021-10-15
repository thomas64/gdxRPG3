package nl.t64.cot

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetLoaderParameters
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.MusicLoader
import com.badlogic.gdx.assets.loaders.SoundLoader
import com.badlogic.gdx.assets.loaders.TextureLoader
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.OrderedMap
import ktx.assets.getAsset
import ktx.assets.loadOnDemand
import ktx.assets.setLoader
import ktx.assets.unloadSafely
import ktx.collections.GdxMap
import ktx.json.fromJson


private const val FILE_LIST = "_files.txt"
private const val SPRITE_CONFIGS = "configs/sprites/"
private const val FILE_LIST_SPRITE_CONFIGS = SPRITE_CONFIGS + FILE_LIST
private const val SHOP_CONFIGS = "configs/shops/"
private const val ACADEMY_CONFIGS = "configs/academies/"
private const val SCHOOL_CONFIGS = "configs/schools/"
private const val CONFIG_SUFFIX = ".json"
private const val ATLAS_FILES = "sprites/"
private const val FILE_LIST_ATLAS_FILES = ATLAS_FILES + FILE_LIST
private const val ATLAS_FILES2 = "sprites/inventory/"
private const val FILE_LIST_ATLAS_FILES2 = ATLAS_FILES2 + FILE_LIST
private const val ATLAS_FILES3 = "sprites/spells/"
private const val FILE_LIST_ATLAS_FILES3 = ATLAS_FILES3 + FILE_LIST

class ResourceManager {

    private val assetManager = AssetManager().apply {
        setLoader(TmxMapLoader(fileHandleResolver))
        setLoader(FreeTypeFontGeneratorLoader(fileHandleResolver))
        setLoader(FreetypeFontLoader(fileHandleResolver), ".ttf")
        setLoader(TextureLoader(fileHandleResolver))
        setLoader(SoundLoader(fileHandleResolver))
        setLoader(MusicLoader(fileHandleResolver))
    }
    private val spriteConfigs = GdxMap<String, SpriteConfig>()
    private val atlasList = ArrayList<TextureAtlas>()
    private val json = Json()

    fun unloadAsset(assetFilenamePath: String) {
        assetManager.unloadSafely(assetFilenamePath)
    }

    fun getMapAsset(mapFilenamePath: String): TiledMap {
        return getAsset(mapFilenamePath)
    }

    fun getTrueTypeAsset(trueTypeFilenamePath: String, fontSize: Int): BitmapFont {
        val parameters = FreetypeFontLoader.FreeTypeFontLoaderParameter()
        parameters.fontFileName = trueTypeFilenamePath
        parameters.fontParameters.size = fontSize
        return getAsset(trueTypeFilenamePath, parameters)
    }

    fun getTextureAsset(textureFilenamePath: String): Texture {
        return getAsset(textureFilenamePath)
    }

    fun getSoundAsset(soundFilenamePath: String): Sound {
        return getAsset(soundFilenamePath)
    }

    fun getMusicAsset(musicFilenamePath: String): Music {
        return getAsset(musicFilenamePath)
    }

    private inline fun <reified T : Any> getAsset(path: String, parameters: AssetLoaderParameters<T>? = null): T {
        assetManager.loadOnDemand(path, parameters).finishLoading()
        return assetManager.getAsset(path)
    }

    fun getSpriteConfig(spriteId: String): SpriteConfig {
        if (spriteConfigs.isEmpty) {
            loadSpriteConfigs()
        }
        return spriteConfigs[spriteId]
    }

    private fun loadSpriteConfigs() {
        Gdx.files.internal(FILE_LIST_SPRITE_CONFIGS).readString()
            .split(System.lineSeparator())
            .map { Gdx.files.internal(SPRITE_CONFIGS + it) }
            .map { json.fromJson(GdxMap::class.java, SpriteConfig::class.java, it) }
            .forEach { spriteConfigs.putAll(it as GdxMap<String, SpriteConfig>) }
    }

    fun getAtlasTexture(atlasId: String): TextureRegion {
        if (atlasList.isEmpty()) {
            loadAtlasTexture(FILE_LIST_ATLAS_FILES, ATLAS_FILES)
            loadAtlasTexture(FILE_LIST_ATLAS_FILES2, ATLAS_FILES2)
            loadAtlasTexture(FILE_LIST_ATLAS_FILES3, ATLAS_FILES3)
        }
        return atlasList.mapNotNull { it.findRegion(atlasId) }.first()
    }

    private fun loadAtlasTexture(fileName: String, directory: String) {
        Gdx.files.internal(fileName).readString()
            .split(System.lineSeparator())
            .filter { it.isNotBlank() }
            .map { Gdx.files.internal(directory + it) }
            .map { TextureAtlas(it) }
            .forEach { atlasList.add(it) }
    }

    fun getShopInventory(shopId: String): List<String> {
        val fullFilenamePath = SHOP_CONFIGS + shopId + CONFIG_SUFFIX
        return json.fromJson(Gdx.files.internal(fullFilenamePath))
    }

    fun getAcademyInventory(academyId: String): GdxMap<String, Int> {
        val fullFileNamePath = ACADEMY_CONFIGS + academyId + CONFIG_SUFFIX
        return json.fromJson(OrderedMap::class.java, Int::class.java, Gdx.files.internal(fullFileNamePath)) as OrderedMap<String, Int>
    }

    fun getSchoolInventory(schoolId: String): GdxMap<String, Int> {
        val fullFileNamePath = SCHOOL_CONFIGS + schoolId + CONFIG_SUFFIX
        return json.fromJson(OrderedMap::class.java, Int::class.java, Gdx.files.internal(fullFileNamePath)) as OrderedMap<String, Int>
    }

}
