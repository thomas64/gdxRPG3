package nl.t64.cot.components.battle

import nl.t64.cot.components.party.inventory.InventoryGroup


class BattleField {

    val heroSpaces: MutableList<Character?> = MutableList(20) { null }
    val enemySpaces: MutableList<Character?> = MutableList(20) { null }


    fun clear() {
        heroSpaces.fill(null)
        enemySpaces.fill(null)
    }

    fun setPositionsHeroes(heroes: List<Character>) {
        val indices: List<Int> = (0 until 8).shuffled()
        heroes.forEachIndexed { index, hero -> heroSpaces[indices[index]] = hero }
    }

    fun setPositionsEnemies(enemies: List<Character>) {
        val indices: List<Int> = (12 until 20).shuffled()
        enemies.forEachIndexed { index, enemy -> enemySpaces[indices[index]] = enemy }
    }

    fun getIndexOf(character: Character): Int {
        return heroSpaces.indexOf(character)
            .takeUnless { it == -1 }
            ?: enemySpaces.indexOf(character)
    }

    fun moveCharacter(character: Character, newSpace: Int) {
        heroSpaces[heroSpaces.indexOf(character)] = null
        heroSpaces[newSpace] = character
    }

    fun getTargetableEnemiesFor(currentParticipant: Participant): List<Character> {
        return enemySpaces
            .filterNotNull()
            .filter { it.isInRangeOf(currentParticipant) }
    }

    private fun Character.isInRangeOf(currentParticipant: Participant): Boolean {
        val enemySpace: Int = enemySpaces.indexOf(this)
        val heroSpace: Int = heroSpaces.indexOf(currentParticipant.character)
        val weaponRange: Int = currentParticipant.getWeaponRange()
        return enemySpace == heroSpace + weaponRange || enemySpace == heroSpace - weaponRange - 1
    }

    private fun Participant.getWeaponRange(): Int {
        val outOfRange = 21
        return this.character.getInventoryItem(InventoryGroup.WEAPON)?.getWeaponRange() ?: outOfRange
    }


}
