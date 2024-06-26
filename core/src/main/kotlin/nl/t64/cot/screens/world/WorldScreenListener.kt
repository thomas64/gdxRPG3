package nl.t64.cot.screens.world

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.preferenceManager
import nl.t64.cot.constants.Constant
import nl.t64.cot.screens.help.HelpScreen
import nl.t64.cot.screens.inventory.InventoryScreen
import nl.t64.cot.screens.menu.MenuPause
import nl.t64.cot.screens.questlog.QuestLogScreen


internal class WorldScreenListener(
    private val isInTransition: () -> Boolean,
    private val doBeforeLoadScreen: () -> Unit,
    private val showHidePartyWindowFunction: () -> Unit,
    private val openMiniMap: () -> Unit,
    private val setShowGrid: () -> Unit,
    private val setShowObjects: () -> Unit,
    private val setShowDebug: () -> Unit
) : InputAdapter() {

    override fun keyDown(keycode: Int): Boolean {
        if (isInTransition.invoke()) return false

        if (preferenceManager.isInDebugMode) {
            if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)
                || (Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT))
            ) {
                if (Gdx.input.isKeyPressed(Input.Keys.NUM_9)) {
                    gameData.clock.halfHourBack()
                    return false
                }
                if (Gdx.input.isKeyPressed(Input.Keys.NUM_0)) {
                    gameData.clock.halfHourForward()
                    return false
                }
            }
            when (keycode) {
                Input.Keys.F10 -> setShowGrid.invoke()
                Input.Keys.F11 -> setShowObjects.invoke()
                Input.Keys.F12 -> setShowDebug.invoke()
                Input.Keys.NUM_9 -> gameData.clock.fiveMinutesBack()
                Input.Keys.NUM_0 -> gameData.clock.fiveMinutesForward()
                Input.Keys.EQUALS -> gameData.clock.start()
                Input.Keys.MINUS -> gameData.clock.stop()
            }
        }

        when (keycode) {
            Constant.KEYCODE_START, Input.Keys.ESCAPE,
            Constant.KEYCODE_TOP, Input.Keys.I,
            Constant.KEYCODE_LEFT, Input.Keys.L,
            Constant.KEYCODE_L3, Input.Keys.H -> doBeforeLoadScreen.invoke()
        }

        when (keycode) {
            Constant.KEYCODE_SELECT, Input.Keys.M -> openMiniMap.invoke()
            Constant.KEYCODE_R3, Input.Keys.P -> showHidePartyWindowFunction.invoke()

            Constant.KEYCODE_START, Input.Keys.ESCAPE -> MenuPause.load()
            Constant.KEYCODE_TOP, Input.Keys.I -> InventoryScreen.load()
            Constant.KEYCODE_LEFT, Input.Keys.L -> QuestLogScreen.load()
            Constant.KEYCODE_L3, Input.Keys.H -> HelpScreen.load()

        }

        return false
    }

}
