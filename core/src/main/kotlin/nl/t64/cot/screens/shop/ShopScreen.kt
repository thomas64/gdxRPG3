package nl.t64.cot.screens.shop

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.ScreenUtils
import nl.t64.cot.Utils
import nl.t64.cot.Utils.audioManager
import nl.t64.cot.Utils.screenManager
import nl.t64.cot.audio.AudioCommand
import nl.t64.cot.audio.AudioEvent
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
            audioManager.handle(AudioCommand.SE_PLAY_ONCE, AudioEvent.SE_SCROLL)
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
        Gdx.input.inputProcessor = stage
        Utils.setGamepadInputProcessor(stage)
        stage.addListener(ShopScreenListener({ closeScreen() },
                                             { takeOne() },
                                             { takeHalf() },
                                             { takeFull() },
                                             { equip() },
                                             { selectPreviousHero() },
                                             { selectNextHero() },
                                             { selectPreviousTable() },
                                             { selectNextTable() },
                                             { toggleTooltip() },
                                             { toggleCompare() }))

        shopUI = ShopUI(stage, npcId, shopId)
        ShopButtonLabels(stage).create()
    }

    override fun render(dt: Float) {
        ScreenUtils.clear(Color.BLACK)
        stage.act(dt)
        stage.draw()
        shopUI.update()
    }

    override fun hide() {
        shopUI.unloadAssets()
        stage.clear()
    }

    override fun dispose() {
        stage.dispose()
    }

    private fun closeScreen() {
        Gdx.input.inputProcessor = null
        Utils.setGamepadInputProcessor(null)
        audioManager.handle(AudioCommand.SE_PLAY_ONCE, AudioEvent.SE_SCROLL)
        fadeParchment()
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
        audioManager.handle(AudioCommand.SE_PLAY_ONCE, AudioEvent.SE_MENU_CURSOR)
        shopUI.updateSelectedHero { InventoryUtils.selectPreviousHero() }
    }

    private fun selectNextHero() {
        audioManager.handle(AudioCommand.SE_PLAY_ONCE, AudioEvent.SE_MENU_CURSOR)
        shopUI.updateSelectedHero { InventoryUtils.selectNextHero() }
    }

    private fun selectPreviousTable() {
        audioManager.handle(AudioCommand.SE_PLAY_ONCE, AudioEvent.SE_MENU_CURSOR)
        shopUI.selectPreviousTable()
    }

    private fun selectNextTable() {
        audioManager.handle(AudioCommand.SE_PLAY_ONCE, AudioEvent.SE_MENU_CURSOR)
        shopUI.selectNextTable()
    }

    private fun toggleTooltip() {
        audioManager.handle(AudioCommand.SE_PLAY_ONCE, AudioEvent.SE_MENU_CONFIRM)
        shopUI.toggleTooltip()
    }

    private fun toggleCompare() {
        audioManager.handle(AudioCommand.SE_PLAY_ONCE, AudioEvent.SE_MENU_CONFIRM)
        shopUI.toggleCompare()
    }

}
