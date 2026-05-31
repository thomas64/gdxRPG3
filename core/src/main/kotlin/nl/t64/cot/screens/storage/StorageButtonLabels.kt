package nl.t64.cot.screens.storage

import com.badlogic.gdx.scenes.scene2d.Stage
import nl.t64.cot.Utils
import nl.t64.cot.screens.inventory.ButtonLabels


internal class StorageButtonLabels(stage: Stage) : ButtonLabels(stage) {

    override fun createBottomLeftText(): String {
        return if (Utils.isGamepadConnected()) {
            "Take:     [A] One      [X] Half      [Y] All     |     [Start] Sort container"
        } else {
            "Take:     [A] One      [S] Half      [D] All     |     [Space] Sort container"
        }
    }

    override fun createBottomRightText(): String {
        return if (Utils.isGamepadConnected()) {
            "[L-Stick] Toggle tooltip      [R-Stick] Toggle compare      [B] Back                                                                        [Select] De/Equip"
        } else {
            "[T] Toggle tooltip      [C] Toggle compare      [Esc] Back                                                                        [E] De/Equip"
        }
    }

}
