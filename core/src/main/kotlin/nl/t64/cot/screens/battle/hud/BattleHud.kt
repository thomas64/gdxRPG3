package nl.t64.cot.screens.battle.hud

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Table
import nl.t64.cot.components.battle.*
import nl.t64.cot.components.party.HeroItem
import nl.t64.cot.screens.battle.BattleState

class BattleHud(
    private val stage: Stage,
    private val hudBuilder: BattleHudBuilder,
    private val currentParticipant: () -> Participant,
    private val battleState: BattleState
) {
    private val battleFieldBuilder = BattleFieldTableBuilder()

    private var heroTable: Table = Table()
    private var enemyTable: Table = Table()
    private var turnTable: Table = Table()
    var battleFieldTable: Table = Table()


    fun updateHeroTable(heroes: List<HeroItem>, getCurrentAp: (Character) -> Int) {
        heroTable.remove()
        heroTable = hudBuilder.createHeroTable(heroes, getCurrentAp, currentParticipant.invoke().character.name)
        stage.addActor(heroTable)
    }

    fun updateEnemyTable(enemies: List<EnemyItem>, getCurrentAp: (Character) -> Int) {
        enemyTable.remove()
        enemyTable = hudBuilder.createEnemyTable(enemies, getCurrentAp, currentParticipant.invoke().character.name)
        stage.addActor(enemyTable)
    }

    fun updateTurnTable(turnManager: TurnManager, visionSlots: Int) {
        turnTable.remove()
        val forecast: List<Participant> = turnManager.simulateForecast(visionSlots)
        val enemyCountMap: Map<String, Int> = turnManager.getOnlyEnemies()
            .groupingBy { it.character.id }
            .eachCount()
        turnTable = hudBuilder.createTurnTable(forecast, enemyCountMap)
        stage.addActor(turnTable)
    }

    fun updateBattleField(battleField: BattleField) {
        battleFieldTable.remove()
        battleField.removeDeadParticipants()
        battleFieldTable = battleFieldBuilder.createBattleFieldTable(battleField, currentParticipant.invoke(), battleState.phase)
        stage.addActor(battleFieldTable)
    }

}
