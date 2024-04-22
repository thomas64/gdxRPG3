package nl.t64.cot.components.battle


class Battler(
    private val id: String = "",
    private val amount: Int = 0
) {

    fun createEnemyList(): List<EnemyItem> {
        return List(amount) { index ->
            if (amount > 1) {
                EnemyDatabase.createEnemy(id, index)
            } else {
                EnemyDatabase.createEnemy(id)
            }
        }
    }

}
