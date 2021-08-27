package nl.t64.cot.screens.shop

import com.badlogic.gdx.scenes.scene2d.Stage
import nl.t64.cot.Utils
import nl.t64.cot.screens.inventory.ButtonLabels


internal class ShopButtonLabels(stage: Stage) : ButtonLabels(stage) {

    override fun createBottomLeftText(): String {
        return if (Utils.isGamepadConnected()) {
            "Buy / Sell:      [A] One      [X] Half      [Y] Full      |      [Start] De/Equip"
        } else {
            "Buy / Sell:      [A] One      [S] Half      [D] Full      |      [E] De/Equip"
        }
    }

    override fun createBottomRightText(): String {
        return if (Utils.isGamepadConnected()) {
            "[Select] Toggle tooltip      [L3] Toggle compare      [B] Back"
        } else {
            "[T] Toggle tooltip      [C] Toggle compare      [Esc] Back"
        }
    }

}
