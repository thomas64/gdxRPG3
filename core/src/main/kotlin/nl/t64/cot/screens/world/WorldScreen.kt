package nl.t64.cot.screens.world

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import nl.t64.cot.Utils
import nl.t64.cot.Utils.brokerManager
import nl.t64.cot.Utils.mapManager
import nl.t64.cot.Utils.profileManager
import nl.t64.cot.Utils.screenManager
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe
import nl.t64.cot.components.loot.Loot
import nl.t64.cot.constants.Constant
import nl.t64.cot.constants.GameState
import nl.t64.cot.constants.ScreenType
import nl.t64.cot.screens.BattleResolver
import nl.t64.cot.screens.battle.BattleObserver
import nl.t64.cot.screens.battle.BattleScreen
import nl.t64.cot.screens.inventory.tooltip.MessageTooltip
import nl.t64.cot.screens.loot.FindScreen
import nl.t64.cot.screens.storage.StorageScreen
import nl.t64.cot.screens.warp.WarpScreen
import nl.t64.cot.screens.world.conversation.ConversationDialog
import nl.t64.cot.screens.world.conversation.ConversationObserver
import nl.t64.cot.screens.world.debug.DebugBox
import nl.t64.cot.screens.world.debug.DebugRenderer
import nl.t64.cot.screens.world.debug.GridRenderer
import nl.t64.cot.screens.world.entity.Entity
import nl.t64.cot.screens.world.entity.GraphicsPlayer
import nl.t64.cot.screens.world.entity.InputPlayer
import nl.t64.cot.screens.world.entity.PhysicsPlayer
import nl.t64.cot.screens.world.entity.events.FindPathEvent
import nl.t64.cot.screens.world.entity.events.LoadEntityEvent
import nl.t64.cot.screens.world.schedule.WorldSchedule
import nl.t64.cot.sfx.TransitionImage
import nl.t64.cot.sfx.TransitionPurpose
import nl.t64.cot.subjects.*


class WorldScreen : Screen,
    MapObserver, ComponentObserver, EntityObserver, LootObserver, ConversationObserver, MessageObserver,
    BattleObserver {

    private lateinit var previousGameState: GameState
    private lateinit var gameState: GameState

    private val stage = Stage()
    private val camera = Camera()
    private val mapRenderer = TextureMapObjectRenderer(camera)
    private val multiplexer = InputMultiplexer().apply { addProcessor(createListener()) }
    private val shapeRenderer = ShapeRenderer()
    private val clockBox = ClockBox()
    private val partyWindow = PartyWindow()
    private val conversationDialog = ConversationDialog(this)
    private val messageDialog = MessageDialog(multiplexer)
    private val messageTooltip = MessageTooltip()

    private val player = Entity(Constant.PLAYER_ID, InputPlayer(multiplexer), PhysicsPlayer(), GraphicsPlayer())
    private val gridRenderer = GridRenderer(camera)
    private val debugRenderer = DebugRenderer(camera, player)
    private val debugBox = DebugBox(player)
    private val buttonsBox = ButtonBox()

    private val worldSchedule = WorldSchedule()

    private val visibleScheduledEntities: MutableList<Entity> = mutableListOf()
    private lateinit var npcEntities: List<Entity>
    private lateinit var currentNpcEntity: Entity
    private lateinit var lootList: List<Entity>
    private lateinit var doorList: List<Entity>

    init {
        brokerManager.messageObservers.addObserver(this)
        brokerManager.componentObservers.addObserver(this)
        brokerManager.mapObservers.addObserver(this)
        brokerManager.entityObservers.addObserver(this)
        brokerManager.lootObservers.addObserver(this)
    }

    //region MapObserver ///////////////////////////////////////////////////////////////////////////////////////////////

    override fun onNotifyFadeOut(actionAfterFade: () -> Unit, transitionColor: Color, duration: Float) {
        fadeOut(actionAfterFade, transitionColor, duration)
    }

    override fun onNotifyMapChanged(currentMap: GameMap) {
        mapRenderer.map = currentMap.tiledMap
        camera.setNewMapSize(currentMap.pixelWidth, currentMap.pixelHeight)
        player.send(LoadEntityEvent(currentMap.playerSpawnDirection, currentMap.playerSpawnLocation))
        npcEntities = NpcEntitiesLoader(currentMap).createNpcs()
        lootList = LootLoader(currentMap).createLoot()
        doorList = DoorLoader(currentMap).createDoors()
        currentMap.setTiledGraphs()
        mapManager.setNextMapTitleNull()
    }

    override fun onNotifyShakeCamera() {
        camera.startShaking()
    }

    override fun onNotifyStartCutscene(cutsceneId: String, fadeDuration: Float) {
        doBeforeLoadScreen()
        fadeOut({ screenManager.setScreen(ScreenType.valueOf(cutsceneId.uppercase())) }, Color.BLACK, fadeDuration)
    }
    //endregion

    //region ComponentObserver /////////////////////////////////////////////////////////////////////////////////////////

    override fun onNotifyShowConversationDialogFromNpc(conversationId: String, npcEntity: Entity) {
        currentNpcEntity = npcEntity
        player.resetInput()
        gameState = GameState.DIALOG
        conversationDialog.loadConversation(conversationId, npcEntity.id)
        conversationDialog.show()
    }

    override fun onNotifyShowConversationDialogFromEvent(conversationId: String, entityId: String) {
        if (entityId != Constant.PLAYER_ID) {
            currentNpcEntity = getEntityBasedOnEventData(conversationId, entityId)
        }
        player.resetInput()
        gameState = GameState.DIALOG
        conversationDialog.loadConversation(conversationId, entityId)
        conversationDialog.show()
    }

    private fun getEntityBasedOnEventData(conversationId: String, entityId: String): Entity {
        return npcEntities
            .filter { it.id == entityId }
            .filter { it.getConversationId() == conversationId }
            .first()
    }

    override fun onNotifyShowNoteDialog(noteId: String) {
        player.resetInput()
        gameState = GameState.DIALOG
        conversationDialog.loadNote(noteId)
        conversationDialog.show()
    }

    override fun onNotifyShowFindScreenWithMessageDialog(loot: Loot, event: AudioEvent, message: String) {
        doBeforeLoadScreen()
        gameState = GameState.DIALOG
        messageDialog.setActionAfterHide { FindScreen.load(loot, event) }
        messageDialog.show(message, AudioEvent.SE_CONVERSATION_NEXT)
    }

    override fun onNotifyShowFindScreen(loot: Loot, event: AudioEvent) {
        doBeforeLoadScreen()
        FindScreen.load(loot, event)
    }

    override fun onNotifyShowStorageScreen() {
        player.resetInput()
        StorageScreen.load()
    }

    override fun onNotifyShowWarpScreen(currentMapName: String) {
        player.resetInput()
        WarpScreen.load(currentMapName)
    }

    override fun onNotifyShowMessageDialog(message: String) {
        player.resetInput()
        gameState = GameState.DIALOG
        messageDialog.setActionAfterHide { gameState = GameState.RUNNING }
        messageDialog.show(message, AudioEvent.SE_CONVERSATION_NEXT)
    }

    override fun onNotifyShowBattleScreen(battleId: String, enemyEntity: Entity) {
        if (player.moveSpeed != Constant.MOVE_SPEED_4 && !isInMapTransition) {
            setInputProcessors(null)
            currentNpcEntity = enemyEntity
            gameState = GameState.BATTLE
            doBeforeLoadScreen()
            fadeOut({ BattleScreen.load(battleId, this) }, Color.BLACK)
        }
    }
    //endregion

    //region EntityObserver ////////////////////////////////////////////////////////////////////////////////////////////

    override fun onNotifyPartyUpdate() {
        // atm no party update necessary.
    }

    override fun onNotifyNpcsUpdate(newNpcEntities: List<Entity>) {
        npcEntities = newNpcEntities
    }

    override fun onNotifyAddScheduledEntity(entity: Entity) {
        if (entity !in visibleScheduledEntities) {
            visibleScheduledEntities.add(entity)
        }
    }

    override fun onNotifyRemoveScheduledEntity(entity: Entity) {
        if (entity in visibleScheduledEntities) {
            visibleScheduledEntities.remove(entity)
        }
    }
    //endregion

    //region LootObserver //////////////////////////////////////////////////////////////////////////////////////////////

    override fun onNotifySpoilsUpdated() {
        onNotifyLootTaken()
    }

    override fun onNotifyLootTaken() {
        lootList.forEach { brokerManager.actionObservers.removeObserver(it) }
        lootList.forEach { brokerManager.blockObservers.removeObserver(it) }
        lootList = LootLoader(mapManager.currentMap).createLoot()
    }
    //endregion

    //region ConversationObserver //////////////////////////////////////////////////////////////////////////////////////

    override fun onNotifyExitConversation() {
        show()
        doBeforeLoadScreen()
    }

    override fun onNotifyHeroJoined() {
        brokerManager.blockObservers.removeObserver(currentNpcEntity)
        npcEntities = npcEntities.filter { it != currentNpcEntity }
    }

    override fun onNotifyShowBattleScreen(battleId: String) {
        gameState = GameState.BATTLE
        fadeOut({ BattleScreen.load(battleId, this) }, Color.BLACK)
    }

    override fun onNotifyReloadNpcs() {
        fadeOut({ reloadNpcs() }, Color.BLACK, 1.5f, TransitionPurpose.JUST_FADE)
    }

    private fun reloadNpcs() {
        brokerManager.blockObservers.removeAllNpcObservers()
        npcEntities = NpcEntitiesLoader(mapManager.currentMap).createNpcs()
    }
    //endregion

    //region MessageObserver ///////////////////////////////////////////////////////////////////////////////////////////

    override fun onNotifyShowMessageTooltip(message: String) {
        messageTooltip.show(message, stage)
    }
    //endregion

    //region BattleObserver ////////////////////////////////////////////////////////////////////////////////////////////

    override fun onNotifyBattleWon(battleId: String, spoils: Loot) {
        screenManager.setScreen(ScreenType.WORLD)
        BattleResolver.resolveWin(battleId, spoils, player.position, currentNpcEntity, npcEntities)
        doBeforeLoadScreen()
    }

    override fun onNotifyBattleLost() {
        screenManager.setScreen(ScreenType.SCENE_DEATH)
    }

    override fun onNotifyBattleFled() {
        val mapTitle = profileManager.getLastSaveLocation()
        mapManager.loadMapAfterFleeing(mapTitle)
        screenManager.setScreen(ScreenType.WORLD)
    }
    //endregion

    override fun show() {
        gameState = GameState.RUNNING
        setInputProcessors(multiplexer)
        mapManager.continueAudio()
    }

    override fun render(dt: Float) {
        when (gameState) {
            GameState.PAUSED -> { // do nothing here
            }
            GameState.MINIMAP -> renderMiniMap()
            GameState.RUNNING -> {
                updateEntities(dt)
                renderAll(dt)
            }
            GameState.DIALOG -> renderAll(dt)
            GameState.BATTLE -> renderAll(dt)
        }
    }

    private fun updateEntities(dt: Float) {
        if (!isInTransition) {
            clockBox.update(dt)
            player.update(dt)
            worldSchedule.update()
        }
        doorList.forEach { it.update(dt) }
        lootList.forEach { it.update(dt) }
        val playerGridPosition = player.getPositionInGrid()
        npcEntities.forEach { it.update(dt) }
        npcEntities.forEach { it.send(FindPathEvent(playerGridPosition)) }
        visibleScheduledEntities.forEach { it.update(dt) }
    }

    private fun renderMiniMap() {
        updateCameraPosition()
        mapRenderer.renderMapWithoutEntities()
        // todo, eventually remove shaperenderer and use sprite icons for minimap.
        shapeRenderer.projectionMatrix = camera.combined
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        player.renderOnMiniMap(mapRenderer.batch, shapeRenderer)
        npcEntities.forEach { it.renderOnMiniMap(mapRenderer.batch, shapeRenderer) }
        mapManager.drawFogOfWar(shapeRenderer)
        shapeRenderer.end()
    }

    private fun renderAll(dt: Float) {
        mapManager.updateFogOfWar(player.position, dt)
        mapManager.updateConditionLayers()
        updateCameraPosition()
        mapRenderer.renderAll(player.position) { renderEntities(it) }
        gridRenderer.possibleRender()
        debugRenderer.possibleRenderObjects(doorList, lootList, npcEntities)
        debugBox.possibleUpdate(dt)
        buttonsBox.update(dt)
        partyWindow.update(dt)
        clockBox.render(dt)
        conversationDialog.update(dt)
        messageDialog.update(dt)

        stage.act(dt)
        if (isInMapTransition) {
            mapManager.fadeAudio()
        }
        stage.draw()
    }

    private fun updateCameraPosition() {
        camera.setPosition(player.position)
        mapRenderer.updateCamera()
    }

    private fun renderEntities(batch: Batch) {
        lootList.forEach { it.render(batch) }
        doorList
            .filter { it.position.y >= player.position.y }
            .forEach { it.render(batch) }

        val allEntities: MutableList<Entity> = ArrayList()
        allEntities.addAll(npcEntities)
        allEntities.addAll(visibleScheduledEntities)
        allEntities.add(player)
        allEntities.sortByDescending { it.position.y }
        allEntities.forEach { it.render(batch) }

        doorList
            .filter { it.position.y < player.position.y }
            .forEach { it.render(batch) }
    }

    private fun fadeOut(
        actionAfterFade: () -> Unit,
        transitionColor: Color,
        duration: Float = 0f,
        transitionPurpose: TransitionPurpose = TransitionPurpose.MAP_CHANGE
    ) {
        val transition = TransitionImage(transitionPurpose, transitionColor)
        stage.addActor(transition)
        transition.addAction(Actions.sequence(Actions.alpha(0f),
                                              Actions.fadeIn(Constant.FADE_DURATION),
                                              Actions.delay(duration),
                                              Actions.run(actionAfterFade),
                                              Actions.removeActor()))
    }

    private fun doBeforeLoadScreen() {
        player.resetInput()
        render(0f)
    }

    private fun openMiniMap() {
        if (camera.zoom()) {
            playSe(AudioEvent.SE_MINIMAP)
            gameState = GameState.MINIMAP
            multiplexer.removeProcessor(0)
            multiplexer.addProcessor(0, MiniMapListener { closeMiniMap() })
        } else {
            playSe(AudioEvent.SE_MENU_ERROR)
        }
    }

    private fun closeMiniMap() {
        playSe(AudioEvent.SE_MINIMAP)
        gameState = GameState.RUNNING
        multiplexer.removeProcessor(0)
        multiplexer.addProcessor(0, createListener())
        camera.reset()
    }

    private fun createListener(): WorldScreenListener {
        return WorldScreenListener({ isInTransition },
                                   { doBeforeLoadScreen() },
                                   { partyWindow.showHide() },
                                   { openMiniMap() },
                                   { gridRenderer.setShowGrid() },
                                   { debugRenderer.setShowObjects() },
                                   { debugBox.setShowDebug() })
    }

    private fun setInputProcessors(inputProcessor: InputProcessor?) {
        Gdx.input.inputProcessor = inputProcessor
        Utils.setGamepadInputProcessor(inputProcessor)
    }

    private val isInMapTransition: Boolean
        get() = isInTransition && (stage.actors.peek() as TransitionImage).purpose == TransitionPurpose.MAP_CHANGE
    private val isJustInTransition: Boolean
        get() = isInTransition && (stage.actors.peek() as TransitionImage).purpose == TransitionPurpose.JUST_FADE
    private val isInTransition: Boolean
        get() = stage.actors.notEmpty()
                && gameState != GameState.DIALOG
                && stage.actors.peek() is TransitionImage

    override fun resize(width: Int, height: Int) {
        // empty
    }

    override fun pause() {
        previousGameState = gameState
        gameState = GameState.PAUSED
    }

    override fun resume() {
        gameState = previousGameState
    }

    override fun hide() {
        pause()
        setInputProcessors(null)
    }

    override fun dispose() {
        clockBox.dispose()
        player.dispose()
        mapRenderer.dispose()
        shapeRenderer.dispose()
        partyWindow.dispose()
        conversationDialog.dispose()
        messageDialog.dispose()
        debugRenderer.dispose()
        gridRenderer.dispose()
        debugBox.dispose()
        buttonsBox.dispose()
        stage.dispose()
    }

}
