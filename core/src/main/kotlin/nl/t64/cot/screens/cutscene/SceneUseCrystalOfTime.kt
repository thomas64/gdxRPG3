package nl.t64.cot.screens.cutscene

import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import nl.t64.cot.screens.inventory.InventoryScreen


class SceneUseCrystalOfTime : CutsceneScreen() {

    override fun prepare() {
        actions = listOf(openInventoryScreen())
    }

    private fun openInventoryScreen(): Action {
        return Actions.sequence(
            Actions.run {
                setMapWithBgsOnly("ylarus_place")
                setCameraPosition(0f, 0f)
            },
            Actions.run { exitScreen() }
        )
    }

    override fun exitScreen() {
        endCutsceneWithoutFadeAnd { InventoryScreen.loadForCutsceneTryCrystal() }
    }

}
