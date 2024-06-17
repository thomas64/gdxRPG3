package nl.t64.cot.screens.world.map

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.Vector2
import ktx.tiled.*
import nl.t64.cot.Utils
import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.resourceManager
import nl.t64.cot.Utils.screenManager
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.screens.cutscene.CutsceneScreen
import nl.t64.cot.screens.cutscene.SceneIntro
import nl.t64.cot.screens.world.entity.Direction
import nl.t64.cot.screens.world.entity.EntityState
import nl.t64.cot.screens.world.mapobjects.*
import nl.t64.cot.screens.world.pathfinding.TiledGraph
import java.util.*
import kotlin.concurrent.thread


private const val SOUND_LAYER = "sound"
private const val SCHEDULED_LAYER = "scheduled"
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
private const val PARTICLES_LAYER = "particles"

private const val STEP_SOUND_PROPERTY = "step_sound"
private const val DEFAULT_STEP_SOUND = "grass"

private const val BGM_PROPERTY = "bgm"
private const val BGS_PROPERTY = "bgs"
private const val DEFAULT_BG = "NONE"

private const val PARALLAX_BACKGROUND = "parallax_background"
private const val LIGHTMAP_CAMERA_PROPERTY = "lightmap_camera"
private const val LIGHTMAP_MAP_PROPERTY = "lightmap_map"
private const val LIGHTMAP_PLAYER_PROPERTY = "lightmap_player"
private const val DEFAULT_LIGHTMAP = "default"

class GameMap(val mapTitle: String) {

    val tiledMap: TiledMap = resourceManager.getTiledMapAsset(mapTitle)
    private val loader = GameMapLayerLoader(tiledMap)

    val bgm: AudioEvent get() = tiledMap.bgm
    val bgs: List<AudioEvent> = tiledMap.bgs
    val pixelWidth: Float = tiledMap.totalWidth().toFloat()
    val pixelHeight: Float = tiledMap.totalHeight().toFloat()
    val width: Int = tiledMap.width
    val height: Int = tiledMap.height

    val parallaxBackground: GameMapParallaxBackground? = tiledMap.propertyOrNull<String>(PARALLAX_BACKGROUND)
        ?.let { GameMapParallaxBackground(it) }
    val lightmapCamera = GameMapLightmapCamera(tiledMap.property(LIGHTMAP_CAMERA_PROPERTY, DEFAULT_LIGHTMAP), pixelWidth, pixelHeight)
    val lightmapMap = GameMapLightmapMap(tiledMap.property(LIGHTMAP_MAP_PROPERTY, DEFAULT_LIGHTMAP))
    val lightmapPlayer: Sprite? = tiledMap.propertyOrNull<String>(LIGHTMAP_PLAYER_PROPERTY)
        ?.let { Sprite(Utils.createLightmap(it)) }
    private val defaultStepSound: String = tiledMap.property(STEP_SOUND_PROPERTY, DEFAULT_STEP_SOUND)

    lateinit var playerSpawnLocation: Vector2
    lateinit var playerSpawnDirection: Direction
    private val tiledGraphs: EnumMap<EntityState, TiledGraph> = EnumMap(EntityState::class.java)

    val schedules: List<RectangleMapObject> = loader.wholeLayer(SCHEDULED_LAYER)
    val npcs: List<GameMapNpc> = loader.wholeLayer(NPC_LAYER) { GameMapNpc(it) }
    val heroes: List<GameMapHero> = loader.partOfLayer(HERO_LAYER, { gameData.heroes.contains(it.name) }, { GameMapHero(it) })
    val enemies: List<GameMapEnemy> = loader.wholeLayer(ENEMY_LAYER) { GameMapEnemy(it) }
    val lights: List<GameMapLight> = loader.wholeLayer(LIGHTS_LAYER) { GameMapLight(it) }
    val torches: List<GameMapParticle> = loader.nameEqualsIgnoreCase(PARTICLES_LAYER, "torch") { GameMapParticle(it) }
    val conditionBlockers: List<GameMapConditionBlocker> = loader.typeEqualsIgnoreCase(QUEST_LAYER, "blocker") { GameMapConditionBlocker(it) }
    val upperTextures: List<GameMapConditionTexture> = loader.wholeTextureLayer(UPPER_TEXTURE_LAYER) { GameMapConditionTexture(it) }
    val lowerTextures: List<GameMapConditionTexture> = loader.wholeTextureLayer(LOWER_TEXTURE_LAYER) { GameMapConditionTexture(it) }
    val sparkles: List<GameMapSparkle> = loader.nameStartsWith(REST_LAYER, "sparkle") { GameMapSparkle(it) }
    val chests: List<RectangleMapObject> = loader.nameStartsWith(REST_LAYER, "chest")
    val doors: List<RectangleMapObject> = loader.nameStartsWith(REST_LAYER, "door")
    val storage: List<GameMapStorage> = loader.nameStartsWith(REST_LAYER, "storage") { GameMapStorage(it) }

    private val sounds: List<RectangleMapObject> = loader.wholeLayer(SOUND_LAYER)
    private val blockers: List<GameMapBlocker> = loader.wholeLayer(COLLISION_LAYER) { GameMapBlocker(it) }
    private val lowBlockers: List<GameMapBlockerLow> = loader.wholeLayer(COLLISION_LOW_LAYER) { GameMapBlockerLow(it) }
    private val eventDiscovers: List<GameMapEventDiscover> = loader.typeEqualsIgnoreCase(EVENT_LAYER, "discover") { GameMapEventDiscover(it) }
    private val eventCheckers: List<GameMapEventChecker> = loader.typeEqualsIgnoreCase(EVENT_LAYER, "check") { GameMapEventChecker(it) }
    private val cutsceneDiscovers: List<GameMapCutscene> = loader.typeEqualsIgnoreCase(CUTSCENE_LAYER, "discover") { GameMapCutscene(it) }
    private val questDiscovers: List<GameMapQuestDiscover> = loader.typeEqualsIgnoreCase(QUEST_LAYER, "discover") { GameMapQuestDiscover(it) }
    private val questCheckers: List<GameMapQuestChecker> = loader.typeEqualsIgnoreCase(QUEST_LAYER, "check") { GameMapQuestChecker(it) }
    private val notes: List<GameMapNote> = loader.nameStartsWith(REST_LAYER, "note") { GameMapNote(it) }
    private val savePoints: List<GameMapSavePoint> = loader.wholeLayer(SAVE_LAYER) { GameMapSavePoint(it) }
    private val spawnPoints: List<GameMapSpawnPoint> = loader.wholeLayer(SPAWN_LAYER) { GameMapSpawnPoint(it) }
    private val portals: List<GameMapRelocator> = loader.wholeLayer(PORTAL_LAYER) { GameMapPortal(it, mapTitle) }

    // warpPoints have been replaced by warpPortals. if needed again, they need to be added another way on the tmx map.
//    private val warpPoints: List<GameMapRelocator> = loader.loadLayer(WARP_LAYER) { GameMapWarpPoint(it, mapTitle) }
    private val warpPortals: List<GameMapWarpPortal> = loader.wholeLayer(WARP_LAYER) { GameMapWarpPortal(it, mapTitle) }

    fun setTiledGraphs() {
        if (enemies.any { it.isMeetingConditions() }) {
            thread {
                tiledGraphs[EntityState.WALKING] = TiledGraph(width, height, EntityState.WALKING)
                tiledGraphs[EntityState.FLYING] = TiledGraph(width, height, EntityState.FLYING)
            }
        }
    }

    fun getTiledGraph(state: EntityState): TiledGraph? {
        return tiledGraphs[state] ?: tiledGraphs[EntityState.WALKING]
    }

    fun getUnderground(point: Vector2): String {
        return sounds
            .filter { it.rectangle.contains(point) }
            .map { it.name }
            .firstOrNull() ?: defaultStepSound
    }

    fun setPlayerSpawnLocationForNewLoad(mapTitle: String) {
        val spawnForNewLoadPortal = GameMapRelocator.createPortalForNewLoad(mapTitle)
        setPlayerSpawnLocation(spawnForNewLoadPortal)
    }

    fun setPlayerSpawnLocationForWarpPortal() {
        spawnPoints.single { it.isPortal() }.let {
            playerSpawnLocation = Vector2(it.x, it.y)
            playerSpawnDirection = it.direction
        }
    }

    fun setPlayerSpawnLocation(portal: GameMapRelocator) {
        spawnPoints.first { it.isInConnectionWith(portal) }.let {
            playerSpawnLocation = Vector2(it.x, it.y)
            setPlayerSpawnDirection(portal, it)
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
        return point.x < 0 || point.x >= pixelWidth
            || point.y < 0 || point.y >= pixelHeight
    }

    fun dispose() {
        torches.forEach { it.dispose() }
        tiledMap.dispose()
    }

    fun debug(shapeRenderer: ShapeRenderer) {

        shapeRenderer.color = Color.YELLOW

        (blockers + lowBlockers + conditionBlockers)
            .map { it.rectangle }
            .forEach { shapeRenderer.rect(it.x, it.y, it.width, it.height) }

        shapeRenderer.color = Color.BLUE

        (portals + spawnPoints + npcs + heroes + enemies + eventDiscovers + eventCheckers + cutsceneDiscovers
            + questDiscovers + questCheckers + notes + sparkles)
            .map { it.rectangle }
            .forEach { shapeRenderer.rect(it.x, it.y, it.width, it.height) }

        (chests + doors)
            .map { it.rectangle }
            .forEach { shapeRenderer.rect(it.x, it.y, it.width, it.height) }
    }

}

val TiledMap.bgm: AudioEvent
    get() {
        return propertyOrNull<String>(BGM_PROPERTY)?.let {
            if (
                gameData.clock.isWarning()
                && screenManager.currentScreen !is CutsceneScreen
            ) {
                AudioEvent.BGM_END_NEAR
            } else if (
                !gameData.events.hasEventPlayed("stop_intro_bgm")
                && screenManager.currentScreen !is SceneIntro
            ) {
                AudioEvent.BGM_TENSION
            } else {
                AudioEvent.valueOf(it.uppercase())
            }
        } ?: AudioEvent.NONE
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
