package nl.t64.cot.screens.warp

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import nl.t64.cot.Utils.mapManager
import nl.t64.cot.Utils.screenManager
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe
import nl.t64.cot.constants.Constant
import nl.t64.cot.constants.ScreenType
import nl.t64.cot.sfx.TransitionImage


class WarpScreen : PortalSelectionScreen("   Warp to") {

    private lateinit var currentMapName: String

    companion object {
        fun load(currentMapName: String) {
            playSe(AudioEvent.SE_ACTIVATE)
            val warpScreen = screenManager.getScreen(ScreenType.WARP) as WarpScreen
            warpScreen.currentMapName = currentMapName
            screenManager.openParchmentLoadScreen(ScreenType.WARP)
        }
    }

    override fun populateList() = portalListTable.populateWarpPortalList(currentMapName)

    override fun onConfirm() {
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
            Actions.removeActor()
        ))
    }

    override fun onClose() = closeScreen()

}
