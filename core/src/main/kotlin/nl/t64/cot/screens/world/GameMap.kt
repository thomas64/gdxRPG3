package nl.t64.cot.screens.world

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.Vector2
import ktx.tiled.*
import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.resourceManager
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.screens.world.entity.Direction
import nl.t64.cot.screens.world.entity.EntityState
import nl.t64.cot.screens.world.mapobjects.*
import nl.t64.cot.screens.world.pathfinding.TiledGraph
import java.util.*


const val LIGHTMAP_REGION_MULTIPLIER = 10

private const val SOUND_LAYER = "sound"
private const val EVENT_LAYER = "event"
private const val CUTSCENE_LAYER = "cutscene"
private const val QUEST_LAYER = "quest"
private const val UPPER_TEXTURE_LAYER = "upper_texture"
private const val LOWER_TEXTURE_LAYER = "lower_texture"
private const val SAVE_LAYER = "save"
private const val NPC_LAYER = "npc"
private const val HERO_LAYER = "hero"
private const val ENEMY_LAYER = "enemy"
private const val COLLISION_LAYER = "collision"
private const val COLLISION_LOW_LAYER = "collision_low"
private const val SPAWN_LAYER = "spawn"
private const val PORTAL_LAYER = "portal"
private const val WARP_LAYER = "warp"
private const val REST_LAYER = "rest"
private const val LIGHTS_LAYER = "lights"

private const val STEP_SOUND_PROPERTY = "step_sound"
private const val DEFAULT_STEP_SOUND = "grass"

private const val BGM_PROPERTY = "bgm"
private const val BGS_PROPERTY = "bgs"
private const val DEFAULT_BG = "NONE"

class GameMap(val mapTitle: String) {

    val tiledMap: TiledMap = resourceManager.getTiledMapAsset(mapTitle)
    private val loader = GameMapLayerLoader(tiledMap)

    val parallaxBackground: TextureRegion = loader.loadParallaxBackground()
    val lightmapCamera: List<Texture> = loader.loadLightmapCamera()
    val lightmapMap: Sprite = loader.loadLightmapMap()
    val lightmapPlayer: Sprite? = loader.loadLightmapPlayer()
    private val defaultStepSound: String = tiledMap.property(STEP_SOUND_PROPERTY, DEFAULT_STEP_SOUND)

    lateinit var playerSpawnLocation: Vector2
    lateinit var playerSpawnDirection: Direction
    private val tiledGraphs: EnumMap<EntityState, TiledGraph> = EnumMap(EntityState::class.java)

    val npcs: List<GameMapNpc> = loader.loadLayer(NPC_LAYER) { GameMapNpc(it) }
    val heroes: List<GameMapHero> = loader.loadLayer(HERO_LAYER, { gameData.heroes.contains(it.name) }, { GameMapHero(it) })
    val enemies: List<GameMapEnemy> = loader.loadLayer(ENEMY_LAYER) { GameMapEnemy(it) }
    val lights: List<GameMapLight> = loader.loadLayer(LIGHTS_LAYER) { GameMapLight(it) }
    val questBlockers: List<GameMapConditionBlocker> = loader.equalsIgnoreCase(QUEST_LAYER, "blocker") { GameMapConditionBlocker(it) }
    val upperTextures: List<GameMapConditionTexture> = loader.loadTextureLayer(UPPER_TEXTURE_LAYER) { GameMapConditionTexture(it) }
    val lowerTextures: List<GameMapConditionTexture> = loader.loadTextureLayer(LOWER_TEXTURE_LAYER) { GameMapConditionTexture(it) }
    val sparkles: List<GameMapSparkle> = loader.startsWith(REST_LAYER, "sparkle") { GameMapSparkle(it) }
    val chests: List<RectangleMapObject> = loader.startsWith(REST_LAYER, "chest")
    val doors: List<RectangleMapObject> = loader.startsWith(REST_LAYER, "door")
    val storage: List<GameMapStorage> = loader.startsWith(REST_LAYER, "storage") { GameMapStorage(it) }

    private val sounds: List<RectangleMapObject> = loader.loadLayer(SOUND_LAYER)
    private val blockers: List<GameMapBlocker> = loader.loadLayer(COLLISION_LAYER) { GameMapBlocker(it) }
    private val lowBlockers: List<GameMapBlockerLow> = loader.loadLayer(COLLISION_LOW_LAYER) { GameMapBlockerLow(it) }
    private val eventDiscovers: List<GameMapEventDiscover> = loader.equalsIgnoreCase(EVENT_LAYER, "discover") { GameMapEventDiscover(it) }
    private val eventCheckers: List<GameMapEventChecker> = loader.equalsIgnoreCase(EVENT_LAYER, "check") { GameMapEventChecker(it) }
    private val cutsceneDiscovers: List<GameMapCutscene> = loader.equalsIgnoreCase(CUTSCENE_LAYER, "discover") { GameMapCutscene(it) }
    private val questDiscovers: List<GameMapQuestDiscover> = loader.equalsIgnoreCase(QUEST_LAYER, "discover") { GameMapQuestDiscover(it) }
    private val questCheckers: List<GameMapQuestChecker> = loader.equalsIgnoreCase(QUEST_LAYER, "check") { GameMapQuestChecker(it) }
    private val notes: List<GameMapNote> = loader.startsWith(REST_LAYER, "note") { GameMapNote(it) }
    private val savePoints: List<GameMapSavePoint> = loader.loadLayer(SAVE_LAYER) { GameMapSavePoint(it) }
    private val spawnPoints: List<GameMapSpawnPoint> = loader.loadLayer(SPAWN_LAYER) { GameMapSpawnPoint(it) }
    private val portals: List<GameMapRelocator> = loader.loadLayer(PORTAL_LAYER) { GameMapPortal(it, mapTitle) }
    private val warpPoints: List<GameMapRelocator> = loader.loadLayer(WARP_LAYER) { GameMapWarpPoint(it, mapTitle) }

    val bgm: AudioEvent = tiledMap.bgm
    val bgs: List<AudioEvent> = tiledMap.bgs
    val pixelWidth: Float = tiledMap.totalWidth().toFloat()
    val pixelHeight: Float = tiledMap.totalHeight().toFloat()
    val width: Int = tiledMap.width
    val height: Int = tiledMap.height

    fun setTiledGraphs() {
        tiledGraphs[EntityState.WALKING] = TiledGraph(tiledMap.width, tiledMap.height, EntityState.WALKING)
        tiledGraphs[EntityState.FLYING] = TiledGraph(tiledMap.width, tiledMap.height, EntityState.FLYING)
    }

    fun getTiledGraph(state: EntityState): TiledGraph {
        return tiledGraphs[state] ?: tiledGraphs[EntityState.WALKING]!!
    }

    fun getUnderground(point: Vector2): String {
        return sounds
            .filter { it.rectangle.contains(point) }
            .map { it.name }
            .firstOrNull() ?: defaultStepSound
    }

    fun setPlayerSpawnLocationForNewLoad(mapTitle: String) {
        val dummyObject = RectangleMapObject()
        dummyObject.name = mapTitle
        val spawnForNewLoadPortal = GameMapPortal(dummyObject, mapTitle)
        setPlayerSpawnLocation(spawnForNewLoadPortal)
    }

    fun setPlayerSpawnLocation(portal: GameMapRelocator) {
        spawnPoints.filter { it.isInConnectionWith(portal) }
            .forEach {
                playerSpawnLocation = Vector2(it.x, it.y)
                setPlayerSpawnDirection(portal, it)
                return
            }
    }

    private fun setPlayerSpawnDirection(portal: GameMapRelocator, spawnPoint: GameMapSpawnPoint) {
        playerSpawnDirection = if (spawnPoint.direction == Direction.NONE) {
            portal.enterDirection
        } else {
            spawnPoint.direction
        }
    }

    fun isOutsideMap(point: Vector2): Boolean {
        return point.x < 0 || point.x >= tiledMap.width
                || point.y < 0 || point.y >= tiledMap.height
    }

    fun dispose() {
        tiledMap.dispose()
    }

    fun debug(shapeRenderer: ShapeRenderer) {

        shapeRenderer.color = Color.YELLOW

        listOf(blockers, lowBlockers, questBlockers)
            .flatten()
            .map { it.rectangle }
            .forEach { shapeRenderer.rect(it.x, it.y, it.width, it.height) }

        shapeRenderer.color = Color.BLUE

        listOf(
            portals, spawnPoints, npcs, heroes, enemies, eventDiscovers, eventCheckers, cutsceneDiscovers,
            questDiscovers, questCheckers, notes, sparkles
        )
            .flatten()
            .map { it.rectangle }
            .forEach { shapeRenderer.rect(it.x, it.y, it.width, it.height) }

        listOf(chests, doors)
            .flatten()
            .map { it.rectangle }
            .forEach { shapeRenderer.rect(it.x, it.y, it.width, it.height) }
    }

}

val TiledMap.bgm: AudioEvent
    get() {
        val audioEvent: String = property(BGM_PROPERTY, DEFAULT_BG)
        return AudioEvent.valueOf(audioEvent.uppercase())
    }

val TiledMap.bgs: List<AudioEvent>
    get() {
        val audioEvents: String = property(BGS_PROPERTY, DEFAULT_BG)
        return audioEvents
            .uppercase()
            .split(",")
            .map { it.trim() }
            .map { AudioEvent.valueOf(it) }
    }
