package nl.t64.cot.screens.battle.scene

import com.badlogic.gdx.graphics.Color


class AnimationFlash(
    val atAnimationFrame: Int = 0,
    val frameCount: Int = 0,
    val color: List<Float> = emptyList()
) {

    fun toColor(): Color {
        return Color(color[0], color[1], color[2], color[3])
    }

}
