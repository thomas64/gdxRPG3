package nl.t64.cot.screens.world

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.Screen
import com.badlogic.gdx.ai.pfa.DefaultGraphPath
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import nl.t64.cot.Utils
import nl.t64.cot.Utils.audioManager
import nl.t64.cot.Utils.brokerManager
import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.mapManager
import nl.t64.cot.Utils.screenManager
import nl.t64.cot.audio.AudioCommand
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.components.loot.Loot
import nl.t64.cot.components.loot.Spoil
import nl.t64.cot.constants.Constant
import nl.t64.cot.constants.GameState
import nl.t64.cot.constants.ScreenType
import nl.t64.cot.screens.academy.AcademyScreen
import nl.t64.cot.screens.battle.BattleScreen
import nl.t64.cot.screens.inventory.tooltip.MessageTooltip
import nl.t64.cot.screens.loot.FindScreen
import nl.t64.cot.screens.loot.ReceiveScreen
import nl.t64.cot.screens.loot.RewardScreen
import nl.t64.cot.screens.loot.SpoilsScreen
import nl.t64.cot.screens.school.SchoolScreen
import nl.t64.cot.screens.shop.ShopScreen
import nl.t64.cot.screens.world.conversation.ConversationDialog
import nl.t64.cot.screens.world.conversation.ConversationObserver
import nl.t64.cot.screens.world.entity.Entity
import nl.t64.cot.screens.world.entity.GraphicsPlayer
import nl.t64.cot.screens.world.entity.InputPlayer
import nl.t64.cot.screens.world.entity.PhysicsPlayer
import nl.t64.cot.screens.world.entity.events.LoadEntityEvent
import nl.t64.cot.screens.world.entity.events.PathUpdateEvent
import nl.t64.cot.screens.world.pathfinding.TiledNode
import nl.t64.cot.sfx.TransitionImage
import nl.t64.cot.subjects.*


class WorldScreen : Screen,
    MapObserver, ComponentObserver, PartyObserver, LootObserver, ConversationObserver, QuestObserver, BattleObserver {

    private lateinit var previousGameState: GameState
    private lateinit var gameState: GameState

    private val stage = Stage()
    private val camera = Camera()
    private val mapRenderer = TextureMapObjectRenderer(camera)
    private val multiplexer = InputMultiplexer().apply { addProcessor(createListener()) }
    private val shapeRenderer = ShapeRenderer()
    private val partyWindow = PartyWindow()
    private val conversationDialog = ConversationDialog()
    private val messageDialog = MessageDialog(multiplexer)
    private val messageTooltip = MessageTooltip()

    private val player = Entity(Constant.PLAYER_ID, InputPlayer(multiplexer), PhysicsPlayer(), GraphicsPlayer())
    private val debugBox = DebugBox(player)
    private val buttonsBox = ButtonBox()

    private lateinit var partyMembers: List<Entity>
    private lateinit var npcEntities: List<Entity>
    private lateinit var currentNpcEntity: Entity
    private lateinit var lootList: List<Entity>
    private lateinit var doorList: List<Entity>

    private var showGrid = false
    private var showObjects = false
    private var showDebug = false

    init {
        conversationDialog.conversationObservers.addObserver(this)
        brokerManager.questObservers.addObserver(this)
        brokerManager.componentObservers.addObserver(this)
        brokerManager.mapObservers.addObserver(this)
        brokerManager.partyObservers.addObserver(this)
        brokerManager.lootObservers.addObserver(this)
        brokerManager.battleObservers.addObserver(this)
    }

    // MapObserver /////////////////////////////////////////////////////////////////////////////////////////////////////

    override fun onNotifyFadeOut(actionAfterFade: () -> Unit, transitionColor: Color) {
        fadeOut(actionAfterFade, transitionColor)
    }

    override fun onNotifyMapChanged(currentMap: GameMap) {
        mapRenderer.map = currentMap.tiledMap
        camera.setNewMapSize(currentMap.pixelWidth, currentMap.pixelHeight)
        player.send(LoadEntityEvent(currentMap.playerSpawnDirection, currentMap.playerSpawnLocation))
        npcEntities = NpcEntitiesLoader(currentMap).createNpcs()
        lootList = LootLoader(currentMap).createLoot()
        doorList = DoorLoader(currentMap).createDoors()
        partyMembers = PartyMembersLoader(player).loadPartyMembers()
        currentMap.setTiledGraphs()
    }

    override fun onNotifyShakeCamera() {
        camera.startShaking()
    }

    override fun onNotifyStartCutscene(cutsceneId: String) {
        val cutscenes = gameData.cutscenes
        if (!cutscenes.isPlayed(cutsceneId)) {
            cutscenes.setPlayed(cutsceneId)
            doBeforeLoadScreen()
            fadeOut({ screenManager.setScreen(ScreenType.valueOf(cutsceneId.uppercase())) }, Color.BLACK)
        }
    }

    // ComponentObserver ///////////////////////////////////////////////////////////////////////////////////////////////

    override fun onNotifyShowConversationDialog(conversationId: String, npcEntity: Entity) {
        currentNpcEntity = npcEntity
        player.resetInput()
        gameState = GameState.DIALOG
        conversationDialog.loadConversation(conversationId, npcEntity.id)
        conversationDialog.show()
    }

    override fun onNotifyShowConversationDialog(conversationId: String, entityId: String) {
        player.resetInput()
        gameState = GameState.DIALOG
        conversationDialog.loadConversation(conversationId, entityId)
        conversationDialog.show()
    }

    override fun onNotifyShowNoteDialog(noteId: String) {
        player.resetInput()
        gameState = GameState.DIALOG
        conversationDialog.loadNote(noteId)
        conversationDialog.show()
    }

    override fun onNotifyShowFindDialog(loot: Loot, event: AudioEvent, message: String) {
        doBeforeLoadScreen()
        gameState = GameState.DIALOG
        messageDialog.setActionAfterHide { FindScreen.load(loot, event) }
        messageDialog.show(message, AudioEvent.SE_CONVERSATION_NEXT)
    }

    override fun onNotifyShowFindDialog(loot: Loot, event: AudioEvent) {
        doBeforeLoadScreen()
        FindScreen.load(loot, event)
    }

    override fun onNotifyShowMessageDialog(message: String) {
        player.resetInput()
        gameState = GameState.DIALOG
        messageDialog.setActionAfterHide { gameState = GameState.RUNNING }
        messageDialog.show(message, AudioEvent.SE_CONVERSATION_NEXT)
    }

    override fun onNotifyShowBattleScreen(battleId: String) {
        onNotifyShowBattleScreen(battleId, currentNpcEntity)
    }

    override fun onNotifyShowBattleScreen(battleId: String, enemyEntity: Entity) {
        if (player.moveSpeed != Constant.MOVE_SPEED_4 && !isInTransition) {
            currentNpcEntity = enemyEntity
            mapManager.prepareForBattle()
            gameState = GameState.BATTLE
            doBeforeLoadScreen()
            fadeOut({ BattleScreen.load(battleId) }, Color.BLACK)
        }
    }

    // PartyObserver ///////////////////////////////////////////////////////////////////////////////////////////////////

    override fun onNotifyHeroDismissed() {
        partyMembers = PartyMembersLoader(player).loadPartyMembers()
    }

    // LootObserver ////////////////////////////////////////////////////////////////////////////////////////////////////

    override fun onNotifySpoilsUpdated() {
        onNotifyLootTaken()
    }

    override fun onNotifyLootTaken() {
        lootList.forEach { brokerManager.actionObservers.removeObserver(it) }
        lootList.forEach { brokerManager.blockObservers.removeObserver(it) }
        lootList = LootLoader(mapManager.currentMap).createLoot()
    }

    override fun onNotifyRewardTaken() {
//        val conversationId = currentNpcEntity.getConversationId()
//        gameData.quests.finish(conversationId)
//        val conversation = gameData.conversations.getConversationById(conversationId)
//        conversation.currentPhraseId = Constant.PHRASE_ID_QUEST_FINISHED
    }

    override fun onNotifyReceiveTaken() {
//        val conversationId = currentNpcEntity.getConversationId()
//        gameData.quests.accept(conversationId)
//        val conversation = gameData.conversations.getConversationById(conversationId)
//        conversation.currentPhraseId = Constant.PHRASE_ID_QUEST_ACCEPT
    }

    // ConversationObserver ////////////////////////////////////////////////////////////////////////////////////////////

    override fun onNotifyExitConversation() {
        conversationDialog.hideWithFade()
        show()
    }

    // also QuestObserver
    override fun onNotifyShowMessageTooltip(message: String) {
        messageTooltip.show(message, stage)
    }

    override fun onNotifyShowLevelUpDialog(message: String) {
        player.resetInput()
        messageDialog.show(message, AudioEvent.SE_LEVELUP)
    }

    override fun onNotifyLoadShop() {
        conversationDialog.hide()
        show()
        doBeforeLoadScreen()
        ShopScreen.load(currentNpcEntity.id, currentNpcEntity.getConversationId())
    }

    override fun onNotifyLoadAcademy() {
        conversationDialog.hide()
        show()
        doBeforeLoadScreen()
        AcademyScreen.load(currentNpcEntity.id, currentNpcEntity.getConversationId())
    }

    override fun onNotifyLoadSchool() {
        conversationDialog.hide()
        show()
        doBeforeLoadScreen()
        SchoolScreen.load(currentNpcEntity.id, currentNpcEntity.getConversationId())
    }

    override fun onNotifyShowRewardDialog(reward: Loot, levelUpMessage: String?) {
        stage.addAction(Actions.sequence(Actions.run { conversationDialog.hideWithFade() },
                                         Actions.delay(Constant.DIALOG_FADE_OUT_DURATION),
                                         Actions.run { RewardScreen.load(reward, levelUpMessage) }))
    }

    override fun onNotifyShowReceiveDialog(receive: Loot) {
        stage.addAction(Actions.sequence(Actions.run { conversationDialog.hideWithFade() },
                                         Actions.delay(Constant.DIALOG_FADE_OUT_DURATION),
                                         Actions.run { ReceiveScreen.load(receive) }))
    }

    override fun onNotifyHeroJoined() {
        brokerManager.blockObservers.removeObserver(currentNpcEntity)
        npcEntities = npcEntities.filter { it != currentNpcEntity }
        partyMembers = PartyMembersLoader(player).loadPartyMembers()
    }

    // BattleObserver //////////////////////////////////////////////////////////////////////////////////////////////////

    override fun onNotifyBattleWon(battleId: String, spoils: Loot, levelUpMessage: String?) {
        if (gameData.quests.contains(battleId)) {
            gameData.quests.getQuestById(battleId).setKillTaskComplete()
        }

        refreshNpcEntitiesListAfterBattle(battleId)
        partyMembers = PartyMembersLoader(player).loadPartyMembers()
        if (!spoils.isTaken()) {
            gameData.spoils.addSpoil(battleId, Spoil(mapManager.currentMap.mapTitle,
                                                     player.position.x, player.position.y, spoils))
            doBeforeLoadScreen()
            SpoilsScreen.load(spoils, levelUpMessage)
        } else {
            levelUpMessage?.let { messageDialog.show(levelUpMessage, AudioEvent.SE_LEVELUP) }
        }
    }

    private fun refreshNpcEntitiesListAfterBattle(battleId: String) {
        if (currentNpcEntity.isNpc()) {
            val remainingNpcEntities = mutableListOf<Entity>()
            npcEntities.forEach {
                if (it.isNpc() && it.getConversationId() == battleId) {
                    brokerManager.blockObservers.removeObserver(it)
                } else {
                    remainingNpcEntities.add(it)
                }
            }
            npcEntities = remainingNpcEntities
        } else {
            npcEntities = npcEntities.filter { it != currentNpcEntity }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    override fun show() {
        gameState = GameState.RUNNING
        Gdx.input.inputProcessor = multiplexer
        Utils.setGamepadInputProcessor(multiplexer)
        mapManager.continueAudio()
    }

    override fun render(dt: Float) {
        when (gameState) {
            GameState.PAUSED -> { // do nothing here
            }
            GameState.MINIMAP -> renderMiniMap()
            GameState.RUNNING, GameState.DIALOG, GameState.BATTLE -> renderAll(dt)
        }
    }

    private fun renderMiniMap() {
        updateCameraPosition()
        mapRenderer.renderMap()
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
        mapManager.updateQuestLayers()
        if (gameState != GameState.DIALOG && gameState != GameState.BATTLE) {
            updateEntities(dt)
        }
        updateCameraPosition()
        mapRenderer.renderAll(player.position) { renderEntities() }
        renderGrid()
        renderObjects()
        updateDebugBox(dt)
        buttonsBox.update(dt)
        partyWindow.update(dt)
        conversationDialog.update(dt)
        messageDialog.update(dt)

        stage.act(dt)
        if (isInTransition) {
            player.resetInput()
            mapManager.fadeAudio()
        }
        stage.draw()
    }

    private fun updateEntities(dt: Float) {
        if (!isInTransition) {
            player.update(dt)
        }
        doorList.forEach { it.update(dt) }
        lootList.forEach { it.update(dt) }
        npcEntities.forEach { it.send(PathUpdateEvent(getPathOf(it))) }
        npcEntities.forEach { it.update(dt) }
        partyMembers.forEach { it.send(PathUpdateEvent(getPathOf(it))) }
        partyMembers.forEach { it.update(dt) }
    }

    private fun updateCameraPosition() {
        camera.setPosition(player.position)
        mapRenderer.updateCamera()
    }

    private fun renderEntities() {
        lootList.forEach { it.render(mapRenderer.batch) }
        doorList
            .filter { it.position.y >= player.position.y }
            .forEach { it.render(mapRenderer.batch) }

        val allEntities: MutableList<Entity> = ArrayList()
        allEntities.addAll(partyMembers)
        allEntities.addAll(npcEntities)
        allEntities.add(player)
        allEntities.sortByDescending { it.position.y }
        allEntities.forEach { it.render(mapRenderer.batch) }

        doorList
            .filter { it.position.y < player.position.y }
            .forEach { it.render(mapRenderer.batch) }
    }

    private fun getPathOf(npc: Entity): DefaultGraphPath<TiledNode> {
        val startPoint = npc.getPositionInGrid()
        val endPoint = getEndPoint(npc)
        return mapManager.findPath(startPoint, endPoint, npc.state)
    }

    private fun getEndPoint(npc: Entity): Vector2 {
        val index = partyMembers.indexOf(npc)
        return when {
            index <= 0 -> player.getPositionInGrid()
            else -> partyMembers[index - 1].getPositionInGrid()
        }
    }

    private fun fadeOut(actionAfterFade: () -> Unit, transitionColor: Color) {
        val transition = TransitionImage(transitionColor)
        stage.addActor(transition)
        transition.addAction(Actions.sequence(Actions.alpha(0f),
                                              Actions.fadeIn(Constant.FADE_DURATION),
                                              Actions.run(actionAfterFade),
                                              Actions.removeActor()))
    }

    private fun doBeforeLoadScreen() {
        player.resetInput()
        render(0f)
    }

    private fun showHidePartyWindow() {
        partyWindow.showHide()
    }

    private fun openMiniMap() {
        if (camera.zoom()) {
            audioManager.handle(AudioCommand.SE_PLAY_ONCE, AudioEvent.SE_MINIMAP)
            gameState = GameState.MINIMAP
            multiplexer.removeProcessor(0)
            multiplexer.addProcessor(0, MiniMapListener { closeMiniMap() })
        } else {
            audioManager.handle(AudioCommand.SE_PLAY_ONCE, AudioEvent.SE_MENU_ERROR)
        }
    }

    private fun closeMiniMap() {
        audioManager.handle(AudioCommand.SE_PLAY_ONCE, AudioEvent.SE_MINIMAP)
        gameState = GameState.RUNNING
        multiplexer.removeProcessor(0)
        multiplexer.addProcessor(0, createListener())
        camera.reset()
    }

    private fun createListener(): WorldScreenListener {
        return WorldScreenListener({ doBeforeLoadScreen() },
                                   { showHidePartyWindow() },
                                   { openMiniMap() },
                                   { setShowGrid() },
                                   { setShowObjects() },
                                   { setShowDebug() })
    }

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
        Gdx.input.inputProcessor = null
        Utils.setGamepadInputProcessor(null)
    }

    override fun dispose() {
        player.dispose()
        mapRenderer.dispose()
        shapeRenderer.dispose()
        partyWindow.dispose()
        conversationDialog.dispose()
        messageDialog.dispose()
        debugBox.dispose()
        buttonsBox.dispose()
        stage.dispose()
    }

    private fun setShowGrid() {
        if (Utils.preferenceManager.isInDebugMode) showGrid = !showGrid
    }

    private fun setShowObjects() {
        if (Utils.preferenceManager.isInDebugMode) showObjects = !showObjects
    }

    private fun setShowDebug() {
        if (Utils.preferenceManager.isInDebugMode) showDebug = !showDebug
    }

    private fun renderGrid() {
        if (showGrid) {
            shapeRenderer.projectionMatrix = camera.combined
            shapeRenderer.color = Color.DARK_GRAY
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line)

            val mapPixelWidth = mapManager.currentMap.pixelWidth
            val mapPixelHeight = mapManager.currentMap.pixelHeight
            var x = 0f
            while (x < mapPixelWidth) {
                shapeRenderer.line(x, 0f, x, mapPixelHeight)
                x += Constant.TILE_SIZE
            }
            var y = 0f
            while (y < mapPixelHeight) {
                shapeRenderer.line(0f, y, mapPixelWidth, y)
                y += Constant.TILE_SIZE
            }
            shapeRenderer.end()
        }
    }

    private fun renderObjects() {
        if (showObjects) {
            shapeRenderer.projectionMatrix = camera.combined
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
            player.debug(shapeRenderer)
            doorList.forEach { it.debug(shapeRenderer) }
            lootList.forEach { it.debug(shapeRenderer) }
            npcEntities.forEach { it.debug(shapeRenderer) }
            partyMembers.forEach { it.debug(shapeRenderer) }
            mapManager.currentMap.debug(shapeRenderer)
            shapeRenderer.end()
        }
    }

    private fun updateDebugBox(dt: Float) {
        if (showDebug) {
            debugBox.update(dt)
        }
    }

}
