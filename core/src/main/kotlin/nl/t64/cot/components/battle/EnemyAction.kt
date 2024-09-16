package nl.t64.cot.components.battle


class EnemyAction(
    private val battleField: BattleField,
    private val currentParticipant: Participant
) {
    private val attacker: Character = currentParticipant.character

    fun handle(): ArrayDeque<String> {
        // todo, alles naar battleField verplaatsen?
        val attackPoints: List<Int> = battleField.getRangeOfEnemy(currentParticipant)
        val nearestHeroSpace: Int = battleField.getNearestHeroIndexFrom(attackPoints)
        val hero: Participant = battleField.heroSpaces[nearestHeroSpace]!!
        return ArrayDeque(listOf("The nearest hero is ${hero.character.name}."))
    }

}
