package nl.t64.cot.subjects


interface LootObserver {

    fun onNotifySpoilsUpdated()
    fun onNotifyLootTaken()
    fun onNotifyReceiveTaken()

}
