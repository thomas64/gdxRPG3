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
import nl.t64.cot.Utils.fogOfWarManager
import nl.t64.cot.Utils.mapManager
import nl.t64.cot.Utils.profileManager
import nl.t64.cot.Utils.screenManager
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe
import nl.t64.cot.audio.stopAllBgm
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
import nl.t64.cot.screens.world.entity.*
import nl.t64.cot.screens.world.entity.events.DirectionEvent
import nl.t64.cot.screens.world.entity.events.FindPathEvent
import nl.t64.cot.screens.world.entity.events.LoadEntityEvent
import nl.t64.cot.screens.world.entity.events.NpcActionEvent
import nl.t64.cot.screens.world.loaders.DoorLoader
import nl.t64.cot.screens.world.loaders.LootLoader
import nl.t64.cot.screens.world.loaders.NpcEntitiesLoader
import nl.t64.cot.screens.world.map.GameMap
import nl.t64.cot.screens.world.schedule.WorldSchedule
import nl.t64.cot.screens.world.ui.ButtonBox
import nl.t64.cot.screens.world.ui.ClockBox
import nl.t64.cot.screens.world.ui.MovementBox
import nl.t64.cot.screens.world.ui.PartyWindow
import nl.t64.cot.sfx.TransitionImage
import nl.t64.cot.sfx.TransitionPurpose


class WorldScreen : Screen, ConversationObserver, BattleObserver {

    private var previousGameState: GameState = GameState.OFF
    private var gameState: GameState = GameState.OFF

    private val stage = Stage()
    private val camera = Camera()
    private val worldRenderer = WorldRenderer(camera)
    private val multiplexer = InputMultiplexer().apply { addProcessor(createListener()) }
    private val shapeRenderer = ShapeRenderer()
    private val clockBox = ClockBox()
    private val partyWindow = PartyWindow().apply { showHide() }
    private val conversationDialog = ConversationDialog(this)
    private val messageDialog = MessageDialog(multiplexer)
    private val messageTooltip = MessageTooltip()

    private val player = Entity(Constant.PLAYER_ID, InputPlayer(multiplexer), PhysicsPlayer(), GraphicsPlayer())
    private val gridRenderer = GridRenderer(camera)
    private val debugRenderer = DebugRenderer(camera)
    private val debugBox = DebugBox(player)
    private val buttonsBox = ButtonBox()
    private val movementBox = MovementBox()

    private val worldSchedule = WorldSchedule()

    private val visibleScheduledEntities: MutableList<Entity> = mutableListOf()
    private lateinit var npcEntities: List<Entity>
    private lateinit var currentNpcEntity: Entity
    private lateinit var lootList: List<Entity>
    private lateinit var doorList: List<Entity>

    //region public methods ////////////////////////////////////////////////////////////////////////////////////////////

    fun fadeWithFlames() {
        player.resetInput()
        setInputProcessors(null)
        player.send(DirectionEvent(Direction.NORTH))

        EndOfTime(stage, camera).fadeWithFlamesAnd {
            conversationDialog.tryToClose()
            gameState = GameState.OFF
            Utils.runWithDelay(1f) { stage.clear() }
        }
    }

    fun fadeOut(
        transitionColor: Color = Color.BLACK,
        duration: Float = 0f,
        transitionPurpose: TransitionPurpose = TransitionPurpose.MAP_CHANGE,
        actionAfterFade: () -> Unit
    ) {
        val transition = TransitionImage(transitionPurpose, transitionColor)
        stage.addActor(transition)
        transition.addAction(Actions.sequence(Actions.alpha(0f),
                                              Actions.fadeIn(Constant.FADE_DURATION),
                                              Actions.delay(duration),
                                              Actions.run(actionAfterFade),
                                              Actions.removeActor()))
    }

    fun changeMap(currentMap: GameMap) {
        worldRenderer.map = currentMap.tiledMap
        player.send(LoadEntityEvent(currentMap.playerSpawnDirection, currentMap.playerSpawnLocation))
        camera.setNewMapSize(currentMap.pixelWidth, currentMap.pixelHeight)
        camera.setInitPosition(player.position)
        npcEntities = NpcEntitiesLoader(currentMap).createNpcs()
        lootList = LootLoader(currentMap).createLoot()
        doorList = DoorLoader(currentMap).createDoors()
        currentMap.setTiledGraphs()
        mapManager.setNextMapTitleNull()
        fogOfWarManager.setNewMap(currentMap, camera)
    }

    fun shakeCamera() {
        camera.startShaking()
    }

    fun startCutscene(screenType: ScreenType, fadeDuration: Float = 0f) {
        doBeforeLoadScreen()
        val actionAfterFade = { screenManager.setScreen(screenType); stopAllBgm() }
        fadeOut(Color.BLACK, fadeDuration, TransitionPurpose.MAP_CHANGE, actionAfterFade)
    }

    fun startCutsceneWithoutBgmFading(screenType: ScreenType, fadeDuration: Float = 0f) {
        doBeforeLoadScreen()
        val actionAfterFade = { screenManager.setScreen(screenType) }
        fadeOut(Color.BLACK, fadeDuration, TransitionPurpose.JUST_FADE, actionAfterFade)
    }

    fun showConversationDialogFromNpc(conversationId: String, npcEntity: Entity) {
        if (gameState == GameState.DIALOG) return
        currentNpcEntity = npcEntity
        player.resetInput()
        gameState = GameState.DIALOG
        conversationDialog.loadConversation(conversationId, npcEntity.id)
        conversationDialog.show()
    }

    fun showConversationDialogFromEvent(conversationId: String, entityId: String) {
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

    fun showNoteDialog(noteId: String) {
        if (gameState == GameState.DIALOG) return
        player.resetInput()
        gameState = GameState.DIALOG
        conversationDialog.loadNote(noteId)
        conversationDialog.show()
    }

    fun showFindScreenWithMessageDialog(loot: Loot, event: AudioEvent, message: String) {
        doBeforeLoadScreen()
        gameState = GameState.DIALOG
        messageDialog.setActionAfterHide { FindScreen.load(loot, event) }
        messageDialog.show(message, AudioEvent.SE_CONVERSATION_NEXT)
    }

    fun showFindScreen(loot: Loot, event: AudioEvent) {
        doBeforeLoadScreen()
        FindScreen.load(loot, event)
    }

    fun showStorageScreen() {
        player.resetInput()
        StorageScreen.load()
    }

    fun showWarpScreen(currentMapName: String) {
        player.resetInput()
        WarpScreen.load(currentMapName)
    }

    fun showMessageDialog(message: String, actionAfterHide: () -> Unit = {}) {
        player.resetInput()
        gameState = GameState.DIALOG
        messageDialog.setActionAfterHide {
            gameState = GameState.RUNNING
            actionAfterHide.invoke()
        }
        messageDialog.show(message, AudioEvent.SE_CONVERSATION_NEXT)
    }

    fun showBattleScreen(battleId: String, enemyEntity: Entity) {
        if (player.moveSpeed == Constant.MOVE_SPEED_4 || isInMapTransition) return
        setInputProcessors(null)
        currentNpcEntity = enemyEntity
        gameState = GameState.BATTLE
        doBeforeLoadScreen()
        fadeOut { BattleScreen.load(battleId, this) }
    }

    fun updateParty() {
        // atm no party update necessary.
    }

    fun updateNpcs(newNpcEntities: List<Entity>) {
        npcEntities = newNpcEntities
    }

    fun reloadNpcsWithFade() {
        fadeOut(Color.BLACK, 1.5f, TransitionPurpose.JUST_FADE) { reloadNpcs() }
    }

    private fun reloadNpcs() {
        brokerManager.blockObservers.removeAllNpcObservers()
        npcEntities = NpcEntitiesLoader(mapManager.currentMap).createNpcs()
    }

    fun addScheduledEntity(entity: Entity) {
        if (entity !in visibleScheduledEntities) {
            visibleScheduledEntities.add(entity)
        }
    }

    fun removeScheduledEntity(entity: Entity) {
        visibleScheduledEntities.remove(entity)
    }

    fun useDoor(doorId: String) {
        doorList.single { it.id == doorId }.send(NpcActionEvent())
    }

    fun updateLoot() {
        lootList.forEach { brokerManager.actionObservers.removeObserver(it) }
        lootList.forEach { brokerManager.blockObservers.removeObserver(it) }
        lootList = LootLoader(mapManager.currentMap).createLoot()
    }

    fun showMessageTooltip(message: String) {
        messageTooltip.show(message, stage)
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
        fadeOut { BattleScreen.load(battleId, this) }
    }

    override fun onNotifyReloadNpcs() {
        reloadNpcsWithFade()
    }
    //endregion

    //region BattleObserver ////////////////////////////////////////////////////////////////////////////////////////////

    override fun onNotifyBattleWon(battleId: String, spoils: Loot) {
        gameState = GameState.BATTLE
        screenManager.setScreen(ScreenType.WORLD)
        BattleResolver.resolveWin(battleId, spoils, player.position, currentNpcEntity, npcEntities)
        doBeforeLoadScreen()
        if (gameState == GameState.RUNNING) mapManager.continueAudio()
    }

    override fun onNotifyBattleLost() {
        screenManager.setScreen(ScreenType.MENU_MAIN)
    }

    override fun onNotifyBattleFled() {
        val mapTitle = profileManager.getLastSaveLocation()
        mapManager.loadMapAfterFleeing(mapTitle)
        screenManager.setScreen(ScreenType.WORLD)
    }
    //endregion

    override fun show() {
        if (gameState !in listOf(GameState.DIALOG, GameState.BATTLE)) mapManager.continueAudio()
        gameState = GameState.RUNNING
        setInputProcessors(multiplexer)
        Utils.disposeScreenshots()
    }

    override fun render(dt: Float) {
        when (gameState) {
            GameState.OFF,
            GameState.PAUSED -> Unit
            GameState.MINIMAP -> renderMiniMap()
            GameState.RUNNING -> updateAndRender(dt)
            GameState.DIALOG,
            GameState.BATTLE -> renderAll(dt)
        }
    }

    private fun updateAndRender(dt: Float) {
        updateEntities(dt)
        renderAll(dt)
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
        mapManager.getParticleEffects().forEach { it.update(dt) }
    }

    private fun renderMiniMap() {
        updateCameraPosition()
        worldRenderer.renderMapWithoutEntities()
        // todo, eventually remove shaperenderer and use sprite icons for minimap.
        shapeRenderer.projectionMatrix = camera.combined
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        player.renderOnMiniMap(worldRenderer.batch, shapeRenderer)
        npcEntities.forEach { it.renderOnMiniMap(worldRenderer.batch, shapeRenderer) }
        visibleScheduledEntities.forEach { it.renderOnMiniMap(worldRenderer.batch, shapeRenderer) }
        fogOfWarManager.draw(shapeRenderer)
        shapeRenderer.end()
    }

    private fun renderAll(dt: Float) {
        fogOfWarManager.update(player.position, dt)
        mapManager.updateConditionLayers()
        updateCameraPosition()
        worldRenderer.renderAll(player.position) { renderEntities(it) }
        gridRenderer.possibleRender()
        debugRenderer.possibleRenderObjects(doorList + lootList + npcEntities + visibleScheduledEntities + player)
        debugBox.possibleUpdate(dt)
        buttonsBox.update(dt)
        movementBox.update(player.moveSpeed, dt)
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
        worldRenderer.updateCamera()
    }

    private fun renderEntities(batch: Batch) {
        lootList.forEach { it.render(batch) }
        (doorList + npcEntities + visibleScheduledEntities + player)
            .sortedByDescending { it.position.y }
            .forEach { it.render(batch) }
    }

    private fun doBeforeLoadScreen() {
        player.resetInput()
        render(0f)
    }

    private fun openMiniMap() {
        if (camera.isZoomPossible()) {
            camera.zoom()
            camera.setInitPosition(player.position)
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
        camera.setInitPosition(player.position)
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
        get() = stage.actors.notEmpty() && gameState != GameState.DIALOG && stage.actors.peek() is TransitionImage

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
        worldRenderer.dispose()
        shapeRenderer.dispose()
        partyWindow.dispose()
        conversationDialog.dispose()
        messageDialog.dispose()
        debugRenderer.dispose()
        gridRenderer.dispose()
        debugBox.dispose()
        buttonsBox.dispose()
        movementBox.dispose()
        stage.dispose()
    }

}
