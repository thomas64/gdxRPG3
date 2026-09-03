package nl.t64.cot.components.battle

import nl.t64.cot.components.party.skills.SkillItemId


const val BATTLE_FIELD_SIZE = 20

class BattleGrid(participants: List<Participant>) {

    val heroSpaces: MutableList<Participant?> = MutableList(BATTLE_FIELD_SIZE) { null }
    val enemySpaces: MutableList<Participant?> = MutableList(BATTLE_FIELD_SIZE) { null }
    var startingSpace: Int = -1; private set

    init {
        val heroParticipants: List<Participant> = participants.filter { it.isHero }
        val enemyParticipants: List<Participant> = participants.filterNot { it.isHero }

        val randomHeroes: List<Participant> = heroParticipants
            .shuffled()
        val orderedEnemies: List<Participant> = enemyParticipants
            .sortedBy { it.character.getCalculatedTotalSkillOf(SkillItemId.STEALTH) + (Math.random() * 10f) }

        val heroIndices: List<Int> = (0 until 12).shuffled().take(randomHeroes.size).sorted()
        val enemyIndices: List<Int> = (8 until BATTLE_FIELD_SIZE).shuffled().take(orderedEnemies.size).sorted()

        randomHeroes.forEachIndexed { index, participant -> heroSpaces[heroIndices[index]] = participant }
        orderedEnemies.forEachIndexed { index, participant -> enemySpaces[enemyIndices[index]] = participant }
    }

    fun resetStartingSpace() {
        startingSpace = -1
    }

    fun setStartingSpace(index: Int) {
        startingSpace = index
    }

    fun getEffectiveStartingSpace(currentIndex: Int): Int {
        return if (startingSpace == -1) currentIndex else startingSpace
    }

    fun getSpaceIndexOf(participant: Participant): Int {
        return heroSpaces.indexOf(participant)
            .takeUnless { it == -1 }
            ?: enemySpaces.indexOf(participant)
    }

    fun isEnemySpaceNextToHero(spaceIndex: Int): Boolean {
        return heroSpaces[spaceIndex] != null
            || (spaceIndex < BATTLE_FIELD_SIZE - 1 && heroSpaces[spaceIndex + 1] != null)
    }

    fun isHeroSpaceNextToEnemy(spaceIndex: Int): Boolean {
        return (spaceIndex > 0 && enemySpaces[spaceIndex - 1] != null)
            || enemySpaces[spaceIndex] != null
    }

    fun getRange(fromIndex: Int, offsetLeft: Int, offSetRight: Int, ranges: List<Int>): List<Int> {
        return ranges
            .map { listOf(fromIndex - it + offsetLeft, fromIndex + it + offSetRight) }
            .flatten()
            .filter { it in 0 until BATTLE_FIELD_SIZE }
            .distinct()
    }

    fun moveHeroToSpace(hero: Participant, newSpace: Int) {
        heroSpaces[heroSpaces.indexOf(hero)] = null
        heroSpaces[newSpace] = hero
    }

    fun moveEnemyToSpace(enemy: Participant, newSpace: Int) {
        enemySpaces[enemySpaces.indexOf(enemy)] = null
        enemySpaces[newSpace] = enemy
    }

    fun removeFledHero(hero: Participant) {
        heroSpaces[heroSpaces.indexOf(hero)] = null
    }

    fun removeDeadParticipants() {
        heroSpaces.removeDeadParticipants()
        enemySpaces.removeDeadParticipants()
    }

    private fun MutableList<Participant?>.removeDeadParticipants() {
        this.forEachIndexed { index, participant ->
            if (participant?.character?.isAlive == false) {
                this[index] = null
            }
        }
    }

}
