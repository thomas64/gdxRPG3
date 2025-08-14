package nl.t64.cot.screens.inventory

import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import nl.t64.cot.Utils
import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.preferenceManager
import nl.t64.cot.Utils.screenManager
import nl.t64.cot.Utils.worldScreen
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe
import nl.t64.cot.components.party.inventory.InventoryDatabase
import nl.t64.cot.constants.Constant
import nl.t64.cot.constants.ScreenType
import nl.t64.cot.screens.ParchmentScreen
import nl.t64.cot.screens.ScreenUI
import nl.t64.cot.screens.dialog.MessageDialog
import nl.t64.cot.screens.questlog.QuestLogScreen
import nl.t64.cot.screens.world.conversation.ConversationDialog
import nl.t64.cot.screens.world.conversation.ConversationObserver


class InventoryScreen : ParchmentScreen(), ConversationObserver {

    private val conversationDialog: ConversationDialog = ConversationDialog(this)
    private lateinit var inventoryUI: InventoryUI
    private lateinit var listener: InventoryScreenListener
    private var startingSelectedTableIndex: Int = 4
    private var isLoadedFromMechanicScreen: Boolean = false

    companion object {
        fun loadFromMechanic(screenShot: Image, parchment: Image) {
            playSe(AudioEvent.SE_MENU_CURSOR)
            (screenManager.getScreen(ScreenType.INVENTORY) as InventoryScreen).apply {
                this.setBackground(screenShot, parchment)
                this.startingSelectedTableIndex = 1
                this.isLoadedFromMechanicScreen = true

                this.createAndSetListener(openQuestLogFunction = { this.openQuestLogScreen() },
                                          closeScreenFunction = { this.closeScreen() },
                                          doActionFunction = { this.doAction() },
                                          tryToDropItemFunction = { this.tryToDropItem() },
                                          tryToDismissHeroFunction = { this.tryToDismissHero() })
                this.addInputListenerWithSmallDelay()
            }
            screenManager.setScreen(ScreenType.INVENTORY)
        }

        fun load() {
            loadAndGetInventoryScreen().apply {
                this.createAndSetListener(openQuestLogFunction = { this.openQuestLogScreen() },
                                          closeScreenFunction = { this.closeScreen() },
                                          doActionFunction = { this.doAction() },
                                          tryToDropItemFunction = { this.tryToDropItem() },
                                          tryToDismissHeroFunction = { this.tryToDismissHero() })
                this.addInputListenerWithSmallDelay()
            }
        }

        fun loadForCutsceneTryCrystal() {
            loadAndGetInventoryScreen().apply {
                this.stage.addAction(Actions.sequence(
                    Actions.delay(0.5f),
                    Actions.run {
                        val dialog = MessageDialog("Select and use the Crystal of Time to revert time by 12 hours.")
                        dialog.show(this.stage, AudioEvent.SE_CONVERSATION_NEXT, 2f)
                        this.createAndSetListener(openQuestLogFunction = { playSe(AudioEvent.SE_MENU_ERROR) },
                                                  closeScreenFunction = { dialog.show(this.stage, AudioEvent.SE_CONVERSATION_NEXT) },
                                                  doActionFunction = { this.doCrystalAction() },
                                                  tryToDropItemFunction = {},
                                                  tryToDismissHeroFunction = {})
                        this.addInputListenerWithSmallDelay()
                    }))
            }
        }

        fun loadForPreBattle() {
            loadAndGetInventoryScreen().apply {
                this.createAndSetListener(openQuestLogFunction = { playSe(AudioEvent.SE_MENU_ERROR) },
                                          closeScreenFunction = { this.closeScreen(ScreenType.BATTLE) },
                                          doActionFunction = { this.doPreBattleAction() },
                                          tryToDropItemFunction = {},
                                          tryToDismissHeroFunction = {})
                this.addInputListenerWithSmallDelay()
            }
        }

        fun loadForBattle() {
            loadAndGetInventoryScreen().apply {
                this.createAndSetListener(openQuestLogFunction = { playSe(AudioEvent.SE_MENU_ERROR) },
                                          closeScreenFunction = { this.closeScreen(ScreenType.BATTLE) },
                                          doActionFunction = { this.doBattleAction() },
                                          tryToDropItemFunction = {},
                                          tryToDismissHeroFunction = {})
                this.addInputListenerWithSmallDelay()
            }
        }

        private fun loadAndGetInventoryScreen(): InventoryScreen {
            playSe(AudioEvent.SE_SCROLL)
            screenManager.openParchmentLoadScreen(ScreenType.INVENTORY)
            return (screenManager.getScreen(ScreenType.INVENTORY) as InventoryScreen).apply {
                this.startingSelectedTableIndex = 4
                this.isLoadedFromMechanicScreen = false
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
        worldScreen.updateParty()
    }

    override fun getScreenUI(): ScreenUI {
        return inventoryUI
    }

    override fun show() {
        setInputProcessors(stage)
        inventoryUI = InventoryUI(stage, startingSelectedTableIndex, isLoadedFromMechanicScreen)
        ButtonLabels(stage).create()
    }

    override fun render(dt: Float) {
        renderStage(dt)
        inventoryUI.update()
        conversationDialog.update(dt)
    }

    override fun hide() {
        super.hide()
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
                                         Actions.addListener(listener, false)))
    }

    fun closeScreenAnd(actionAfter: () -> Unit) {
        closeScreen()
        actionAfter.invoke()
    }

    private fun createAndSetListener(openQuestLogFunction: () -> Unit,
                                     closeScreenFunction: () -> Unit,
                                     doActionFunction: () -> Unit,
                                     tryToDropItemFunction: () -> Unit,
                                     tryToDismissHeroFunction: () -> Unit) {
        listener = InventoryScreenListener(stage,
                                           closeScreenFunction,
                                           openQuestLogFunction,
                                           doActionFunction,
                                           { selectPreviousHero() },
                                           { selectNextHero() },
                                           { selectPreviousTable() },
                                           { selectNextTable() },
                                           tryToDropItemFunction,
                                           tryToDismissHeroFunction,
                                           { sortInventory() },
                                           { toggleTooltip() },
                                           { toggleCompare() },
                                           { cheatAddGold() },
                                           { cheatRemoveGold() })
    }

    private fun openQuestLogScreen() {
        closeScreen()
        Utils.runWithDelay(Constant.FADE_DURATION + 0.1f) { QuestLogScreen.load() }
    }

    private fun doCrystalAction() {
        inventoryUI.doCrystalAction()
    }

    private fun doPreBattleAction() {
        inventoryUI.doPreBattleAction()
    }

    private fun doBattleAction() {
        MessageDialog("This action is not allowed here during battle.").show(stage, AudioEvent.SE_MENU_ERROR)
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

    private fun tryToDropItem() {
        inventoryUI.getItemsToDrop()
            ?.let {
                playSe(AudioEvent.SE_DROP)
                worldScreen.dropItems(it)
            } ?: playSe(AudioEvent.SE_MENU_ERROR)
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
        inventoryUI.toggleTooltip()
    }

    private fun toggleCompare() {
        inventoryUI.toggleCompare()
    }

    private fun cheatAddGold() {
        if (preferenceManager.isDebugModeOn) {
            val inventory = gameData.inventory
            if (inventory.hasRoomForResource("gold")
                && inventory.hasRoomForResource("herb")
                && inventory.hasRoomForResource("spice")
                && inventory.hasRoomForResource("gemstone")
            ) {
                playSe(AudioEvent.SE_MENU_ERROR)
                gameData.party.gainXp(100)
                inventory.autoSetItem(InventoryDatabase.createInventoryItem("gold", 100))
                inventory.autoSetItem(InventoryDatabase.createInventoryItem("herb", 100))
                inventory.autoSetItem(InventoryDatabase.createInventoryItem("spice", 100))
                inventory.autoSetItem(InventoryDatabase.createInventoryItem("gemstone", 100))
                inventoryUI.reloadInventory()
            }
        }
    }

    private fun cheatRemoveGold() {
        if (preferenceManager.isDebugModeOn) {
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
