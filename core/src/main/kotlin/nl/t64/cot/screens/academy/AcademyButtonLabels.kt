package nl.t64.cot.screens.academy

import com.badlogic.gdx.scenes.scene2d.Stage
import nl.t64.cot.Utils
import nl.t64.cot.screens.inventory.ButtonLabels


internal class AcademyButtonLabels(stage: Stage) : ButtonLabels(stage) {

    override fun createBottomLeftText(): String {
        return if (Utils.isGamepadConnected()) {
            "                                                       [A] Upgrade"
        } else {
            "                                                       [A] Upgrade"
        }
    }

    override fun createBottomRightText(): String {
        return if (Utils.isGamepadConnected()) {
            "               [Select] Toggle tooltip      [B] Back"
        } else {
            "               [T] Toggle tooltip      [Esc] Back"
        }
    }

}
