package nl.t64.cot.screens.battle

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Table
import nl.t64.cot.components.battle.*
import nl.t64.cot.components.party.HeroItem

class BattleTableManager(
    private val stage: Stage,
    private val screenBuilder: BattleScreenBuilder,
    private val currentParticipant: () -> Participant
) {
    private val battleFieldBuilder = BattleFieldTableBuilder()

    private var heroTable: Table = Table()
    private var enemyTable: Table = Table()
    private var turnTable: Table = Table()
    var battleFieldTable: Table = Table()


    fun updateHeroTable(heroes: List<HeroItem>, getCurrentAp: (Character) -> Int) {
        heroTable.remove()
        heroTable = screenBuilder.createHeroTable(heroes, getCurrentAp)
        stage.addActor(heroTable)
    }

    fun updateEnemyTable(enemies: List<EnemyItem>) {
        enemyTable.remove()
        enemyTable = screenBuilder.createEnemyTable(enemies)
        stage.addActor(enemyTable)
    }

    fun updateTurnTable(turnManager: TurnManager) {
        turnTable.remove()
        turnTable = screenBuilder.createTurnTable(turnManager.participants)
        stage.addActor(turnTable)
    }

    fun updateBattleField(battleField: BattleField) {
        battleFieldTable.remove()
        battleField.removeDeadParticipants()
        battleFieldTable = battleFieldBuilder.createBattleFieldTable(battleField, currentParticipant.invoke())
        stage.addActor(battleFieldTable)
    }

}
