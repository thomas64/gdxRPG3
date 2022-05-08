package nl.t64.cot.screens.world

import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.math.Vector2
import nl.t64.cot.Utils.brokerManager
import nl.t64.cot.Utils.gameData
import nl.t64.cot.components.loot.Loot
import nl.t64.cot.components.loot.Spoil
import nl.t64.cot.screens.world.entity.*
import nl.t64.cot.screens.world.entity.events.LoadEntityEvent
import nl.t64.cot.screens.world.mapobjects.GameMapSparkle


class LootLoader(private val currentMap: GameMap) {

    private val lootList: MutableList<Entity> = ArrayList()

    fun createLoot(): List<Entity> {
        loadSpoils()
        loadSparkles()
        loadChests()
        return ArrayList(lootList)
    }

    private fun loadSpoils() {
        gameData.spoils.getByMapId(currentMap.mapTitle)
            .filterValues { !it.loot.isTaken() }
            .forEach { loadSpoil(it) }
    }

    private fun loadSparkles() {
        currentMap.sparkles.forEach {
            val sparkle = gameData.loot.getLoot(it.name)
            if (!sparkle.isTaken()) {
                loadSparkle(it, sparkle)
            }
        }
    }

    private fun loadChests() {
        currentMap.chests.forEach {
            val chest = gameData.loot.getLoot(it.name)
            loadChest(it, chest)
        }
    }

    private fun loadSpoil(spoil: Map.Entry<String, Spoil>) {
        val entity = Entity(spoil.key, InputEmpty(), PhysicsSparkle(spoil.value.loot), GraphicsSparkle(AnimationType.SHORT))
        lootList.add(entity)
        brokerManager.actionObservers.addObserver(entity)
        val position = Vector2(spoil.value.x, spoil.value.y)
        entity.send(LoadEntityEvent(position))
    }

    private fun loadSparkle(gameMapSparkle: GameMapSparkle, sparkle: Loot) {
        val entity = Entity(gameMapSparkle.name, InputEmpty(), PhysicsSparkle(sparkle), GraphicsSparkle(gameMapSparkle.animationType))
        lootList.add(entity)
        brokerManager.actionObservers.addObserver(entity)
        val position = Vector2(gameMapSparkle.rectangle.x, gameMapSparkle.rectangle.y)
        entity.send(LoadEntityEvent(position))
    }

    private fun loadChest(gameMapChest: RectangleMapObject, chest: Loot) {
        val entity = Entity(gameMapChest.name, InputEmpty(), PhysicsChest(chest), GraphicsChest())
        lootList.add(entity)
        brokerManager.actionObservers.addObserver(entity)
        brokerManager.blockObservers.addObserver(entity)
        val entityState = if (chest.isTaken()) EntityState.OPENED else EntityState.IMMOBILE
        val position = Vector2(gameMapChest.rectangle.x, gameMapChest.rectangle.y)
        entity.send(LoadEntityEvent(entityState, position))
    }

}
