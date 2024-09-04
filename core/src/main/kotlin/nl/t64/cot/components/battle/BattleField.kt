package nl.t64.cot.components.battle

import nl.t64.cot.audio.AudioEvent
import nl.t64.cot.audio.playSe
import nl.t64.cot.components.party.inventory.InventoryGroup


private const val BATTLE_FIELD_SIZE = 20

class BattleField(participants: List<Participant>) {

    val heroSpaces: MutableList<Participant?> = MutableList(BATTLE_FIELD_SIZE) { null }
    val enemySpaces: MutableList<Participant?> = MutableList(BATTLE_FIELD_SIZE) { null }
    var startingSpace: Int = -1

    init {
        val heroIndices: List<Int> = (0 until 8).shuffled()
        val enemyIndices: List<Int> = (12 until 20).shuffled()
        participants.forEachIndexed { index, participant ->
            if (participant.isHero) {
                heroSpaces[heroIndices[index]] = participant
            } else {
                enemySpaces[enemyIndices[index]] = participant
            }
        }
    }

    fun resetStartingSpace() {
        startingSpace = -1
    }

    fun setStartingSpace(currentParticipant: Participant) {
        startingSpace = getCurrentSpace(currentParticipant)
    }

    fun cancelMovement(participant: Participant) {
        if (startingSpace == -1) return
        participant.moveTo(startingSpace)
    }

    fun removeDeadParticipants() {
        heroSpaces.removeDeadParticipants()
        enemySpaces.removeDeadParticipants()
    }

    fun moveParticipantRight(currentParticipant: Participant) {
        val currentIndex = getCurrentSpace(currentParticipant)
        val upperBound = minOf(startingSpace + 1 + currentParticipant.currentAP, 20)
        (currentIndex + 1 until upperBound)
            .firstOrNull { heroSpaces[it] == null }
            ?.let {
                currentParticipant.moveTo(it)
                playSe(AudioEvent.SE_MENU_CURSOR)
            } ?: playSe(AudioEvent.SE_MENU_ERROR)
    }

    fun moveParticipantLeft(currentParticipant: Participant) {
        val currentIndex = getCurrentSpace(currentParticipant)
        val lowerBound = maxOf(startingSpace - currentParticipant.currentAP, 0)
        (currentIndex - 1 downTo lowerBound)
            .firstOrNull { heroSpaces[it] == null }
            ?.let {
                currentParticipant.moveTo(it)
                playSe(AudioEvent.SE_MENU_CURSOR)
            } ?: playSe(AudioEvent.SE_MENU_ERROR)
    }

    fun getCurrentSpace(participant: Participant): Int {
        return heroSpaces.indexOf(participant)
            .takeUnless { it == -1 }
            ?: enemySpaces.indexOf(participant)
    }

    fun getTargetableEnemiesFor(currentParticipant: Participant): List<Participant> {
        return enemySpaces
            .filterNotNull()
            .filter { it.isInRangeOf(currentParticipant) }
    }

    fun getRangeOf(currentParticipant: Participant): List<Int> {
        val heroSpace: Int = heroSpaces.indexOf(currentParticipant)
        return currentParticipant.character.getInventoryItem(InventoryGroup.WEAPON)
            ?.getWeaponRange()
            ?.map { listOf(heroSpace - it, heroSpace + it - 1) }
            ?.flatten()
            ?.filter { it in 0 until BATTLE_FIELD_SIZE }
            ?.distinct()
            ?: emptyList()
    }

    private fun Participant.moveTo(newSpace: Int) {
        heroSpaces[heroSpaces.indexOf(this)] = null
        heroSpaces[newSpace] = this
    }

    private fun Participant.isInRangeOf(currentParticipant: Participant): Boolean {
        val enemySpace: Int = enemySpaces.indexOf(this)
        val range: List<Int> = getRangeOf(currentParticipant)
        return enemySpace in range
    }

    private fun MutableList<Participant?>.removeDeadParticipants() {
        this.forEachIndexed { index, participant ->
            if (participant?.character?.isAlive == false) {
                this[index] = null
            }
        }
    }

}
