package nl.t64.cot.subjects

import nl.t64.cot.ProfileManager


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

    fun notifySaveProfile(profileManager: ProfileManager) {
        observers.forEach { it.onNotifySaveProfile(profileManager) }
    }

    fun notifyLoadProfile(profileManager: ProfileManager) {
        observers.forEach { it.onNotifyLoadProfile(profileManager) }
    }

}
