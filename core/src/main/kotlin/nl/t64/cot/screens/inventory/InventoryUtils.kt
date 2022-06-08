package nl.t64.cot.screens.inventory

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.Window
import nl.t64.cot.Utils
import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.screenManager
import nl.t64.cot.components.party.HeroItem
import nl.t64.cot.screens.ScreenUI


private const val SPRITE_BORDER_TOP_GRAY = "sprites/border_top_gray.png"

object InventoryUtils {

    private var selectedHero: HeroItem? = null

    fun getSelectedHero(): HeroItem {
        val party = gameData.party
        if (selectedHero == null || !party.containsExactlyEqualTo(selectedHero!!)) {
            selectedHero = party.getHero(0)
        }
        return selectedHero!!
    }

    fun selectPreviousHero() {
        selectedHero = gameData.party.getPreviousHero(selectedHero!!)
    }

    fun selectNextHero() {
        selectedHero = gameData.party.getNextHero(selectedHero!!)
    }

    fun setWindowDeselected(container: Table) {
        val parent = container.parent as Window
        parent.titleTable.background = Utils.createDrawable(Color.CLEAR)
    }

    fun setWindowSelected(container: Table) {
        val parent = container.parent as Window
        parent.titleTable.background = Utils.createTopBorder(SPRITE_BORDER_TOP_GRAY)
        parent.titleTable.padTop(-1f)
    }

    fun getScreenUI(): ScreenUI = screenManager.getCurrentParchmentScreen().getScreenUI()

}
