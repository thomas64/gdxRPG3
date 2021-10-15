package nl.t64.cot.screens.academy

import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.ui.Dialog
import nl.t64.cot.constants.Constant


class AcademyScreenListener(
    private val closeScreenFunction: () -> Unit,
    private val upgradeSkillFunction: () -> Unit,
    private val previousHeroFunction: () -> Unit,
    private val nextHeroFunction: () -> Unit,
    private val previousTableFunction: () -> Unit,
    private val nextTableFunction: () -> Unit,
    private val toggleTooltipFunction: () -> Unit,
) : InputListener() {

    override fun keyDown(event: InputEvent, keycode: Int): Boolean {
        if (event.stage.actors.items.any { it is Dialog }) {
            return true
        }
        when (keycode) {
            Constant.KEYCODE_RIGHT, Input.Keys.ESCAPE -> closeScreenFunction.invoke()
            Constant.KEYCODE_BOTTOM, Input.Keys.A -> upgradeSkillFunction.invoke()
            Constant.KEYCODE_L1, Input.Keys.Q -> previousHeroFunction.invoke()
            Constant.KEYCODE_R1, Input.Keys.W -> nextHeroFunction.invoke()
            Constant.KEYCODE_R3_L, Input.Keys.Z -> previousTableFunction.invoke()
            Constant.KEYCODE_R3_R, Input.Keys.X -> nextTableFunction.invoke()
            Constant.KEYCODE_SELECT, Input.Keys.T -> toggleTooltipFunction.invoke()
        }
        return true
    }

}
