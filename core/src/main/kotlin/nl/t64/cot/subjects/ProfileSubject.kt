package nl.t64.cot.subjects

import com.badlogic.gdx.math.Vector2
import nl.t64.cot.gamestate.ProfileManager


class ProfileSubject {

    private val observers: MutableList<ProfileObserver> = ArrayList()

    fun addObserver(observer: ProfileObserver) {
        observers.add(observer)
    }

    fun removeObserver(observer: ProfileObserver) {
        observers.remove(observer)
    }

    fun notifyCreateProfile(profileManager: ProfileManager) {
        observers.forEach { it.onNotifyCreateProfile(profileManager) }
    }

    fun notifySaveProfileAfterBattle(profileManager: ProfileManager, playerPosition: Vector2) {
        observers.forEach { it.onNotifySaveProfileAfterBattle(profileManager, playerPosition) }
    }

    fun notifySaveProfile(profileManager: ProfileManager) {
        observers.forEach { it.onNotifySaveProfile(profileManager) }
    }

    fun notifyLoadProfile(profileManager: ProfileManager) {
        observers.forEach { it.onNotifyLoadProfile(profileManager) }
    }

}
