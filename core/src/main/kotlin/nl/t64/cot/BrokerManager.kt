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
    val battleObservers = BattleSubject()
    val mapObservers = MapSubject()

    val partyObservers = PartySubject()
    val profileObservers = ProfileSubject()

}
