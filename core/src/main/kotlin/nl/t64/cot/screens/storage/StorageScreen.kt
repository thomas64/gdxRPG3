package nl.t64.cot.screens.storage

import com.badlogic.gdx.scenes.scene2d.actions.Actions
import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.screenManager
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe
import nl.t64.cot.constants.ScreenType
import nl.t64.cot.screens.ParchmentScreen
import nl.t64.cot.screens.ScreenUI
import nl.t64.cot.screens.inventory.InventoryUtils
import nl.t64.cot.screens.inventory.inventoryslot.InventorySlotsTable


class StorageScreen : ParchmentScreen() {

    private lateinit var storageUI: StorageUI
    private lateinit var listener: StorageScreenListener

    companion object {
        fun load() {
            playSe(AudioEvent.SE_CHEST)
            screenManager.openParchmentLoadScreen(ScreenType.STORAGE)
        }
    }

    override fun getScreenUI(): ScreenUI {
        return storageUI
    }

    override fun show() {
        setInputProcessors(stage)
        createAndSetListener()
        addInputListenerWithSmallDelay()
        storageUI = StorageUI(stage)
        StorageButtonLabels(stage).create()
    }

    override fun render(dt: Float) {
        renderStage(dt)
        storageUI.update()
    }

    override fun removeTriggersListener() {
        listener.removeTriggers()
    }

    private fun addInputListenerWithSmallDelay() {
        stage.addAction(Actions.sequence(Actions.delay(.1f),
                                         Actions.addListener(listener, false)))
    }

    private fun createAndSetListener() {
        listener = StorageScreenListener(stage,
                                         { closeScreen() },
                                         { takeOne() },
                                         { takeHalf() },
                                         { takeFull() },
                                         { equip() },
                                         { selectPreviousHero() },
                                         { selectNextHero() },
                                         { selectPreviousTable() },
                                         { selectNextTable() },
                                         { sortStorage() },
                                         { toggleTooltip() },
                                         { toggleCompare() })
    }

    private fun takeOne() {
        storageUI.takeOne()
    }

    private fun takeHalf() {
        storageUI.takeHalf()
    }

    private fun takeFull() {
        storageUI.takeFull()
    }

    private fun equip() {
        storageUI.equip()
    }

    private fun selectPreviousHero() {
        playSe(AudioEvent.SE_MENU_CURSOR)
        storageUI.updateSelectedHero { InventoryUtils.selectPreviousHero() }
    }

    private fun selectNextHero() {
        playSe(AudioEvent.SE_MENU_CURSOR)
        storageUI.updateSelectedHero { InventoryUtils.selectNextHero() }
    }

    private fun selectPreviousTable() {
        playSe(AudioEvent.SE_MENU_CURSOR)
        storageUI.selectPreviousTable()
    }

    private fun selectNextTable() {
        playSe(AudioEvent.SE_MENU_CURSOR)
        storageUI.selectNextTable()
    }

    private fun sortStorage() {
        when (val selectedTable = storageUI.getSelectedTable()) {
            is StorageSlotsTable -> {
                playSe(AudioEvent.SE_MENU_CONFIRM)
                gameData.storage.sort()
                storageUI.reloadInventory()
            }
            is InventorySlotsTable -> {
                playSe(AudioEvent.SE_MENU_CONFIRM)
                gameData.inventory.sort()
                selectedTable.clearAndFill()
            }
            else -> {
                playSe(AudioEvent.SE_MENU_ERROR)
            }
        }
    }

    private fun toggleTooltip() {
        playSe(AudioEvent.SE_MENU_CONFIRM)
        storageUI.toggleTooltip()
    }

    private fun toggleCompare() {
        playSe(AudioEvent.SE_MENU_CONFIRM)
        storageUI.toggleCompare()
    }

}
