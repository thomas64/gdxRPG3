package nl.t64.cot.screens.world.loaders

import nl.t64.cot.Utils.brokerManager
import nl.t64.cot.Utils.gameData
import nl.t64.cot.components.party.HeroItem
import nl.t64.cot.components.party.PartyContainer
import nl.t64.cot.screens.world.entity.*
import nl.t64.cot.screens.world.entity.events.LoadEntityEvent


internal class PartyMembersLoader(private val player: Entity) {

    private val partyMembers: MutableList<Entity> = ArrayList(PartyContainer.MAXIMUM - 1)

    fun loadPartyMembers(): List<Entity> {
        createPartyMembers()
        return ArrayList(partyMembers)
    }

    private fun createPartyMembers() {
        gameData.party.getAllHeroesAlive()
            .filter { !it.isPlayer }
            .map { createEntity(it) }
            .forEach { addToPartyMembers(it) }
    }

    private fun createEntity(hero: HeroItem): Entity {
        return Entity(hero.id, InputPartyMember(), PhysicsPartyMember(), GraphicsPartyMember(hero.id))
    }

    private fun addToPartyMembers(partyMember: Entity) {
        partyMembers.add(partyMember)
        brokerManager.detectionObservers.addObserver(partyMember)
        partyMember.send(LoadEntityEvent(EntityState.IDLE, player.direction, player.position.cpy()))
    }

}
