package nl.t64.cot.subjects


class LootSubject {

    private val observers: MutableList<LootObserver> = ArrayList()

    fun addObserver(observer: LootObserver) {
        observers.add(observer)
    }

    fun removeObserver(observer: LootObserver) {
        observers.remove(observer)
    }

    fun removeAllObservers() {
        observers.clear()
    }

    fun notifySpoilsUpdated() {
        observers.forEach { it.onNotifySpoilsUpdated() }
    }

    fun notifyLootTaken() {
        observers.forEach { it.onNotifyLootTaken() }
    }

}
