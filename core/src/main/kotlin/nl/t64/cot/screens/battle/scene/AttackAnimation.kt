package nl.t64.cot.screens.battle.scene

import com.fasterxml.jackson.annotation.JsonProperty


class AttackAnimation(
    val spriteName: String = "",
    @JsonProperty("frameDuration")
    val frameDurationInSeconds: Float = 4f / 60f,
    val frames: List<List<AnimationCell>> = emptyList(),
    val flashes: List<AnimationFlash> = emptyList()
)
