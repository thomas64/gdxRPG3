package nl.t64.cot

import nl.t64.cot.subjects.*


class BrokerManager {

    val actionObservers = ActionSubject()
    val blockObservers = BlockSubject()
    val bumpObservers = BumpSubject()
    val detectionObservers = DetectionSubject()
    val collisionObservers = CollisionSubject()

    val profileObservers = ProfileSubject()

}
