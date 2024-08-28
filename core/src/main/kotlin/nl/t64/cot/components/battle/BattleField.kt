package nl.t64.cot.components.battle


class BattleField {

    val heroSpaces: MutableList<Character?> = MutableList(20) { null }
    val enemySpaces: MutableList<Character?> = MutableList(20) { null }


    fun clear() {
        heroSpaces.fill(null)
        enemySpaces.fill(null)
    }

    fun positionHeroes(heroes: List<Character>) {
        val indices: List<Int> = (0 until 8).shuffled()
        heroes.forEachIndexed { index, hero -> heroSpaces[indices[index]] = hero }
    }

    fun positionEnemies(enemies: List<Character>) {
        val indices: List<Int> = (12 until 20).shuffled()
        enemies.forEachIndexed { index, enemy -> enemySpaces[indices[index]] = enemy }
    }

    fun moveCharacter(character: Character, newSpace: Int) {
        heroSpaces[heroSpaces.indexOf(character)] = null
        heroSpaces[newSpace] = character
    }

}
