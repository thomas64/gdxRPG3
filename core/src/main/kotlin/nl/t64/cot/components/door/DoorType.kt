package nl.t64.cot.components.door

import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.constants.Constant


enum class DoorType(val width: Float, val height: Float, val audioEvent: AudioEvent) {

    SMALL(Constant.TILE_SIZE, Constant.TILE_SIZE * 2f, AudioEvent.SE_SMALL_DOOR),
    LARGE(Constant.TILE_SIZE * 3f, Constant.TILE_SIZE * 2f, AudioEvent.SE_LARGE_DOOR),
    GATE(Constant.TILE_SIZE, Constant.TILE_SIZE * 2f, AudioEvent.SE_METAL_GATE),
    LARGE_WOODEN_CAVE_GATE(Constant.TILE_SIZE * 3f, Constant.TILE_SIZE * 2f, AudioEvent.SE_WOODEN_GATE),
    SMALL_HIDDEN_CAVE_DOOR(Constant.TILE_SIZE, Constant.TILE_SIZE * 2f, AudioEvent.SE_WOODEN_GATE)

}
