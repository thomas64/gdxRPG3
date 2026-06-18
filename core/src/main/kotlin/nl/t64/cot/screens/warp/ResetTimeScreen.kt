package nl.t64.cot.screens.warp

import com.badlogic.gdx.scenes.scene2d.actions.Actions
import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.mapManager
import nl.t64.cot.Utils.profileManager
import nl.t64.cot.Utils.screenManager
import nl.t64.cot.Utils.worldScreen
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe
import nl.t64.cot.components.portal.Portal
import nl.t64.cot.constants.Constant
import nl.t64.cot.constants.ScreenType
import nl.t64.cot.screens.inventory.InventoryScreen


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
        val inventorySnapshot = stage.actors.first()

        // put the inventory's own grey blurred world background underneath, so the inventory
        // and the parchment can fade out together onto it - exactly like closing the inventory -
        // instead of the inventory screen popping straight to the world.
        val inventoryScreen = screenManager.getScreen(ScreenType.INVENTORY) as InventoryScreen
        inventoryScreen.createBlurredBackgroundCopy()?.let { stage.root.addActorAt(0, it) }

        playSe(AudioEvent.SE_SCROLL)
        inventorySnapshot.addAction(Actions.fadeOut(Constant.FADE_DURATION))
        parchment.addAction(Actions.sequence(
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
            profileManager.autoSave()
            worldScreen.changeMap(mapManager.currentMap)
        }
        worldScreen.warpReset(actionAfterFade)
    }

}
