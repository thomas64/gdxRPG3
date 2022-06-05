package nl.t64.cot.subjects


class MessageSubject {

    private val observers: MutableList<MessageObserver> = ArrayList()

    fun addObserver(observer: MessageObserver) {
        observers.add(observer)
    }

    fun removeObserver(observer: MessageObserver) {
        observers.remove(observer)
    }

    fun removeAllObservers() {
        observers.clear()
    }

    fun notifyShowMessageTooltip(message: String) {
        observers.forEach { it.onNotifyShowMessageTooltip(message) }
    }

}
