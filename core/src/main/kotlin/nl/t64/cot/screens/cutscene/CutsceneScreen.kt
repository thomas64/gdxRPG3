package nl.t64.cot.screens.cutscene

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.ScreenUtils
import nl.t64.cot.Utils
import nl.t64.cot.Utils.audioManager
import nl.t64.cot.Utils.mapManager
import nl.t64.cot.Utils.resourceManager
import nl.t64.cot.Utils.screenManager
import nl.t64.cot.audio.playSe
import nl.t64.cot.audio.stopAllBgm
import nl.t64.cot.components.loot.Loot
import nl.t64.cot.constants.Constant
import nl.t64.cot.constants.ScreenType
import nl.t64.cot.screens.battle.BattleObserver
import nl.t64.cot.screens.world.Camera
import nl.t64.cot.screens.world.WorldRenderer
import nl.t64.cot.screens.world.conversation.ConversationDialog
import nl.t64.cot.screens.world.conversation.ConversationObserver
import nl.t64.cot.sfx.TransitionAction
import nl.t64.cot.sfx.TransitionImage
import nl.t64.cot.sfx.TransitionType


private const val TITLE_FONT = "fonts/spectral_regular_24.ttf"
private const val FONT_SIZE = 24
const val NORMAL_STEP = 0.5f
const val FAST_STEP = 0.25f

abstract class CutsceneScreen : Screen, ConversationObserver, BattleObserver {

    val camera = Camera()
    private val worldRenderer = WorldRenderer(camera)
    val actorsStage = Stage(camera.viewport)
    val transitionStage = Stage(camera.viewport)
    val transition: Actor = TransitionImage()
    val title: Label = createTitle()

    lateinit var actions: List<Action>
    var isBgmFading: Boolean = false

    private lateinit var conversationDialog: ConversationDialog
    private var actionId = 0
    private var followingActor = Actor()
    private var isCameraFixed: Boolean = true
    private val skipBox = SkipBox()
    private var isEnding: Boolean = false


    private fun createTitle(): Label {
        val font = resourceManager.getTrueTypeAsset(TITLE_FONT, FONT_SIZE)
        val style = LabelStyle(font, Color.WHITE)
        return Label("", style).apply {
            setAlignment(Align.center)
            isVisible = false
        }
    }

    override fun show() {
        conversationDialog = ConversationDialog(this)
        actionId = 0
        isEnding = false

        stopAllBgm()

        title.setText("")
        title.clearActions()

        transitionStage.clear()
        transitionStage.addActor(transition)
        transitionStage.addActor(title)

        actorsStage.clear()
        Gdx.input.inputProcessor = actorsStage
        Utils.setGamepadInputProcessor(actorsStage)
        actorsStage.addAction(Actions.sequence(Actions.delay(1f),
                                               Actions.addListener(CutSceneListener { exitScreen() }, false)))

        prepare()
        actorsStage.addAction(actions[actionId])
    }

    override fun render(dt: Float) {
        ScreenUtils.clear(Color.BLACK)

        if (isCameraFixed) {
            camera.update()
        } else {
            camera.setPosition(followingActor.x, followingActor.y)
        }

        worldRenderer.updateCamera()
        actorsStage.act(dt)
        worldRenderer.renderAll(Vector2(followingActor.x, followingActor.y)) { actorsStage.draw() }

        conversationDialog.update(dt)

        transitionStage.act(dt)
        if (isBgmFading) {
            audioManager.certainFadeBgmBgs()
        }
        transitionStage.draw()
        if (!conversationDialog.isVisible() && !isEnding) {
            skipBox.update(dt)
        }
    }

    override fun resize(width: Int, height: Int) {
        // empty
    }

    override fun pause() {
        // empty
    }

    override fun resume() {
        // empty
    }

    override fun hide() {
        // empty
    }

    override fun dispose() {
        worldRenderer.dispose()
        conversationDialog.dispose()
        actorsStage.dispose()
        transitionStage.dispose()
        skipBox.dispose()
    }

    override fun onNotifyExitConversation() {
        Gdx.input.inputProcessor = actorsStage
        Utils.setGamepadInputProcessor(actorsStage)
        actionId++
        actorsStage.addAction(actions[actionId])
    }

    override fun onNotifyBattleWon(battleId: String, spoils: Loot) {
        throw IllegalStateException("Implement this method in child.")
    }

    override fun onNotifyBattleLost() {
        throw IllegalStateException("Implement this method in child.")
    }

    override fun onNotifyBattleFled() {
        throw IllegalStateException("Implement this method in child.")
    }

    abstract fun prepare()

    abstract fun exitScreen()

    fun endCutsceneAndOpenMap(mapTitle: String, spawnId: String) {
        endCutsceneAndOpenMapAnd(mapTitle, spawnId) {}
    }

    fun endCutsceneAndOpenMapAnd(mapTitle: String, spawnId: String = mapTitle, actionAfter: () -> Unit) {
        endCutsceneAnd {
            openMap(mapTitle, spawnId)
            actionAfter.invoke()
        }
    }

    fun endCutsceneAnd(actionAfter: () -> Unit) {
        isEnding = true
        Gdx.input.inputProcessor = null
        Utils.setGamepadInputProcessor(null)
        actorsStage.addAction(Actions.sequence(
            if (title.isVisible) fadeFromTitle() else fadeFromMap(),
            Actions.run { actionAfter.invoke() }
        ))
    }

    private fun openMap(mapTitle: String, spawnId: String) {
        mapManager.loadMapAfterCutscene(mapTitle, spawnId)
        screenManager.setScreen(ScreenType.WORLD)
    }

    private fun fadeFromTitle(): Action {
        return Actions.sequence(
            Actions.addAction(Actions.sequence(
                Actions.run { isBgmFading = true },
                Actions.fadeOut(Constant.FADE_DURATION),
                Actions.visible(false),
                Actions.alpha(1f),
                Actions.run { isBgmFading = false }
            ), title),
            Actions.delay(1f)
        )
    }

    private fun fadeFromMap(): Action {
        return Actions.sequence(
            actionFadeOut(),
            Actions.delay(1f),
            Actions.addAction(Actions.alpha(1f), transition)
        )
    }

    fun setMapWithHardBgmBgs(mapId: String) {
        mapManager.loadMapWithHardBgmBgsSwitch(mapId)
        setNewMap()
    }

    fun setMapWithNoSound(mapId: String) {
        mapManager.loadMap(mapId)
        setNewMap()
    }

    fun setMapWithBgmBgs(mapId: String) {
        mapManager.loadMapWithBgmBgs(mapId)
        setNewMap()
    }

    fun setMapWithBgsOnly(mapId: String) {
        mapManager.loadMapWithBgs(mapId)
        setNewMap()
    }

    private fun setNewMap() {
        val currentMap = mapManager.currentMap
        camera.setNewMapSize(currentMap.pixelWidth, currentMap.pixelHeight)
        worldRenderer.map = mapManager.getTiledMap()
    }

    fun showConversationDialog(conversationId: String, entityId: String, faceColor: Color? = null) {
        conversationDialog.loadConversation(conversationId, entityId)
        faceColor?.let { conversationDialog.setFaceColor(it) }
        conversationDialog.show()
    }

    fun followActor(actor: Actor) {
        followingActor = actor
        isCameraFixed = false
    }

    fun setCameraPosition(x: Float, y: Float) {
        camera.setPosition(x, y)
        isCameraFixed = true
    }

    fun actionFadeIn(): Action {
        return Actions.sequence(
            Actions.addAction(TransitionAction(TransitionType.FADE_IN), transition),
            Actions.delay(Constant.FADE_DURATION)
        )
    }

    fun actionFadeOut(): Action {
        return Actions.sequence(
            Actions.run { isBgmFading = true },
            Actions.addAction(TransitionAction(TransitionType.FADE_OUT), transition),
            Actions.delay(Constant.FADE_DURATION),
            Actions.run { isBgmFading = false }
        )
    }

    fun actionFadeOutWithoutBgmFading(): Action {
        return Actions.sequence(
            Actions.addAction(TransitionAction(TransitionType.FADE_OUT), transition),
            Actions.delay(Constant.FADE_DURATION),
        )
    }

    fun actionMoveTo(actor: CutsceneActor, x: Float, y: Float, duration: Float, stepSpeed: Float): Action {
        return Actions.parallel(
            Actions.moveTo(x, y, duration),
            actionWalkSound(actor, duration, stepSpeed)
        )
    }

    fun actionMoveBy(actor: CutsceneActor, amountX: Float, amountY: Float, duration: Float, stepSpeed: Float): Action {
        return Actions.parallel(
            Actions.moveBy(amountX, amountY, duration),
            actionWalkSound(actor, duration, stepSpeed)
        )
    }

    fun actionWalkSound(actor: CutsceneActor, duration: Float, stepSpeed: Float): Action {
        val nTimes = (duration / stepSpeed).toInt()
        return Actions.repeat(nTimes, Actions.sequence(
            Actions.run { playSe(mapManager.getGroundSound(actor.x, actor.y)) },
            Actions.delay(stepSpeed)
        ))
    }

}
