package nl.t64.cot.screens.battle.scene

import com.fasterxml.jackson.annotation.JsonProperty


class AnimationCell(
    @JsonProperty("spriteCellIndex")
    val index: Int = 0,
    val x: Float = 0f,
    val y: Float = 0f,
    val scale: Float = 1f,
    val rotation: Float = 0f,
    val mirror: Boolean = false,
    val opacity: Float = 1f,
    val blend: Int = 0
)
