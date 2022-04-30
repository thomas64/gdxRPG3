package nl.t64.cot.screens.storage

import com.badlogic.gdx.scenes.scene2d.Stage
import nl.t64.cot.Utils
import nl.t64.cot.screens.inventory.ButtonLabels


internal class StorageButtonLabels(stage: Stage) : ButtonLabels(stage) {

    override fun createBottomLeftText(): String {
        return if (Utils.isGamepadConnected()) {
            "Take:     [A] One      [X] Half      [Y] Full     |     [R3] Sort inventory"
        } else {
            "Take:     [A] One      [S] Half      [D] Full     |     [Space] Sort inventory"
        }
    }

    override fun createBottomRightText(): String {
        return if (Utils.isGamepadConnected()) {
            "[Select] Toggle tooltip      [L3] Toggle compare      [B] Back                                                                        [Start] De/Equip"
        } else {
            "[T] Toggle tooltip      [C] Toggle compare      [Esc] Back                                                                        [E] De/Equip"
        }
    }

}
