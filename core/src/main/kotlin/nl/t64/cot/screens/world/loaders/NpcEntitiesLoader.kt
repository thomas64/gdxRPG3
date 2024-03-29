package nl.t64.cot.screens.world.loaders

import nl.t64.cot.Utils.brokerManager
import nl.t64.cot.Utils.gameData
import nl.t64.cot.components.battle.EnemyContainer
import nl.t64.cot.screens.world.entity.*
import nl.t64.cot.screens.world.entity.events.LoadEntityEvent
import nl.t64.cot.screens.world.map.GameMap
import nl.t64.cot.screens.world.mapobjects.GameMapEnemy
import nl.t64.cot.screens.world.mapobjects.GameMapHero
import nl.t64.cot.screens.world.mapobjects.GameMapNpc


internal class NpcEntitiesLoader(private val currentMap: GameMap) {

    private val npcEntities: MutableList<Entity> = ArrayList()

    fun createNpcs(): List<Entity> {
        loadNpcs()
        loadHeroes()
        loadEnemies()
        return ArrayList(npcEntities)
    }

    private fun loadNpcs() {
        currentMap.npcs
            .filter { it.isMeetingConditions() }
            .filterNot { it.isEnemy && gameData.battles.isBattleWon(it.conversation) }
            .forEach { loadNpcEntity(it) }
    }

    private fun loadHeroes() {
        currentMap.heroes
            .filter { it.isMeetingConditions() }
            .forEach { loadHero(it) }
    }

    private fun loadEnemies() {
        currentMap.enemies
            .filter { it.isMeetingConditions() }
            .filterNot { gameData.battles.isBattleWon(it.battleId) }
            .forEach { loadEnemy(it) }
    }

    private fun loadHero(gameMapHero: GameMapHero) {
        val hero = gameData.heroes.getCertainHero(gameMapHero.name)
        if (hero.isAlive && gameMapHero.hasBeenRecruited == hero.hasBeenRecruited) {
            loadNpcEntity(gameMapHero)
        }
    }

    private fun loadEnemy(gameMapEnemy: GameMapEnemy) {
        val entityId = gameMapEnemy.name
        val enemyEntity = Entity(entityId, InputEnemy(), PhysicsEnemy(), GraphicsEnemy(entityId))
        npcEntities.add(enemyEntity)
        addObserversForEnemy(gameMapEnemy, enemyEntity)
        enemyEntity.send(LoadEntityEvent(gameMapEnemy.state,
                                         gameMapEnemy.direction,
                                         gameMapEnemy.position,
                                         gameMapEnemy.battleId))
    }

    private fun addObserversForEnemy(gameMapEnemy: GameMapEnemy, enemyEntity: Entity) {
        if (gameMapEnemy.state == EntityState.IMMOBILE) {
            brokerManager.actionObservers.addObserver(enemyEntity)
        } else {
            if (EnemyContainer(gameMapEnemy.battleId).doEnemiesWantToBattle()) {
                brokerManager.detectionObservers.addObserver(enemyEntity)
            }
            brokerManager.bumpObservers.addObserver(enemyEntity)
        }
    }

    private fun loadNpcEntity(gameMapNpc: GameMapNpc) {
        val entityId = gameMapNpc.name
        val npcEntity = Entity(entityId, InputNpc(), PhysicsNpc(), GraphicsNpc(entityId))
        npcEntities.add(npcEntity)
        addObserversForNpc(npcEntity)
        npcEntity.send(LoadEntityEvent(gameMapNpc.state,
                                       gameMapNpc.direction,
                                       gameMapNpc.position,
                                       gameMapNpc.conversation))
    }

    private fun addObserversForNpc(npcEntity: Entity) {
        brokerManager.actionObservers.addObserver(npcEntity)
        brokerManager.blockObservers.addObserver(npcEntity)
        brokerManager.bumpObservers.addObserver(npcEntity)
    }

}
