package nl.t64.cot.subjects


class DetectionSubject {

    private val observers: MutableList<DetectionObserver> = ArrayList()

    fun addObserver(observer: DetectionObserver) {
        observers.add(observer)
    }

    fun removeObserver(observer: DetectionObserver) {
        observers.remove(observer)
    }

    fun removeAllObservers() {
        observers.clear()
    }

    fun notifyDetection(playerMoveSpeed: Float) {
        ArrayList(observers).forEach { it.onNotifyDetection(playerMoveSpeed) }
    }

}
