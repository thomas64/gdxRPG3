package nl.t64.cot.screens.shop

import com.badlogic.gdx.scenes.scene2d.actions.Actions
import nl.t64.cot.Utils.screenManager
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe
import nl.t64.cot.constants.ScreenType
import nl.t64.cot.screens.ParchmentScreen
import nl.t64.cot.screens.ScreenUI
import nl.t64.cot.screens.inventory.InventoryUtils


class ShopScreen : ParchmentScreen() {

    private lateinit var shopUI: ShopUI
    private lateinit var npcId: String
    private lateinit var shopId: String

    companion object {
        fun load(npcId: String, shopId: String) {
            playSe(AudioEvent.SE_SCROLL)
            val shopScreen = screenManager.getScreen(ScreenType.SHOP) as ShopScreen
            shopScreen.npcId = npcId
            shopScreen.shopId = shopId
            screenManager.openParchmentLoadScreen(ScreenType.SHOP)
        }
    }

    override fun getScreenUI(): ScreenUI {
        return shopUI
    }

    override fun show() {
        setInputProcessors(stage)
        addInputListenerWithSmallDelay()
        shopUI = ShopUI(stage, npcId, shopId)
        ShopButtonLabels(stage).create()
    }

    override fun render(dt: Float) {
        renderStage(dt)
        shopUI.update()
    }

    private fun addInputListenerWithSmallDelay() {
        stage.addAction(Actions.sequence(Actions.delay(.1f),
                                         Actions.addListener(ShopScreenListener({ closeScreen() },
                                                                                { takeOne() },
                                                                                { takeHalf() },
                                                                                { takeFull() },
                                                                                { equip() },
                                                                                { selectPreviousHero() },
                                                                                { selectNextHero() },
                                                                                { selectPreviousTable() },
                                                                                { selectNextTable() },
                                                                                { toggleTooltip() },
                                                                                { toggleCompare() }),
                                                             false)))
    }

    private fun takeOne() {
        shopUI.takeOne()
    }

    private fun takeHalf() {
        shopUI.takeHalf()
    }

    private fun takeFull() {
        shopUI.takeFull()
    }

    private fun equip() {
        shopUI.equip()
    }

    private fun selectPreviousHero() {
        playSe(AudioEvent.SE_MENU_CURSOR)
        shopUI.updateSelectedHero { InventoryUtils.selectPreviousHero() }
    }

    private fun selectNextHero() {
        playSe(AudioEvent.SE_MENU_CURSOR)
        shopUI.updateSelectedHero { InventoryUtils.selectNextHero() }
    }

    private fun selectPreviousTable() {
        playSe(AudioEvent.SE_MENU_CURSOR)
        shopUI.selectPreviousTable()
    }

    private fun selectNextTable() {
        playSe(AudioEvent.SE_MENU_CURSOR)
        shopUI.selectNextTable()
    }

    private fun toggleTooltip() {
        playSe(AudioEvent.SE_MENU_CONFIRM)
        shopUI.toggleTooltip()
    }

    private fun toggleCompare() {
        playSe(AudioEvent.SE_MENU_CONFIRM)
        shopUI.toggleCompare()
    }

}
