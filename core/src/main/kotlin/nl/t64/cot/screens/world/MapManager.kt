package nl.t64.cot.screens.world

import com.badlogic.gdx.ai.pfa.DefaultGraphPath
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.Vector2
import nl.t64.cot.ProfileManager
import nl.t64.cot.Utils.audioManager
import nl.t64.cot.Utils.brokerManager
import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.profileManager
import nl.t64.cot.Utils.resourceManager
import nl.t64.cot.audio.AudioCommand
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.toAudioEvent
import nl.t64.cot.components.cutscene.CutsceneId
import nl.t64.cot.constants.Constant
import nl.t64.cot.screens.world.entity.Direction
import nl.t64.cot.screens.world.entity.EntityState
import nl.t64.cot.screens.world.mapobjects.*
import nl.t64.cot.screens.world.pathfinding.TiledNode
import nl.t64.cot.subjects.ProfileObserver


class MapManager : ProfileObserver {

    lateinit var currentMap: GameMap
    private var isMapLoaded: Boolean = false
    private var nextMapTitle: String? = null
    private lateinit var fogOfWar: FogOfWar

    override fun onNotifyCreateProfile(profileManager: ProfileManager) {
        fogOfWar = FogOfWar()
        loadMap(Constant.STARTING_MAP)
        currentMap.setPlayerSpawnLocationForNewLoad(Constant.STARTING_MAP)
        onNotifySaveProfile(profileManager)
        brokerManager.mapObservers.notifyMapChanged(currentMap)
    }

    override fun onNotifySaveProfile(profileManager: ProfileManager) {
        profileManager.setProperty("mapTitle", currentMap.mapTitle)
        profileManager.setProperty("fogOfWar", fogOfWar)
    }

    override fun onNotifyLoadProfile(profileManager: ProfileManager) {
        val mapTitle = profileManager.getProperty<String>("mapTitle")
        fogOfWar = profileManager.getProperty("fogOfWar")
        if (gameData.cutscenes.isPlayed(CutsceneId.SCENE_INTRO)) {
            loadMapWithBgmBgs(mapTitle)
        } else {
            loadMap(mapTitle)
        }
        currentMap.setPlayerSpawnLocationForNewLoad(mapTitle)
        brokerManager.mapObservers.notifyMapChanged(currentMap)
    }

    fun loadMapAfterFleeing(mapTitle: String) {
        loadMapAfterCutscene(mapTitle, mapTitle)
    }

    fun loadMapAfterCutscene(mapTitle: String, spawnId: String) {
        loadMapWithBgmBgs(mapTitle)
        currentMap.setPlayerSpawnLocationForNewLoad(spawnId)
        brokerManager.mapObservers.notifyMapChanged(currentMap)
    }

    fun getTiledMap(): TiledMap = currentMap.tiledMap
    fun getParallaxBackground(): GameMapParallaxBackground? = currentMap.parallaxBackground
    fun getLightmapCamera(): GameMapLightmapCamera = currentMap.lightmapCamera
    fun getLightmapMap(): GameMapLightmapMap = currentMap.lightmapMap
    fun getGameMapLights(): List<GameMapLight> = currentMap.lights
    fun getLightmapPlayer(): Sprite? = currentMap.lightmapPlayer
    fun getLowerConditionTextures(): List<GameMapConditionTexture> = currentMap.lowerTextures
    fun getUpperConditionTextures(): List<GameMapConditionTexture> = currentMap.upperTextures

    fun findPath(startPoint: Vector2, endPoint: Vector2, state: EntityState): DefaultGraphPath<TiledNode> {
        return currentMap.getTiledGraph(state).findPath(startPoint, endPoint)
    }

    fun updateFogOfWar(playerPosition: Vector2, dt: Float) {
        fogOfWar.update(playerPosition, currentMap, dt)
    }

    fun drawFogOfWar(shapeRenderer: ShapeRenderer) {
        fogOfWar.draw(shapeRenderer, currentMap.mapTitle)
    }

    fun updateConditionLayers() {
        currentMap.conditionBlockers.forEach { it.update() }
        currentMap.upperTextures.forEach { it.update() }
        currentMap.lowerTextures.forEach { it.update() }
    }

    fun getGroundSound(x: Float, y: Float): AudioEvent {
        return getGroundSound(Vector2(x, y))
    }

    fun getGroundSound(playerFeetPosition: Vector2): AudioEvent {
        return currentMap.getUnderground(playerFeetPosition).toAudioEvent()
    }

    fun setNextMapTitleNull() {
        nextMapTitle = null
    }

    fun useCrystal() {
        audioManager.handle(AudioCommand.SE_PLAY_ONCE, AudioEvent.SE_RESET)
        val actionAfterFade = {
            gameData.resetCycle()
            val mapTitle = "honeywood_house_mozes"
            loadMap(mapTitle)
            currentMap.setPlayerSpawnLocationForNewLoad(mapTitle)
            profileManager.saveProfile()
            brokerManager.mapObservers.notifyMapChanged(currentMap)
        }
        brokerManager.mapObservers.notifyFadeOut(actionAfterFade, Color.GRAY, 1f)
    }

    fun checkWarpPoint(warpPoint: GameMapWarpPoint, playerDirection: Direction) {
        audioManager.handle(AudioCommand.SE_PLAY_ONCE, AudioEvent.SE_WARP)
        nextMapTitle = warpPoint.toMapName
        brokerManager.mapObservers.notifyFadeOut(
            { changeMapWithCameraShake(warpPoint, playerDirection) }, warpPoint.fadeColor
        )
    }

    fun collisionPortal(portal: GameMapPortal, playerDirection: Direction) {
        nextMapTitle = portal.toMapName
        brokerManager.mapObservers.notifyFadeOut(
            { changeMap(portal, playerDirection) }, portal.fadeColor
        )
    }

    private fun changeMapWithCameraShake(warpPoint: GameMapRelocator, direction: Direction) {
        changeMap(warpPoint, direction)
        brokerManager.mapObservers.notifyShakeCamera()
    }

    private fun changeMap(portal: GameMapRelocator, direction: Direction) {
        portal.enterDirection = direction
        loadMapWithBgmBgs(portal.toMapName)
        currentMap.setPlayerSpawnLocation(portal)
        brokerManager.mapObservers.notifyMapChanged(currentMap)
    }

    fun setTiledGraph() {
        currentMap.setTiledGraphs()
    }

    fun continueAudio() {
        if (isMapLoaded) {
            audioManager.handle(AudioCommand.BGM_PLAY_LOOP, currentMap.bgm)
            audioManager.handle(AudioCommand.BGS_PLAY_LOOP, currentMap.bgs)
        }
    }

    fun fadeAudio() {
        val nextMap: TiledMap = resourceManager.getTiledMapAsset(nextMapTitle)
        audioManager.possibleBgmFade(currentMap.bgm, nextMap.bgm)
        audioManager.possibleBgsFade(currentMap.bgs, nextMap.bgs)
    }

    fun loadMapWithHardBgmBgsSwitch(mapTitle: String) {
        loadMap(mapTitle)
        audioManager.certainBgmSwitch(currentMap.bgm)
        audioManager.certainBgsSwitch(currentMap.bgs)
    }

    fun loadMapWithBgmBgs(mapTitle: String) {
        val prevBgm = if (isMapLoaded) currentMap.bgm else AudioEvent.NONE
        val prevBgs = if (isMapLoaded) currentMap.bgs else listOf(AudioEvent.NONE)
        loadMap(mapTitle)
        val nextBgm = currentMap.bgm
        val nextBgs = currentMap.bgs
        audioManager.possibleBgmSwitch(prevBgm, nextBgm)
        audioManager.possibleBgsSwitch(prevBgs, nextBgs)
    }

    fun loadMapWithBgs(mapTitle: String) {
        val prevBgs = if (isMapLoaded) currentMap.bgs else listOf(AudioEvent.NONE)
        loadMap(mapTitle)
        val nextBgs = currentMap.bgs
        audioManager.possibleBgsSwitch(prevBgs, nextBgs)
    }

    fun loadMap(mapTitle: String) {
        disposeOldMaps()
        currentMap = GameMap(mapTitle)
        isMapLoaded = true
        fogOfWar.putIfAbsent(currentMap)
    }

    fun disposeOldMaps() {
        brokerManager.actionObservers.removeAllObservers()
        brokerManager.blockObservers.removeAllObservers()
        brokerManager.bumpObservers.removeAllObservers()
        brokerManager.detectionObservers.removeAllObservers()
        brokerManager.collisionObservers.removeAllObservers()
        if (isMapLoaded) {
            currentMap.dispose()
            isMapLoaded = false
        }
    }

}
