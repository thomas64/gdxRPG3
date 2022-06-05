package nl.t64.cot

import nl.t64.cot.subjects.*


class BrokerManager {

    val actionObservers = ActionSubject()
    val blockObservers = BlockSubject()
    val bumpObservers = BumpSubject()
    val detectionObservers = DetectionSubject()
    val collisionObservers = CollisionSubject()

    val componentObservers = ComponentSubject()
    val lootObservers = LootSubject()
    val mapObservers = MapSubject()
    val entityObservers = EntitySubject()
    val messageObservers = MessageSubject()

    val profileObservers = ProfileSubject()

}
