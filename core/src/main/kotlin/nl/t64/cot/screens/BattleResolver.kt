package nl.t64.cot.screens

import com.badlogic.gdx.math.Vector2
import nl.t64.cot.Utils.brokerManager
import nl.t64.cot.Utils.gameData
import nl.t64.cot.Utils.mapManager
import nl.t64.cot.Utils.worldScreen
import nl.t64.cot.components.loot.Loot
import nl.t64.cot.components.loot.Spoil
import nl.t64.cot.screens.loot.SpoilsScreen
import nl.t64.cot.screens.world.entity.Entity


class BattleResolver private constructor(
    private val battleId: String,
    private val spoils: Loot,
    private val playerPosition: Vector2,
    private val currentNpcEntity: Entity,
    private val npcEntities: List<Entity>
) {

    companion object {
        fun resolveWin(
            battleId: String, spoils: Loot, playerPosition: Vector2, currentNpcEntity: Entity, npcEntities: List<Entity>
        ) {
            BattleResolver(battleId, spoils, playerPosition, currentNpcEntity, npcEntities).battleWon()
        }
    }

    private fun battleWon() {
        setPossibleKillTaskComplete()
        updateEntities()
        loadPossibleSpoils()
    }

    private fun setPossibleKillTaskComplete() {
        if (gameData.quests.contains(battleId)) {
            gameData.quests.getQuestById(battleId).setKillTaskComplete()
        }
    }

    private fun updateEntities() {
        val newNpcEntities = refreshNpcEntitiesListAfterBattle()
        worldScreen.updateNpcs(newNpcEntities)
        worldScreen.updateParty()
    }

    private fun loadPossibleSpoils() {
        if (!spoils.isTaken()) {
            loadSpoilsDialog()
        }
    }

    private fun loadSpoilsDialog() {
        val spoil = Spoil(mapManager.currentMap.mapTitle, playerPosition.x, playerPosition.y, spoils)
        gameData.spoils.addSpoil(battleId, spoil)
        SpoilsScreen.load(spoils)
    }

    private fun refreshNpcEntitiesListAfterBattle(): List<Entity> {
        return when (currentNpcEntity.isNpc()) {
            true -> getRefreshedListAfterConversationBattle()
            false -> getRefreshedListAfterNormalBattle()
        }
    }

    private fun getRefreshedListAfterNormalBattle(): List<Entity> {
        return npcEntities.filter { it != currentNpcEntity }
    }

    private fun getRefreshedListAfterConversationBattle(): List<Entity> {
        val remainingNpcEntities = mutableListOf<Entity>()
        npcEntities.forEach { it.removeFromBlockersOrAddTo(remainingNpcEntities) }
        return remainingNpcEntities
    }

    private fun Entity.removeFromBlockersOrAddTo(remainingNpcEntities: MutableList<Entity>) {
        if (isNpc() && getConversationId() == battleId) {
            brokerManager.blockObservers.removeObserver(this)
        } else {
            remainingNpcEntities.add(this)
        }
    }

}
