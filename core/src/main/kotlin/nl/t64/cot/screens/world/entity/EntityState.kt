package nl.t64.cot.screens.world.entity

import com.badlogic.gdx.math.MathUtils


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
            val randomNumber = MathUtils.random(getOnlyIdleAndWalking())
            return values()[randomNumber]
        }

        private fun getOnlyIdleAndWalking(): Int = values().size - (values().size - 1)
    }

}
