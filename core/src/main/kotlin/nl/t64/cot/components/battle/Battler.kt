package nl.t64.cot.components.battle


class Battler(
    private val id: String = "",
    private val amount: Int = 0
) {

    fun createEnemyList(): List<EnemyItem> {
        return List(amount) { EnemyDatabase.createEnemy(id) }
    }

}
