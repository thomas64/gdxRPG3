package nl.t64.cot.screens.mechanic

import com.badlogic.gdx.scenes.scene2d.Stage
import nl.t64.cot.Utils
import nl.t64.cot.screens.inventory.ButtonLabels


internal class MechanicButtonLabels(stage: Stage) : ButtonLabels(stage) {

    override fun createTopLeftText(): String {
        return ""
    }

    override fun createTopRightText(): String {
        return ""
    }

    override fun createBottomLeftText(): String {
        return ""
    }

    override fun createBottomRightText(): String {
        return if (Utils.isGamepadConnected()) {
            "               [L-Stick] Toggle tooltip      [B] Back"
        } else {
            "               [T] Toggle tooltip      [Esc] Back"
        }
    }

}
