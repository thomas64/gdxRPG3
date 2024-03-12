package nl.t64.cot.screens.inventory

import com.badlogic.gdx.scenes.scene2d.actions.Actions
import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.preferenceManager
import nl.t64.cot.Utils.screenManager
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe
import nl.t64.cot.components.party.inventory.InventoryDatabase
import nl.t64.cot.constants.ScreenType
import nl.t64.cot.screens.ParchmentScreen
import nl.t64.cot.screens.ScreenUI
import nl.t64.cot.screens.inventory.messagedialog.MessageDialog
import nl.t64.cot.screens.world.conversation.ConversationDialog
import nl.t64.cot.screens.world.conversation.ConversationObserver
import kotlin.concurrent.thread


class InventoryScreen : ParchmentScreen(), ConversationObserver {

    private val conversationDialog: ConversationDialog = ConversationDialog(this)
    private lateinit var inventoryUI: InventoryUI
    private lateinit var listener: InventoryScreenListener
    private var isListenerAdded: Boolean = false

    companion object {
        fun load() {
            playSe(AudioEvent.SE_SCROLL)
            screenManager.openParchmentLoadScreen(ScreenType.INVENTORY)
        }

        fun loadForCutsceneTryCrystal() {
            playSe(AudioEvent.SE_SCROLL)
            screenManager.openParchmentLoadScreen(ScreenType.INVENTORY)
            val inventoryScreen = (screenManager.getScreen(ScreenType.INVENTORY) as InventoryScreen)

            thread {
                while (true) {
                    if (inventoryScreen.isListenerAdded) {
                        inventoryScreen.stage.removeListener(inventoryScreen.listener)
                        Thread.sleep(500L)
                        val dialog = MessageDialog("Select and use the Crystal of Time to revert time by 12 hours.")
                        dialog.show(inventoryScreen.stage, AudioEvent.SE_CONVERSATION_NEXT)
                        inventoryScreen.createAndSetListener(closeScreenFunction = { playSe(AudioEvent.SE_MENU_ERROR) })
                        inventoryScreen.stage.addListener(inventoryScreen.listener)
                        break
                    }
                    Thread.sleep(10L)
                }
            }
        }
    }

    override fun onNotifyExitConversation() {
        setInputProcessors(stage)
    }

    override fun onNotifyHeroDismiss() {
        val selectedHero = InventoryUtils.getSelectedHero()
        gameData.heroes.addHero(selectedHero)
        val heroToDismiss = selectedHero.id
        selectPreviousHero()
        gameData.party.removeHero(heroToDismiss)
        screenManager.getWorldScreen().updateParty()
    }

    override fun getScreenUI(): ScreenUI {
        return inventoryUI
    }

    override fun show() {
        setInputProcessors(stage)
        createAndSetListener()
        addInputListenerWithSmallDelay()
        inventoryUI = InventoryUI(stage)
        ButtonLabels(stage).create()
    }

    override fun render(dt: Float) {
        renderStage(dt)
        inventoryUI.update()
        conversationDialog.update(dt)
    }

    override fun hide() {
        super.hide()
        isListenerAdded = false
        setInputProcessors(null)
        removeTriggersListener()
    }

    override fun dispose() {
        super.dispose()
        conversationDialog.dispose()
    }

    override fun removeTriggersListener() {
        listener.removeTriggers()
    }

    private fun addInputListenerWithSmallDelay() {
        stage.addAction(Actions.sequence(Actions.delay(0.1f),
                                         Actions.addListener(listener, false),
                                         Actions.run { isListenerAdded = true }))
    }

    fun closeScreenAnd(actionAfter: () -> Unit) {
        closeScreen()
        actionAfter.invoke()
    }

    private fun createAndSetListener(closeScreenFunction: () -> Unit = { closeScreen() }) {
        listener = InventoryScreenListener(stage,
                                           closeScreenFunction,
                                           { doAction() },
                                           { selectPreviousHero() },
                                           { selectNextHero() },
                                           { selectPreviousTable() },
                                           { selectNextTable() },
                                           { tryToDismissHero() },
                                           { sortInventory() },
                                           { toggleTooltip() },
                                           { toggleCompare() },
                                           { cheatAddGold() },
                                           { cheatRemoveGold() })
    }

    private fun doAction() {
        inventoryUI.doAction()
    }

    private fun selectPreviousHero() {
        playSe(AudioEvent.SE_MENU_CURSOR)
        inventoryUI.updateSelectedHero { InventoryUtils.selectPreviousHero() }
    }

    private fun selectNextHero() {
        playSe(AudioEvent.SE_MENU_CURSOR)
        inventoryUI.updateSelectedHero { InventoryUtils.selectNextHero() }
    }

    private fun selectPreviousTable() {
        playSe(AudioEvent.SE_MENU_CURSOR)
        inventoryUI.selectPreviousTable()
    }

    private fun selectNextTable() {
        playSe(AudioEvent.SE_MENU_CURSOR)
        inventoryUI.selectNextTable()
    }

    private fun tryToDismissHero() {
        val currentHero = InventoryUtils.getSelectedHero()
        if (gameData.party.isPlayer(currentHero.id)) {
            MessageDialog("You cannot dismiss the party leader.").show(stage, AudioEvent.SE_MENU_ERROR)
        } else if (currentHero.isAlive) {
            conversationDialog.loadConversation("dismiss_${currentHero.id}", currentHero.id)
            conversationDialog.show()
        } else {
            conversationDialog.loadConversation("bury_${currentHero.id}", currentHero.id)
            conversationDialog.show()
        }
    }

    private fun sortInventory() {
        playSe(AudioEvent.SE_MENU_CONFIRM)
        gameData.inventory.sort()
        inventoryUI.reloadInventory()
    }

    private fun toggleTooltip() {
        playSe(AudioEvent.SE_MENU_CONFIRM)
        inventoryUI.toggleTooltip()
    }

    private fun toggleCompare() {
        playSe(AudioEvent.SE_MENU_CONFIRM)
        inventoryUI.toggleCompare()
    }

    private fun cheatAddGold() {
        if (preferenceManager.isInDebugMode) {
            val inventory = gameData.inventory
            if (inventory.hasRoomForResource("gold")
                && inventory.hasRoomForResource("herb")
                && inventory.hasRoomForResource("spice")
                && inventory.hasRoomForResource("gemstone")
            ) {
                playSe(AudioEvent.SE_MENU_ERROR)
                gameData.party.gainXp(100, StringBuilder())
                inventory.autoSetItem(InventoryDatabase.createInventoryItem("gold", 100))
                inventory.autoSetItem(InventoryDatabase.createInventoryItem("herb", 100))
                inventory.autoSetItem(InventoryDatabase.createInventoryItem("spice", 100))
                inventory.autoSetItem(InventoryDatabase.createInventoryItem("gemstone", 100))
                inventoryUI.reloadInventory()
            }
        }
    }

    private fun cheatRemoveGold() {
        if (preferenceManager.isInDebugMode) {
            val inventory = gameData.inventory
            if (inventory.hasEnoughOfItem("gold", 1)) {
                playSe(AudioEvent.SE_MENU_ERROR)
                gameData.party.getAllHeroesAlive().forEach { it.takeDamage(1) }
                inventory.autoRemoveItem("gold", 1)
                inventoryUI.reloadInventory()
            }
        }
    }

}
