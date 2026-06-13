package nl.t64.cot.screens.warp

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.mapManager
import nl.t64.cot.Utils.screenManager
import nl.t64.cot.Utils.worldScreen
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe
import nl.t64.cot.components.portal.Portal
import nl.t64.cot.constants.Constant
import nl.t64.cot.constants.ScreenType
import nl.t64.cot.screens.inventory.InventoryScreen
import nl.t64.cot.sfx.TransitionPurpose


class ResetTimeScreen : PortalSelectionScreen("   Reset and return to") {

    companion object {
        fun load() {
            playSe(AudioEvent.SE_SCROLL)
            screenManager.openParchmentLoadScreen(ScreenType.RESET_TIME)
        }
    }

    override fun populateList() = portalListTable.populateResetTimePortalList()

    override fun onConfirm() {
        if (portalListTable.portalList.selectedIndex == -1) return

        val parchment = prepareBackgroundForFade()

        parchment.addAction(Actions.sequence(
            Actions.run { playSe(AudioEvent.SE_SCROLL) },
            Actions.fadeOut(Constant.FADE_DURATION),
            Actions.run {
                val selectedPortal = portalListTable.portalList.selected
                screenManager.setScreen(ScreenType.WORLD)
                resetTime(selectedPortal)
            }
        ))
    }

    override fun onClose() {
        InventoryScreen.loadFromCancelCrystal()
        closeScreen(ScreenType.INVENTORY)
    }

    private fun resetTime(portal: Portal) {
        val warpToMapName = portal.name.lowercase()
        playSe(AudioEvent.SE_RESET)
        val actionAfterFade = {
            gameData.resetCycle()
            mapManager.loadMapWithBgsSwitch(warpToMapName)
            mapManager.currentMap.setPlayerSpawnLocationWithId(warpToMapName)
            worldScreen.changeMap(mapManager.currentMap)
        }
        worldScreen.fadeOut(transitionColor = Color.GRAY,
                            duration = 1f,
                            transitionPurpose = TransitionPurpose.MAP_CHANGE,
                            actionAfterFade = actionAfterFade)
    }

}
