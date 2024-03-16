package nl.t64.cot.screens.warp

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Window
import nl.t64.cot.Utils
import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.mapManager
import nl.t64.cot.Utils.preferenceManager
import nl.t64.cot.Utils.screenManager
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe
import nl.t64.cot.constants.Constant
import nl.t64.cot.constants.ScreenType
import nl.t64.cot.screens.ParchmentScreen
import nl.t64.cot.sfx.TransitionImage


private const val TITLE_PORTALS = "   Warp to"

class WarpScreen : ParchmentScreen() {

    private val portalListTable: PortalListTable = PortalListTable()
    private val portalListWindow: Window = Utils.createDefaultWindow(TITLE_PORTALS, portalListTable.container).apply {
        setPosition((Gdx.graphics.width / 2f) - (width / 2f), (Gdx.graphics.height / 2f) - (height / 2f))
    }
    private lateinit var currentMapName: String

    companion object {
        fun load(currentMapName: String) {
            playSe(AudioEvent.SE_SCROLL)
            val warpScreen = screenManager.getScreen(ScreenType.WARP) as WarpScreen
            warpScreen.currentMapName = currentMapName
            screenManager.openParchmentLoadScreen(ScreenType.WARP)
        }
    }

    override fun show() {
        setInputProcessors(stage)
        stage.addActor(portalListWindow)
        stage.addListener(WarpScreenListener({ warp() },
                                             { cheatActivateAllPortals() },
                                             { closeScreen() }))
        stage.keyboardFocus = portalListTable.portalList
        stage.scrollFocus = portalListTable.scrollPane
        portalListTable.populatePortalList(currentMapName)
    }

    override fun render(dt: Float) {
        renderStage(dt)
    }

    private fun warp() {
        if (portalListTable.portalList.selectedIndex == -1) return

        val transition = TransitionImage(color = Color.WHITE)
        val parchment = prepareBackgroundForFade()

        parchment.addAction(Actions.sequence(
            Actions.run { playSe(AudioEvent.SE_SCROLL) },
            Actions.fadeOut(Constant.FADE_DURATION)
        ))

        stage.addActor(transition)

        transition.addAction(Actions.sequence(
            Actions.alpha(0f),
            Actions.delay(Constant.FADE_DURATION),
            Actions.fadeIn(Constant.FADE_DURATION),
            Actions.delay(0.2f),
            Actions.run { playSe(AudioEvent.SE_WARP) },
            Actions.delay(Constant.FADE_DURATION),
            Actions.run {
                val warpToMapName = portalListTable.portalList.selected.name.lowercase()
                screenManager.setScreen(ScreenType.WORLD)
                mapManager.changeMapWithWarpPortal(warpToMapName)
            },
            Actions.removeActor(),
            Actions.run { stage.clear() }
        ))
    }

    private fun cheatActivateAllPortals() {
        if (preferenceManager.isInDebugMode) {
            playSe(AudioEvent.SE_MENU_ERROR)
            val portals = gameData.portals
            portals.getAllIds().forEach { portals.activate(it) }
            portalListTable.populatePortalList(currentMapName)
        }
    }

}
