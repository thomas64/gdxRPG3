package nl.t64.cot.subjects

import nl.t64.cot.screens.world.entity.EntityState


interface DetectionObserver {

    fun onNotifyDetection(playerMoveSpeed: Float, playerState: EntityState)

}
