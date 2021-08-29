package nl.t64.cot.subjects


class QuestSubject {

    private val observers: MutableList<QuestObserver> = ArrayList()

    fun addObserver(observer: QuestObserver) {
        observers.add(observer)
    }

    fun removeObserver(observer: QuestObserver) {
        observers.remove(observer)
    }

    fun removeAllObservers() {
        observers.clear()
    }

    fun notifyShowMessageTooltip(message: String) {
        observers.forEach { it.onNotifyShowMessageTooltip(message) }
    }

}
