package nl.t64.cot.screens.warp

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.Window
import nl.t64.cot.Utils
import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.preferenceManager
import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe
import nl.t64.cot.screens.ParchmentScreen


abstract class PortalSelectionScreen(title: String) : ParchmentScreen() {

    protected val portalListTable: PortalListTable = PortalListTable()
    private val portalListWindow: Window = Utils.createDefaultWindow(title, portalListTable.container).apply {
        setPosition((Gdx.graphics.width / 2f) - (width / 2f), (Gdx.graphics.height / 2f) - (height / 2f))
    }

    protected abstract fun populateList()
    protected abstract fun onConfirm()
    protected abstract fun onClose()

    override fun show() {
        setInputProcessors(stage)
        stage.addActor(portalListWindow)
        stage.addListener(WarpScreenListener({ onConfirm() }, { cheat() }, { onClose() }))
        stage.keyboardFocus = portalListTable.portalList
        stage.scrollFocus = portalListTable.scrollPane
        populateList()
    }

    override fun render(dt: Float) {
        renderStage(dt)
    }

    private fun cheat() {
        if (preferenceManager.isDebugModeOn) {
            playSe(AudioEvent.SE_MENU_ERROR)
            val portals = gameData.portals
            portals.getAllIds().forEach { portals.activate(it) }
            populateList()
        }
    }

}
