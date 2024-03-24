package nl.t64.cot.screens.world.map

import com.badlogic.gdx.ai.pfa.DefaultGraphPath
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.Vector2
import nl.t64.cot.ProfileManager
import nl.t64.cot.Utils.audioManager
import nl.t64.cot.Utils.brokerManager
import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.resourceManager
import nl.t64.cot.Utils.screenManager
import nl.t64.cot.audio.*
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

    override fun onNotifyCreateProfile(profileManager: ProfileManager) {
        loadMap(Constant.STARTING_MAP)
        currentMap.setPlayerSpawnLocationForNewLoad(Constant.STARTING_MAP)
        onNotifySaveProfile(profileManager)
        screenManager.getWorldScreen().changeMap(currentMap)
    }

    override fun onNotifySaveProfile(profileManager: ProfileManager) {
        profileManager.setProperty("mapTitle", currentMap.mapTitle)
    }

    override fun onNotifyLoadProfile(profileManager: ProfileManager) {
        val mapTitle = profileManager.getProperty<String>("mapTitle")
        if (gameData.cutscenes.isPlayed(CutsceneId.SCENE_INTRO)) {
            loadMapWithBgmBgs(mapTitle)
        } else {
            loadMap(mapTitle)
        }
        currentMap.setPlayerSpawnLocationForNewLoad(mapTitle)
        screenManager.getWorldScreen().changeMap(currentMap)
    }

    fun loadMapAfterFleeing(mapTitle: String) {
        loadMapAfterCutscene(mapTitle, mapTitle)
    }

    fun loadMapAfterCutscene(mapTitle: String, spawnId: String) {
        loadMapWithBgmBgs(mapTitle)
        currentMap.setPlayerSpawnLocationForNewLoad(spawnId)
        screenManager.getWorldScreen().changeMap(currentMap)
    }

    fun getTiledMap(): TiledMap = currentMap.tiledMap
    fun getParallaxBackground(): GameMapParallaxBackground? = currentMap.parallaxBackground
    fun getLightmapCamera(): GameMapLightmapCamera = currentMap.lightmapCamera
    fun getLightmapMap(): GameMapLightmapMap = currentMap.lightmapMap
    fun getGameMapLights(): List<GameMapLight> = currentMap.lights
    fun getLightmapPlayer(): Sprite? = currentMap.lightmapPlayer
    fun getParticleEffects(): List<GameMapParticle> = currentMap.torches
    fun getLowerConditionTextures(): List<GameMapConditionTexture> = currentMap.lowerTextures
    fun getUpperConditionTextures(): List<GameMapConditionTexture> = currentMap.upperTextures

    fun findPath(startPoint: Vector2, endPoint: Vector2, state: EntityState): DefaultGraphPath<TiledNode> {
        return currentMap.getTiledGraph(state)?.findPath(startPoint, endPoint) ?: DefaultGraphPath()
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

    fun checkWarpPoint(warpPoint: GameMapRelocator, playerDirection: Direction) {
        playSe(AudioEvent.SE_WARP)
        nextMapTitle = warpPoint.toMapName
        screenManager.getWorldScreen().fadeOut(
            { changeMapWithCameraShake(warpPoint, playerDirection) }, warpPoint.fadeColor
        )
    }

    fun schedulePortal(portal: GameMapRelocator, playerDirection: Direction) {
        screenManager.getWorldScreen().fadeOut(
            { changeMap(portal, playerDirection) }, duration = 1f
        )
    }

    fun collisionPortal(portal: GameMapRelocator, playerDirection: Direction) {
        nextMapTitle = portal.toMapName
        screenManager.getWorldScreen().fadeOut(
            { changeMap(portal, playerDirection) }, portal.fadeColor
        )
    }

    fun changeMapWithWarpPortal(warpToMapName: String) {
        loadMapWithBgmBgs(warpToMapName)
        currentMap.setPlayerSpawnLocationForWarpPortal()
        screenManager.getWorldScreen().changeMap(currentMap)
        screenManager.getWorldScreen().shakeCamera()
    }

    private fun changeMapWithCameraShake(warpPoint: GameMapRelocator, direction: Direction) {
        changeMap(warpPoint, direction)
        screenManager.getWorldScreen().shakeCamera()
    }

    private fun changeMap(portal: GameMapRelocator, direction: Direction) {
        portal.enterDirection = direction
        loadMapWithBgmBgs(portal.toMapName)
        currentMap.setPlayerSpawnLocation(portal)
        screenManager.getWorldScreen().changeMap(currentMap)
    }

    fun setTiledGraph() {
        currentMap.setTiledGraphs()
    }

    fun continueAudio() {
        if (isMapLoaded) {
            playBgm(currentMap.bgm)
            playBgs(currentMap.bgs)
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
