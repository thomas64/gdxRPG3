package nl.t64.cot.subjects

import nl.t64.cot.ProfileManager


interface ProfileObserver {

    fun onNotifyCreateProfile(profileManager: ProfileManager)
    fun onNotifySaveProfile(profileManager: ProfileManager)
    fun onNotifyLoadProfile(profileManager: ProfileManager)

}
