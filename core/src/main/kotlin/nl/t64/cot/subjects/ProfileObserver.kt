package nl.t64.cot.subjects

import com.badlogic.gdx.math.Vector2
import nl.t64.cot.gamestate.ProfileManager


interface ProfileObserver {

    fun onNotifyCreateProfile(profileManager: ProfileManager)
    fun onNotifySaveProfileAfterBattle(profileManager: ProfileManager, playerPosition: Vector2) {
        onNotifySaveProfile(profileManager)
    }

    fun onNotifySaveProfile(profileManager: ProfileManager)
    fun onNotifyLoadProfile(profileManager: ProfileManager)

}
