package nl.t64.cot.screens.battle.scene

import com.fasterxml.jackson.annotation.JsonProperty


// most animations use the default frame duration. the elements have their own in the json,
// so that they last equally long, while their number of frames differs.
class AttackAnimation(
    val spriteName: String = "",
    @JsonProperty("frameDuration")
    val frameDurationInSeconds: Float = 4f / 60f,
    val frames: List<List<AnimationCell>> = emptyList(),
    val flashes: List<AnimationFlash> = emptyList()
)
