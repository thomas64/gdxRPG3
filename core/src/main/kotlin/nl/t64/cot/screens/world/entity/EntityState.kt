package nl.t64.cot.screens.world.entity

import kotlin.random.Random


enum class EntityState {
    IDLE,
    WALKING,

    IDLE_ANIMATING,
    PLAYING,
    FLYING,
    IMMOBILE,
    ALIGNING,
    INVISIBLE,
    OPENED,
    CLOSING,
    CRAWLING,   // only for scheduled npc's.
    RUNNING;    // only for scheduled npc's and cutscenes.


    companion object {
        fun getRandomIdleOrWalking(): EntityState {
            val onlyIdleAndWalking = 2
            val randomNumber = Random.nextInt(onlyIdleAndWalking)
            return entries[randomNumber]
        }
    }

}
