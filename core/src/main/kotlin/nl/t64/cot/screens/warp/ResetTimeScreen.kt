package nl.t64.cot.screens.warp

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
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
import nl.t64.cot.sfx.TransitionImage
import nl.t64.cot.sfx.TransitionPurpose


class ResetTimeScreen : PortalSelectionScreen("   Reset and return to") {

    companion object {
        fun load() {
            if (screenManager.getCurrentParchmentScreen() !is InventoryScreen) return

            playSe(AudioEvent.SE_SCROLL)
            screenManager.openParchmentLoadScreen(ScreenType.RESET_TIME)
        }
    }

    private val isYlarusDemo: Boolean get() = mapManager.currentMap.mapTitle == "ylarus_place"

    override fun populateList() {
        if (isYlarusDemo) {
            portalListTable.populateHomePortalOnly()
        } else {
            portalListTable.populateResetTimePortalList()
        }
    }

    override fun onConfirm() {
        if (portalListTable.portalList.selectedIndex == -1) return

        setInputProcessors(null)
        if (isYlarusDemo) {
            resetTimeDemo()
        } else {
            resetTimeToSelectedPortal()
        }
    }

    override fun onClose() {
        if (isYlarusDemo) {
            InventoryScreen.loadFromCancelTryCrystal()
        } else {
            InventoryScreen.loadFromCancelCrystal()
        }
        closeScreen(ScreenType.INVENTORY)
    }

    private fun resetTimeDemo() {
        val transition = TransitionImage(TransitionPurpose.JUST_FADE, Color.GRAY)
        stage.addActor(transition)
        transition.addAction(Actions.sequence(Actions.alpha(0f),
                                              Actions.run { playSe(AudioEvent.SE_RESET) },
                                              Actions.delay(0.2f),
                                              Actions.fadeIn(1f),
                                              Actions.delay(1.3f),
                                              Actions.run { startCycle4AfterThisFrame() }))
    }

    private fun startCycle4AfterThisFrame() {
        // switching mid-frame would clear this stage before it is drawn, flashing one black frame.
        Gdx.app.postRunnable { screenManager.setScreen(ScreenType.SCENE_CYCLE_4_BEGINS) }
    }

    private fun resetTimeToSelectedPortal() {
        val parchment = prepareBackgroundForFade()
        val inventorySnapshot = stage.actors.first()

        // put the untinted world screenshot underneath, so the inventory and the parchment fade out
        // onto exactly what the world screen shows next, instead of popping straight to the world.
        val inventoryScreen = screenManager.getScreen(ScreenType.INVENTORY) as InventoryScreen
        inventoryScreen.createWorldScreenshotCopy()?.let { stage.root.addActorAt(0, it) }

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
